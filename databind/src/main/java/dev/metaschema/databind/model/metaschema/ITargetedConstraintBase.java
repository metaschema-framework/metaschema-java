/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents constraint metadata that is common to all constraints that are
 * targeted at a specific set of nodes matching the target.
 */
/**
 * Base interface for targeted constraint bindings.
 * <p>
 * This interface provides access to constraints that target specific nodes
 * within a Metaschema model using Metapath expressions.
 */
public interface ITargetedConstraintBase extends IConstraintBase {
  /**
   * The target to match to determine the nodes to check against the constraint.
   * <p>
   * If a target is not provided the the current context node is used as the
   * target.
   *
   * @return the target or {@code null} if the default target is to be used
   */
  @Nullable
  String getTarget();
}
