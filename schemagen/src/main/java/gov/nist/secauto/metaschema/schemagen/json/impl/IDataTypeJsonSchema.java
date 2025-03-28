/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema data type, which
 * can be generated inline or as a JSON schema definition.
 */
public interface IDataTypeJsonSchema extends IJsonSchemaDefinable {
  /**
   * Get the adapter associated with this data type JSON schema.
   * 
   * @return the adapter
   */
  @NonNull
  IDataTypeAdapter<?> getDataTypeAdapter();
}
