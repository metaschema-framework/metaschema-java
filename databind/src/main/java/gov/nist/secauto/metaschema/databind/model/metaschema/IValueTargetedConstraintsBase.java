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
  @Override
  List<? extends ITargetedConstraintBase> getRules();
}
