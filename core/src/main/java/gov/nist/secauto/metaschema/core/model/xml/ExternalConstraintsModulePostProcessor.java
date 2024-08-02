/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression.ResultType;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraintSet;
import gov.nist.secauto.metaschema.core.model.constraint.ITargetedConstraints;
import gov.nist.secauto.metaschema.core.model.constraint.impl.ConstraintComposingVisitor;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A module loading post processor that integrates applicable external
 * constraints into a given module when loaded.
 *
 * @see ModuleLoader#ModuleLoader(List)
 */
public class ExternalConstraintsModulePostProcessor implements IModuleLoader.IModulePostProcessor {
  private static final Logger LOGGER = LogManager.getLogger(ExternalConstraintsModulePostProcessor.class);
  @NonNull
  private final List<IConstraintSet> registeredConstraintSets;

  /**
   * Create a new post processor.
   *
   * @param additionalConstraintSets
   *          the external constraint sets to apply
   */
  public ExternalConstraintsModulePostProcessor(@NonNull Collection<IConstraintSet> additionalConstraintSets) {
    this.registeredConstraintSets = ObjectUtils.notNull(additionalConstraintSets.stream()
        .flatMap(set -> Stream.concat(
            Stream.of(set),
            set.getImportedConstraintSets().stream()))
        .distinct()
        .collect(Collectors.toUnmodifiableList()));
  }

  /**
   * Get the external constraint sets associated with this post processor.
   *
   * @return the list of constraint sets
   */
  protected List<IConstraintSet> getRegisteredConstraintSets() {
    return registeredConstraintSets;
  }

  @Override
  public void processModule(IModule module) {
    ConstraintComposingVisitor visitor = new ConstraintComposingVisitor();
    IModuleNodeItem moduleItem = INodeItemFactory.instance().newModuleNodeItem(module);

    StaticContext staticContext = StaticContext.builder()
        .defaultModelNamespace(module.getXmlNamespace())
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);

    for (IConstraintSet set : getRegisteredConstraintSets()) {
      assert set != null;
      applyConstraints(module, moduleItem, set, visitor, dynamicContext);
    }
  }

  private static void applyConstraints(
      @NonNull IModule module,
      @NonNull IModuleNodeItem moduleItem,
      @NonNull IConstraintSet set,
      @NonNull ConstraintComposingVisitor visitor,
      @NonNull DynamicContext dynamicContext) {
    for (ITargetedConstraints targeted : set.getTargetedConstraintsForModule(module)) {
      // apply targeted constraints
      String targetExpression = targeted.getTargetExpression();
      MetapathExpression metapath = MetapathExpression.compile(targetExpression, dynamicContext.getStaticContext());
      ISequence<?> items = metapath.evaluateAs(moduleItem, ResultType.SEQUENCE, dynamicContext);
      assert items != null;

      for (IItem item : items) {
        if (item instanceof IDefinitionNodeItem) {
          ((IDefinitionNodeItem<?, ?>) item).accept(visitor, targeted);
        } else {
          // log error
          if (LOGGER.isErrorEnabled()) {
            LOGGER.atError().log(
                "Found non-definition item '{}' while applying external constraints using target expression '{}'.",
                item.toString(),
                targetExpression);
          }
        }
      }
    }
  }
}
