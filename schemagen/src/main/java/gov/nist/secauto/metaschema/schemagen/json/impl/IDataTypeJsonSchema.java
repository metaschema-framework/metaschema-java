/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDataTypeJsonSchema extends IJsonSchemaDefinable {
  @NonNull
  IDataTypeAdapter<?> getDataTypeAdapter();
}
