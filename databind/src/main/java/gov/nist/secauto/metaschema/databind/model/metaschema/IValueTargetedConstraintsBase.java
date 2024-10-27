/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.databind.model.metaschema.binding.ConstraintLetExpression;

import java.util.List;

public interface IValueTargetedConstraintsBase extends IValueConstraintsBase {
  @Override
  List<ConstraintLetExpression> getLets();

  @Override
  List<? extends ITargetedConstraintBase> getRules();
}
