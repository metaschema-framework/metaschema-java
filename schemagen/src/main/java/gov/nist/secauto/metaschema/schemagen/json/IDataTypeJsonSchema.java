/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinable;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IDataTypeJsonSchema extends IJsonSchemaDefinable {
  @NonNull
  IDataTypeAdapter<?> getDataTypeAdapter();
}
