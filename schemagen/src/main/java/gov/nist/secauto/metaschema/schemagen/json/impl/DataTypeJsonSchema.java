/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DataTypeJsonSchema
    extends AbstractDefineableJsonSchema
    implements IDataTypeJsonSchema {
  @NonNull
  private final String name;
  @NonNull
  private final IDataTypeAdapter<?> dataTypeAdapter;

  public DataTypeJsonSchema(
      @NonNull String name,
      @NonNull IDataTypeAdapter<?> dataTypeAdapter) {
    this.name = name;
    this.dataTypeAdapter = dataTypeAdapter;
  }

  @Override
  @NonNull
  public IDataTypeAdapter<?> getDataTypeAdapter() {
    return dataTypeAdapter;
  }

  @Override
  protected String generateDefinitionName(IJsonGenerationState state) {
    return name;
  }

  @Override
  public void generateInlineSchema(ObjectNode obj, IJsonGenerationState state) {
    // do nothing, this is a direct reference to the underlying Module data type
    // the type is generated for the built-in type by the data type manager
    throw new UnsupportedOperationException("not needed");
  }

  @Override
  public boolean isInline(IJsonGenerationState state) {
    // never inline
    return false;
  }
}
