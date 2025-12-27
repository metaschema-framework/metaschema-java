/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import java.util.List;

/**
 * Provides a common interface for targeted value constraint binding objects.
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
