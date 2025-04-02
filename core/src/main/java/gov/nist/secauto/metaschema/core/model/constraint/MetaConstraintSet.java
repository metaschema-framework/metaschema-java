/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelElementVisitor;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import nl.talsmasoftware.lazy4j.Lazy;

public class MetaConstraintSet
    extends AbstractConstraintSet {
  private static final Logger LOGGER = LogManager.getLogger(MetaConstraintSet.class);
  @NonNull
  private final List<? extends IConstraintSet> imports;
  @NonNull
  private final List<Context> contexts;

  public MetaConstraintSet(
      @NonNull ISource source,
      @NonNull List<? extends IConstraintSet> imports,
      @NonNull List<Context> contexts) {
    super(source);
    this.imports = imports;
    this.contexts = contexts;
  }

  @Override
  public Collection<? extends IConstraintSet> getImportedConstraintSets() {
    return imports;
  }

  @Override
  public Set<IDefinition> applyConstraintsForModule(
      IModuleNodeItem moduleItem,
      Set<IDefinition> previouslyTargetedDefinitions,
      IModelElementVisitor<ITargetedConstraints, Void> visitor) {

    Set<IDefinition> targetedDefinitions = new HashSet<>();
    for (IConstraintSet imported : imports) {
      targetedDefinitions.addAll(
          imported.applyConstraintsForModule(moduleItem, previouslyTargetedDefinitions, visitor));
    }

    // generate a dynamic context using the external constraint set's static context
    DynamicContext dynamicContext = new DynamicContext(getSource().getStaticContext());
    for (Context context : contexts) {
      targetedDefinitions.addAll(
          context.applyConstraintsForModule(
              moduleItem,
              previouslyTargetedDefinitions,
              dynamicContext,
              visitor,
              ISequence.of(moduleItem)));
    }
    return CollectionUtil.unmodifiableSet(targetedDefinitions);
  }

  @NonNull
  public static Stream<IMetapathExpression> concatMetapaths(
      @Nullable MetaConstraintSet.Context parent,
      @NonNull Stream<IMetapathExpression> metapaths,
      @NonNull ISource source) {

    Stream<IMetapathExpression> retval = metapaths;
    if (parent != null) {
      List<IMetapathExpression> parentMetapaths = parent.getMetapaths().stream()
          .collect(Collectors.toList());
      retval = metapaths
          .map(IMetapathExpression::getPath)
          .flatMap(childPath -> parentMetapaths.stream()
              .map(parentPath -> parentPath.getPath() + '/' + childPath))
          .map(metapath -> IMetapathExpression.lazyCompile(
              ObjectUtils.requireNonNull(metapath),
              source.getStaticContext()));
    }
    return ObjectUtils.notNull(retval);
  }

  public static class Context {
    @NonNull
    private final List<IMetapathExpression> metapaths;
    @NonNull
    private final Lazy<List<IMetapathExpression>> contextualizedMetapaths;
    @NonNull
    private final List<Context> childContexts = new LinkedList<>();
    @NonNull
    private final Lazy<List<ITargetedConstraints>> targetedConstraints;

    public Context(
        @Nullable Context parent,
        @NonNull ISource source,
        @NonNull List<IMetapathExpression> metapaths,
        @NonNull IModelConstrained constraints) {
      this.metapaths = metapaths;
      this.contextualizedMetapaths = ObjectUtils.notNull(Lazy.lazy(() -> {
        return concatMetapaths(parent, ObjectUtils.notNull(metapaths.stream()), source)
            .collect(Collectors.toUnmodifiableList());
      }));
      this.targetedConstraints = ObjectUtils.notNull(Lazy.lazy(() -> {
        return getMetapaths().stream()
            .map(metapath -> new ModelTargetedConstraints(
                source,
                this::getContextualizedMetapaths,
                constraints))
            .collect(Collectors.toUnmodifiableList());
      }));
    }

    @NonNull
    public List<ITargetedConstraints> getTargetedConstraints() {
      return ObjectUtils.notNull(targetedConstraints.get());
    }

    public void addAll(@NonNull Collection<Context> children) {
      childContexts.addAll(children);
    }

    @NonNull
    public List<IMetapathExpression> getMetapaths() {
      return metapaths;
    }

    public List<IMetapathExpression> getContextualizedMetapaths() {
      return ObjectUtils.notNull(contextualizedMetapaths.get());
    }

    Set<IDefinition> applyConstraintsForModule(
        @NonNull IModuleNodeItem moduleItem,
        @NonNull Set<IDefinition> previouslyTargetedDefinitions,
        @NonNull DynamicContext dynamicContext,
        @NonNull IModelElementVisitor<ITargetedConstraints, Void> visitor,
        @NonNull ISequence<? extends INodeItem> targetedItems) {
      Set<IDefinition> targetedDefinitions = new HashSet<>();

      for (INodeItem nodeItem : targetedItems) {
        for (IMetapathExpression metapath : metapaths) {
          ISequence<? extends IDefinitionNodeItem<?, ?>> items = ISequence.of(ObjectUtils.notNull(
              evaluateItem(metapath, nodeItem, dynamicContext).stream()
                  .filter(item -> filterNonDefinitionItem(item, metapath))
                  .map(item -> (IDefinitionNodeItem<?, ?>) item)))
              .reusable();
          assert items != null;
          if (!items.isEmpty()) {
            // build a map to ensure the constraint is only applied once to each
            // underlying definition
            items.stream()
                .map(IDefinitionNodeItem::getDefinition)
                // ensure the definition only gets processed if the module being processed is
                // the containing module
                .filter(definition -> !previouslyTargetedDefinitions.contains(definition))
                .forEach(targetedDefinitions::add);

            // process child contexts, which will be applied depth first
            for (Context context : childContexts) {
              context.applyConstraintsForModule(moduleItem, previouslyTargetedDefinitions, dynamicContext, visitor,
                  items);
            }
          }
        }
      }

      // apply the constraints for this context
      targetedDefinitions.forEach(definition -> {
        getTargetedConstraints().forEach(constraints -> {
          definition.accept(visitor, constraints);
        });
      });

      return CollectionUtil.unmodifiableSet(targetedDefinitions);
    }

    private ISequence<?> evaluateItem(
        @NonNull IMetapathExpression metapath,
        @NonNull INodeItem nodeItem,
        @NonNull DynamicContext dynamicContext) {
      return metapath.evaluate(nodeItem, dynamicContext).reusable();
    }

    private static boolean filterNonDefinitionItem(IItem item, @NonNull IMetapathExpression metapath) {
      boolean retval = item instanceof IDefinitionNodeItem;
      if (!retval) {
        LOGGER.atError().log(
            "Found non-definition item '{}' while applying external constraints using target expression '{}'.",
            item.toString(),
            metapath.getPath());
      }
      return retval;
    }
  }
}
