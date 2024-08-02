/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDataTypeJsonSchema extends IDefineableJsonSchema {
  @NonNull
  IDataTypeAdapter<?> getDataTypeAdapter();
}
