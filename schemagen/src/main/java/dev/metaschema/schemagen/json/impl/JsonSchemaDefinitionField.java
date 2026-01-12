/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.qname.IEnhancedQName;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides a means to generate a JSON schema based on a Metaschema field
 * definition.
 */
public class JsonSchemaDefinitionField
    extends AbstractJsonSchemaModelDefinition<IFieldDefinition>
    implements IJsonSchemaDefinitionField {
  @NonNull
  private final IDataTypeJsonSchema fieldValueDataType;

  /**
   * Construct a new JSON schema definition based on a Metaschema module
   * definition.
   *
   * @param definition
   *          the Metaschema module definition
   * @param jsonKeyFlagName
   *          the JSON key flag to use with thsi definition or {@code null} if no
   *          JSON key is used
   * @param state
   *          the JSON generation state
   */
  public JsonSchemaDefinitionField(
      @NonNull IFieldDefinition definition,
      @Nullable IEnhancedQName jsonKeyFlagName,
      @NonNull IJsonGenerationState state) {
    super(definition, jsonKeyFlagName, state);
    this.fieldValueDataType = state.getDataTypeSchemaForDefinition(getDefinition());
  }

  @Override
  public List<? extends IJsonSchemaPropertyNamed> getNonValueProperties() {
    return getFlagProperties();
  }

  @Override
  public IDataTypeJsonSchema getFieldValue() {
    return fieldValueDataType;
  }

  @Override
  public void generateBody(ObjectNode node, IJsonGenerationState state) {
    JsonSchemaHelper.generateFieldBody(this, node, state);
  }
}
