/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelElementVisitor;
import gov.nist.secauto.metaschema.core.model.ISource;

import java.util.Collection;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IConstraintSet {
  /**
   * Get information about where the constraint set was sourced from.
   *
   * @return the source information
   */
  @NonNull
  ISource getSource();

  /**
   * Get constraint sets imported by this constraint set.
   *
   * @return the imported constraint sets
   */
  @NonNull
  Collection<? extends IConstraintSet> getImportedConstraintSets();

  /**
   * Apply the constraints associated with this constraint set to the provided
   * module, if applicable.
   * <p>
   * Callers of this method are required to track which definitions have been
   * previously targeted based on the result of this method and to provide these
   * to subsequent calls of this method targeting different modules. This approach
   * ensures that a given constraint is not applied more than once.
   *
   * @param moduleItem
   *          the module node item to apply applicable constraints to
   * @param previouslyTargetedDefinitions
   *          the set of definitions previously targeted for this constraint set
   * @param visitor
   *          the visitor used to apply constraints to target definitions
   * @return the set of definitions targeted
   */
  @NonNull
  Set<IDefinition> applyConstraintsForModule(
      @NonNull IModuleNodeItem moduleItem,
      @NonNull Set<IDefinition> previouslyTargetedDefinitions,
      @NonNull IModelElementVisitor<ITargetedConstraints, Void> visitor);
}
