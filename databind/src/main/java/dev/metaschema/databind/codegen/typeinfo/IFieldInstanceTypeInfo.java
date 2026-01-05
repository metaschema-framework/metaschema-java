/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.typeinfo;

import dev.metaschema.core.model.IFieldInstanceAbsolute;

public interface IFieldInstanceTypeInfo extends INamedModelInstanceTypeInfo {
  @Override
  IFieldInstanceAbsolute getInstance();
}
