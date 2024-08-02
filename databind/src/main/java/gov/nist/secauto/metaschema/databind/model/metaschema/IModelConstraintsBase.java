/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import java.util.List;

public interface IModelConstraintsBase extends IValueConstraintsBase {
  @Override
  List<? extends ITargetedConstraintBase> getRules();
}
