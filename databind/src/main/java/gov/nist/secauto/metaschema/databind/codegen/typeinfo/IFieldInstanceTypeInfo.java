/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import gov.nist.secauto.metaschema.core.model.IFieldInstanceAbsolute;

public interface IFieldInstanceTypeInfo extends INamedModelInstanceTypeInfo {
  @Override
  IFieldInstanceAbsolute getInstance();
}
