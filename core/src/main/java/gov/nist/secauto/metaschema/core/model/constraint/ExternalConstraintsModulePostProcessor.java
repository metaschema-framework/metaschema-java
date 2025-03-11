/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItemFactory;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.core.model.constraint.impl.ConstraintComposingVisitor;
import gov.nist.secauto.metaschema.core.model.xml.ModuleLoader;
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
      applyConstraintsForModule(moduleItem, set, visitor, dynamicContext);
    }
  }

  private static void applyConstraintsForModule(
      @NonNull IModuleNodeItem moduleItem,
      @NonNull IConstraintSet set,
      @NonNull ConstraintComposingVisitor visitor,
      @NonNull DynamicContext dynamicContext) {
    set.applyConstraintsForModule(moduleItem, dynamicContext, visitor);
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
