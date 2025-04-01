/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.model.IModelElementVisitor;
import gov.nist.secauto.metaschema.core.model.ISource;

import java.util.Collection;

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
   *
   * @param moduleItem
   *          the module node item to apply applicable constraints to
   * @param visitor
   *          the visitor used to apply constraints to target definitions
   */
  void applyConstraintsForModule(
      @NonNull IModuleNodeItem moduleItem,
      @NonNull IModelElementVisitor<ITargetedConstraints, Void> visitor);
}
