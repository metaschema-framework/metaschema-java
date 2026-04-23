/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.MetapathException;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.constraint.IAllowedValuesConstraint;
import dev.metaschema.core.model.constraint.ILet;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A visitor that traverses a Metaschema module's node items and collects all
 * allowed-values constraints, organized by the target node they apply to.
 * <p>
 * This visitor extends {@link AbstractRecursionPreventingNodeItemVisitor} to
 * safely handle recursive assembly definitions without infinite looping.
 * <p>
 * Usage example:
 *
 * <pre>
 * AllowedValueCollectingNodeItemVisitor visitor
 *     = new AllowedValueCollectingNodeItemVisitor();
 * visitor.visit(module);
 * Collection&lt;NodeItemRecord&gt; locations = visitor.getAllowedValueLocations();
 * </pre>
 */
public class AllowedValueCollectingNodeItemVisitor
    extends AbstractRecursionPreventingNodeItemVisitor<DynamicContext, Void> {
  private static final Logger LOGGER = LogManager.getLogger(AllowedValueCollectingNodeItemVisitor.class);

  @NonNull
  private final Map<IDefinitionNodeItem<?, ?>, NodeItemRecord> nodeItemAnalysis = new LinkedHashMap<>();

  /**
   * Get the collected allowed-values constraint locations found during
   * visitation.
   *
   * @return a collection of records, each containing a definition node item and
   *         the allowed-values constraints that target it
   */
  @NonNull
  public Collection<NodeItemRecord> getAllowedValueLocations() {
    return nodeItemAnalysis.values();
  }

  /**
   * Visit all definitions in the provided module to collect allowed-values
   * constraints.
   * <p>
   * This method creates a new {@link DynamicContext} configured with the module's
   * default namespace.
   *
   * @param module
   *          the Metaschema module to visit
   */
  public void visit(@NonNull IModule module) {
    DynamicContext context = new DynamicContext(
        StaticContext.builder()
            .defaultModelNamespace(module.getXmlNamespace())
            .build());

    visit(INodeItemFactory.instance().newModuleNodeItem(module), context);
  }

  /**
   * Visit all definitions in the provided module node item using the given
   * dynamic context.
   * <p>
   * The provided context is configured for module-definition walking: predicate
   * evaluation is disabled and no-data atomization is treated as empty. These
   * settings are applied regardless of their state on the incoming context so
   * that expressions which reach instance-only functions (for example,
   * {@code fn:doc} via {@code oscal:resolve-reference(@href)}) degrade to empty
   * sequences rather than raising {@code InvalidTypeFunctionException} and
   * predicates on definitional targets do not attempt evaluation that requires
   * instance data.
   *
   * @param module
   *          the module node item to visit
   * @param context
   *          the dynamic context to use for constraint evaluation
   */
  public void visit(@NonNull IModuleNodeItem module, @NonNull DynamicContext context) {
    context.disablePredicateEvaluation();
    context.enableAtomizeNoDataAsEmpty();
    visitMetaschema(module, context);
  }

  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  private void handleAllowedValuesAtLocation(
      @NonNull IDefinitionNodeItem<?, ?> itemLocation,
      @NonNull DynamicContext context) {
    itemLocation.getDefinition().getAllowedValuesConstraints().stream()
        .forEachOrdered(allowedValues -> {
          try {
            ISequence<?> result = allowedValues.getTarget().evaluate(itemLocation, context);
            result.stream().forEachOrdered(target -> {
              assert target != null;
              handleAllowedValues(allowedValues, itemLocation, (IDefinitionNodeItem<?, ?>) target);
            });
          } catch (MetapathException ex) {
            LOGGER.atWarn().log(
                "Skipping allowed-values constraint target '{}' at '{}' because it cannot be evaluated"
                    + " against the module definition: {}",
                allowedValues.getTarget().getPath(), itemLocation.getMetapath(), ex.getLocalizedMessage());
          }
        });
  }

  private void handleAllowedValues(
      @NonNull IAllowedValuesConstraint allowedValues,
      @NonNull IDefinitionNodeItem<?, ?> location,
      @NonNull IDefinitionNodeItem<?, ?> target) {
    NodeItemRecord itemRecord = nodeItemAnalysis.get(target);
    if (itemRecord == null) {
      itemRecord = new NodeItemRecord(target);
      nodeItemAnalysis.put(target, itemRecord);
    }

    AllowedValuesRecord allowedValuesRecord = new AllowedValuesRecord(allowedValues, location, target);
    itemRecord.addAllowedValues(allowedValuesRecord);
  }

  @Override
  public Void visitFlag(IFlagNodeItem item, DynamicContext context) {
    assert context != null;
    DynamicContext subContext = handleLetStatements(item, context);
    handleAllowedValuesAtLocation(item, subContext);
    return super.visitFlag(item, subContext);
  }

  @Override
  public Void visitField(IFieldNodeItem item, DynamicContext context) {
    assert context != null;
    DynamicContext subContext = handleLetStatements(item, context);
    handleAllowedValuesAtLocation(item, subContext);
    return super.visitField(item, subContext);
  }

  @Override
  public Void visitAssembly(IAssemblyNodeItem item, DynamicContext context) {
    assert context != null;
    DynamicContext subContext = handleLetStatements(item, context);
    handleAllowedValuesAtLocation(item, subContext);
    return super.visitAssembly(item, subContext);
  }

  @SuppressWarnings("PMD.AssignmentInOperand")
  private DynamicContext handleLetStatements(IDefinitionNodeItem<?, ?> item, DynamicContext context) {
    assert context != null;
    DynamicContext subContext = context;
    for (ILet let : item.getDefinition().getLetExpressions().values()) {
      try {
        ISequence<?> result = let.getValueExpression().evaluate(item,
            subContext).reusable();
        subContext = subContext.bindVariableValue(let.getName(), result);
      } catch (MetapathException ex) {
        // Let expressions may reference runtime-only data (e.g. atomization of a
        // flag whose typed value is only available on an instance document) that
        // cannot be evaluated while walking the module definition. Skip binding
        // the variable; dependent expressions will be skipped downstream.
        LOGGER.atWarn().log(
            "Skipping let expression '${}' at '{}' because it cannot be evaluated against"
                + " the module definition: {}",
            let.getName(), item.getMetapath(), ex.getLocalizedMessage());
      }
    }
    return subContext;
  }

  @Override
  public Void visitAssembly(IAssemblyInstanceGroupedNodeItem item, DynamicContext context) {
    return visitAssembly((IAssemblyNodeItem) item, context);
  }

  @Override
  protected Void defaultResult() {
    return null;
  }

  /**
   * A record that associates a definition node item with all the allowed-values
   * constraints that target it.
   */
  public static final class NodeItemRecord {
    @NonNull
    private final IDefinitionNodeItem<?, ?> item;
    @NonNull
    private final List<AllowedValuesRecord> allowedValues = new LinkedList<>();

    private NodeItemRecord(@NonNull IDefinitionNodeItem<?, ?> item) {
      this.item = item;
    }

    /**
     * Get the definition node item that is targeted by the allowed-values
     * constraints.
     *
     * @return the target node item
     */
    @NonNull
    public IDefinitionNodeItem<?, ?> getItem() {
      return item;
    }

    /**
     * Get the list of allowed-values constraint records targeting this node item.
     *
     * @return the list of allowed-values records
     */
    @NonNull
    public List<AllowedValuesRecord> getAllowedValues() {
      return Collections.unmodifiableList(allowedValues);
    }

    /**
     * Add an allowed-values constraint record to this node item.
     *
     * @param record
     *          the allowed-values record to add
     */
    public void addAllowedValues(@NonNull AllowedValuesRecord record) {
      this.allowedValues.add(record);
    }
  }

  /**
   * A record capturing the relationship between an allowed-values constraint, the
   * definition where it is declared, and the target node it applies to.
   */
  public static final class AllowedValuesRecord {
    @NonNull
    private final IAllowedValuesConstraint allowedValues;
    @NonNull
    private final IDefinitionNodeItem<?, ?> location;
    @NonNull
    private final IDefinitionNodeItem<?, ?> target;

    /**
     * Construct a new allowed-values record.
     *
     * @param allowedValues
     *          the allowed-values constraint
     * @param location
     *          the definition node item where the constraint is declared
     * @param target
     *          the definition node item that the constraint targets
     */
    public AllowedValuesRecord(
        @NonNull IAllowedValuesConstraint allowedValues,
        @NonNull IDefinitionNodeItem<?, ?> location,
        @NonNull IDefinitionNodeItem<?, ?> target) {
      this.allowedValues = allowedValues;
      this.location = location;
      this.target = target;
    }

    /**
     * Get the allowed-values constraint.
     *
     * @return the allowed-values constraint
     */
    @NonNull
    public IAllowedValuesConstraint getAllowedValues() {
      return allowedValues;
    }

    /**
     * Get the definition node item where the constraint is declared.
     *
     * @return the location node item
     */
    @NonNull
    public IDefinitionNodeItem<?, ?> getLocation() {
      return location;
    }

    /**
     * Get the definition node item that the constraint targets.
     *
     * @return the target node item
     */
    @NonNull
    public IDefinitionNodeItem<?, ?> getTarget() {
      return target;
    }
  }
}
