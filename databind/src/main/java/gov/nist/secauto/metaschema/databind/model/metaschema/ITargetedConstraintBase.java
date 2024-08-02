/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import edu.umd.cs.findbugs.annotations.Nullable;

public interface ITargetedConstraintBase extends IConstraintBase {
  @Nullable
  String getTarget();
}
