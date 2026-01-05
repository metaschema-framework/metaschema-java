/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.metaschema;

import java.util.List;

/**
 * Base interface for targeted value constraint bindings.
 * <p>
 * This interface combines targeted and value constraint capabilities for
 * constraints that apply to specific value nodes.
 */
public interface IValueTargetedConstraintsBase extends IValueConstraintsBase {
  /**
   * {@inheritDoc}
   * <p>
   * Returns the targeted constraint rules that apply to values matching the
   * target expression.
   *
   * @return the list of targeted constraint rules
   */
  @Override
  List<? extends ITargetedConstraintBase> getRules();
}
