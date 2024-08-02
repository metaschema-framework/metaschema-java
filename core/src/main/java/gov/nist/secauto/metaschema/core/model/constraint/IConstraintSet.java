/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.IModule;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IConstraintSet {
  /**
   * Get the constraints in the constraint set that apply to the provided module.
   *
   * @param module
   *          a Metaschema module
   * @return an iterator over the constraints that target the module
   */
  @NonNull
  Iterable<ITargetedConstraints> getTargetedConstraintsForModule(@NonNull IModule module);

  /**
   * Get constraint sets imported by this constraint set.
   *
   * @return the imported constraint sets
   */
  @NonNull
  Collection<IConstraintSet> getImportedConstraintSets();
}
