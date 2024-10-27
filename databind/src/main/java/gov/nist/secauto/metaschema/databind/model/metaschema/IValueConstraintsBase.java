/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.model.metaschema.binding.ConstraintLetExpression;

import java.util.List;

public interface IValueConstraintsBase extends IBoundObject {
  List<ConstraintLetExpression> getLets();

  List<? extends IConstraintBase> getRules();
}
