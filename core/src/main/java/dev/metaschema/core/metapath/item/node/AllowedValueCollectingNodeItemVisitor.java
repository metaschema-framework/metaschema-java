/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.node;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IExpression;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.MetapathException;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.cst.AbstractExpressionVisitor;
import dev.metaschema.core.metapath.cst.VariableReference;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.constraint.IAllowedValuesConstraint;
import dev.metaschema.core.model.constraint.ILet;
import dev.metaschema.core.qname.IEnhancedQName;
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
   * The set of let variable names that are referenced, transitively, by any
   * allowed-values constraint target expression in the walked module. A let is
   * only evaluated at walk time if its name appears in this set. Lets that are
   * never read by an allowed-values constraint are skipped entirely to avoid
   * evaluating expressions that inherently require instance-level data (for
   * example, those calling {@code fn:doc} or {@code oscal:resolve-reference} on a
   * flag whose typed value only exists in an instance document).
   */
  @NonNull
  private Set<IEnhancedQName> referencedVariables = Collections.emptySet();

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
   * default namespace and with predicate evaluation disabled.
   *
   * @param module
   *          the Metaschema module to visit
   */
  public void visit(@NonNull IModule module) {
    DynamicContext context = new DynamicContext(
        StaticContext.builder()
            .defaultModelNamespace(module.getXmlNamespace())
            .build());
    context.disablePredicateEvaluation();

    visit(INodeItemFactory.instance().newModuleNodeItem(module), context);
  }

  /**
   * Visit all definitions in the provided module node item using the given
   * dynamic context.
   *
   * @param module
   *          the module node item to visit
   * @param context
   *          the dynamic context to use for constraint evaluation
   */
  public void visit(@NonNull IModuleNodeItem module, @NonNull DynamicContext context) {
    this.referencedVariables = computeReferencedVariables(module);
    visitMetaschema(module, context);
  }

  /**
   * Walk the module graph once to determine which let variable names are needed
   * by an allowed-values constraint target expression. The set is expanded
   * transitively so that, if a let's value expression references another let,
   * both are considered needed.
   *
   * @param module
   *          the module graph to scan
   * @return the set of needed variable names
   */
  @NonNull
  private static Set<IEnhancedQName> computeReferencedVariables(@NonNull IModuleNodeItem module) {
    Set<IEnhancedQName> needed = new HashSet<>();
    Map<IEnhancedQName, IMetapathExpression> allLets = new HashMap<>();

    ReferenceCollectingVisitor scanner = new ReferenceCollectingVisitor(needed, allLets);
    scanner.visitMetaschema(module, null);

    // Transitive closure: if a needed let's value expression references another
    // variable, that variable is also needed.
    boolean changed = true;
    while (changed) {
      changed = false;
      Set<IEnhancedQName> toAdd = new HashSet<>();
      for (IEnhancedQName name : needed) {
        IMetapathExpression expr = allLets.get(name);
        if (expr != null) {
          Set<IEnhancedQName> found = new HashSet<>();
          collectVariableReferences(expr, found);
          for (IEnhancedQName ref : found) {
            if (!needed.contains(ref)) {
              toAdd.add(ref);
            }
          }
        }
      }
      if (!toAdd.isEmpty()) {
        needed.addAll(toAdd);
        changed = true;
      }
    }
    return needed;
  }

  /**
   * Walk the AST of the provided compiled Metapath expression and append every
   * variable reference name into {@code sink}. Expressions that cannot be
   * compiled on demand (for lazy expressions) are treated as referencing no
   * variables, which conservatively lets them fail at evaluation time with the
   * existing diagnostic path instead of silently skipping the constraint.
   */
  private static void collectVariableReferences(
      @NonNull IMetapathExpression expression,
      @NonNull Set<IEnhancedQName> sink) {
    try {
      ((IExpression) expression).accept(
          new AbstractExpressionVisitor<Void, Set<IEnhancedQName>>() {
            @Override
            protected Void aggregateResult(Void existing, Void next, Set<IEnhancedQName> ctx) {
              return null;
            }

            @Override
            protected Void defaultResult() {
              return null;
            }

            @Override
            public Void visitVariableReference(VariableReference expr, Set<IEnhancedQName> ctx) {
              ctx.add(expr.getName());
              return null;
            }
          },
          sink);
    } catch (MetapathException ex) {
      // Compilation failed. Leave the variable set unchanged; the downstream
      // evaluation code path will surface the underlying problem with a
      // standard diagnostic.
    }
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
      if (!referencedVariables.contains(let.getName())) {
        // No allowed-values constraint (direct or transitive through another
        // let) references this variable. Skip binding so expressions that
        // cannot be evaluated against a module definition -- for example those
        // calling fn:doc, oscal:resolve-reference, or oscal:resolve-profile --
        // do not raise diagnostics when they would have no effect on the
        // collected allowed-values anyway.
        continue;
      }
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

  /**
   * A node-item visitor used as a one-time pre-pass to populate the set of let
   * variable names that are referenced by any allowed-values constraint target
   * expression in the module, along with the full table of let value expressions
   * for later transitive expansion.
   */
  private static final class ReferenceCollectingVisitor
      extends AbstractRecursionPreventingNodeItemVisitor<Void, Void> {
    @NonNull
    private final Set<IEnhancedQName> referenced;
    @NonNull
    private final Map<IEnhancedQName, IMetapathExpression> letExpressions;

    ReferenceCollectingVisitor(
        @NonNull Set<IEnhancedQName> referenced,
        @NonNull Map<IEnhancedQName, IMetapathExpression> letExpressions) {
      this.referenced = referenced;
      this.letExpressions = letExpressions;
    }

    private void collectFromDefinition(@NonNull IDefinitionNodeItem<?, ?> item) {
      for (ILet let : item.getDefinition().getLetExpressions().values()) {
        letExpressions.putIfAbsent(let.getName(), let.getValueExpression());
      }
      for (IAllowedValuesConstraint constraint : item.getDefinition().getAllowedValuesConstraints()) {
        collectVariableReferences(constraint.getTarget(), referenced);
      }
    }

    @Override
    public Void visitFlag(IFlagNodeItem item, Void context) {
      collectFromDefinition(item);
      return super.visitFlag(item, context);
    }

    @Override
    public Void visitField(IFieldNodeItem item, Void context) {
      collectFromDefinition(item);
      return super.visitField(item, context);
    }

    @Override
    public Void visitAssembly(IAssemblyNodeItem item, Void context) {
      collectFromDefinition(item);
      return super.visitAssembly(item, context);
    }

    @Override
    public Void visitAssembly(IAssemblyInstanceGroupedNodeItem item, Void context) {
      return visitAssembly((IAssemblyNodeItem) item, context);
    }

    @Override
    protected Void defaultResult() {
      return null;
    }
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
