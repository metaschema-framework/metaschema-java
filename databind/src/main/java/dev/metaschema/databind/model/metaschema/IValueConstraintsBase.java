/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import java.util.List;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.databind.model.metaschema.binding.ConstraintLetExpression;
import edu.umd.cs.findbugs.annotations.NonNull;

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
