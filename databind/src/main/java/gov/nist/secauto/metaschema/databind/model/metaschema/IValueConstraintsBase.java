/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.ConstraintLetExpression;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a common interface for value constraint binding objects.
 */
/**
 * Base interface for value-level constraint bindings.
 * <p>
 * This interface provides access to constraints that apply to flag and field
 * values.
 */
public interface IValueConstraintsBase extends IBoundObject {
  /**
   * Get the let expressions defined for this constraint set.
   *
   * <p>
   * The default implementation returns an empty list. Implementations with let
   * expressions should override this method.
   *
   * @return the list of let expressions, or an empty list if none are defined
   */
  @NonNull
  default List<ConstraintLetExpression> getLets() {
    return CollectionUtil.emptyList();
  }

  /**
   * Get the constraint rules defined for this constraint set.
   *
   * @return the list of constraint rules
   */
  List<? extends IConstraintBase> getRules();
}
