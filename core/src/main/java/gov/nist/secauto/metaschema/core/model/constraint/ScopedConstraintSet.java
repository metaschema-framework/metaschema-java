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
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelElementVisitor;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The default implementation of a constraint set sourced from an external
 * constraint resource.
 */
public class ScopedConstraintSet implements IConstraintSet {
  private static final Logger LOGGER = LogManager.getLogger(ScopedConstraintSet.class);
  @NonNull
  private final ISource source;
  @NonNull
  private final Set<IConstraintSet> importedConstraintSets;
  @NonNull
  private final Map<IEnhancedQName, List<IScopedContraints>> scopedContraints;
  @NonNull
  private final Set<IDefinition> previouslyTargetedDefinitions = new HashSet<>();

  /**
   * Construct a new constraint set.
   *
   * @param source
   *          the resource the constraint was provided from
   * @param scopedContraints
   *          a set of constraints qualified by a scope path
   * @param importedConstraintSets
   *          constraint sets imported by this constraint set
   */
  @SuppressWarnings("null")
  public ScopedConstraintSet(
      @NonNull ISource source,
      @NonNull List<IScopedContraints> scopedContraints,
      @NonNull Set<IConstraintSet> importedConstraintSets) {
    this.source = source;
    this.scopedContraints = scopedContraints.stream()
        .collect(
            Collectors.collectingAndThen(
                Collectors.groupingBy(
                    scope -> IEnhancedQName.of(scope.getModuleNamespace().toString(), scope.getModuleShortName()),
                    Collectors.toUnmodifiableList()),
                Collections::unmodifiableMap));
    this.importedConstraintSets = CollectionUtil.unmodifiableSet(importedConstraintSets);
  }

  /**
   * Get the resource the constraint was provided from.
   *
   * @return the resource
   */
  @Override
  public ISource getSource() {
    return source;
  }

  /**
   * Get the set of Metaschema scoped constraints to apply by a {@link QName}
   * formed from the Metaschema namespace and short name.
   *
   * @return the mapping of QName to scoped constraints
   */
  @NonNull
  public Map<IEnhancedQName, List<IScopedContraints>> getScopedContraints() {
    return scopedContraints;
  }

  @Override
  public Set<IConstraintSet> getImportedConstraintSets() {
    return importedConstraintSets;
  }

  @Override
  public void applyConstraintsForModule(
      IModuleNodeItem moduleItem,
      IModelElementVisitor<ITargetedConstraints, Void> visitor) {
    IEnhancedQName qname = moduleItem.getModule().getQName();
    List<IScopedContraints> scopes = getScopedContraints().getOrDefault(qname, CollectionUtil.emptyList());

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    Map<IDefinition, Set<ITargetedConstraints>> definitionConstraints = new HashMap<>();

    DynamicContext dynamicContext = new DynamicContext(getSource().getStaticContext());

    for (IScopedContraints scoped : scopes) {
      for (ITargetedConstraints targeted : scoped.getTargetedContraints()) {
        for (IMetapathExpression metapath : targeted.getTargets()) {
          ISequence<? extends IDefinitionNodeItem<?, ?>> items = ISequence.of(ObjectUtils.notNull(
              metapath.evaluate(moduleItem, dynamicContext).stream()
                  .filter(item -> filterNonDefinitionItem(item, metapath))
                  .map(item -> (IDefinitionNodeItem<?, ?>) item)))
              .reusable();
          assert items != null;

          Set<IDefinition> targetedDefinitions = items.stream()
              .map(IDefinitionNodeItem::getDefinition)
              .filter(definition -> !previouslyTargetedDefinitions.contains(definition))
              .collect(Collectors.toUnmodifiableSet());

          targetedDefinitions.forEach(definition -> {
            definitionConstraints.compute(definition, (key, value) -> {
              Set<ITargetedConstraints> targets = value == null ? new HashSet<>() : value;
              targets.add(targeted);
              return targets;
            });
          });
        }
      }
    }

    for (Map.Entry<IDefinition, Set<ITargetedConstraints>> entry : definitionConstraints.entrySet()) {
      IDefinition definition = entry.getKey();
      for (ITargetedConstraints constraints : entry.getValue()) {
        definition.accept(visitor, constraints);
      }
    }
    previouslyTargetedDefinitions.addAll(ObjectUtils.notNull(definitionConstraints.keySet()));
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
