/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.typeinfo;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;

public interface IAssemblyInstanceTypeInfo extends INamedModelInstanceTypeInfo {
  @Override
  IAssemblyInstanceAbsolute getInstance();
}
