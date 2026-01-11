/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import java.util.Collection;

import dev.metaschema.core.metapath.item.node.IModuleNodeItem;
import dev.metaschema.core.model.IModelElementVisitor;
import dev.metaschema.core.model.ISource;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A set of constraints targeted at the contents of a Metaschema module.
 */
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
   * @param visitor
   *          the visitor used to apply constraints to target definitions
   */
  void applyConstraintsForModule(
      @NonNull IModuleNodeItem moduleItem,
      @NonNull IModelElementVisitor<ITargetedConstraints, Void> visitor);
}
