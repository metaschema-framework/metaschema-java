/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.schemagen.json.IDataTypeJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinable;
import gov.nist.secauto.metaschema.schemagen.json.state.impl.IJsonSchemaDefinition;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DataTypeJsonSchema
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
  public String getDefinitionName() {
    return name;
  }

  @Override
  public boolean isInline(IJsonGenerationState state) {
    // never inline
    return false;
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(Set<IJsonSchemaDefinition> visited,
      IJsonGenerationState state) {
    return Stream.empty();
  }

  @Override
  public void generateDefinitionJsonSchema(ObjectNode node, IJsonGenerationState state) {
    throw new UnsupportedOperationException("not used");
  }

  @Override
  public void generateInlineJsonSchema(ObjectNode node, IJsonGenerationState state) {
    // do nothing, this is a direct reference to the underlying Module data type
    // the type is generated for the built-in type by the data type manager
    throw new UnsupportedOperationException("not used");
  }
}
