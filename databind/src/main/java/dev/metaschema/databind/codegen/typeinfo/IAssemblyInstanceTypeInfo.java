/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import dev.metaschema.core.model.IAssemblyInstanceAbsolute;

public interface IAssemblyInstanceTypeInfo extends INamedModelInstanceTypeInfo {
  @Override
  IAssemblyInstanceAbsolute getInstance();
}
