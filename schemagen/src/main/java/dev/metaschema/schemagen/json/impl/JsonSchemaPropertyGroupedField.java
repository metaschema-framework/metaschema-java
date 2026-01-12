/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.metaschema.core.model.IFieldDefinition;
import dev.metaschema.core.model.IFieldInstanceGrouped;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema
 * {@link IFieldInstanceGrouped}, which can be generated inline or as a JSON
 * schema definition.
 */
public class JsonSchemaPropertyGroupedField
    extends AbstractJsonSchemaPropertyGrouped<IFieldInstanceGrouped>
    implements IJsonSchemaDefinitionField {
  @NonNull
  private final IDataTypeJsonSchema fieldValueDataType;
  @NonNull
  private final List<? extends IJsonSchemaPropertyNamed> nonValueProperties;

  /**
   * Construct a new JSON schema property.
   *
   * @param instance
   *          the instance to construct the property for
   * @param state
   *          the JSON generation state used to get JSON schema information
   */
  public JsonSchemaPropertyGroupedField(
      @NonNull IFieldInstanceGrouped instance,
      @NonNull IJsonGenerationState state) {
    super(instance, state);
    this.fieldValueDataType = state.getDataTypeSchemaForDefinition(instance.getDefinition());
    this.nonValueProperties = ObjectUtils.notNull(Stream.concat(
        getFlagProperties().stream(),
        Stream.of(new DiscriminatorProperty()))
        .collect(Collectors.toUnmodifiableList()));
  }

  @Override
  public IFieldDefinition getDefinition() {
    return getInstance().getDefinition();
  }

  @Override
  public List<? extends IJsonSchemaPropertyNamed> getNonValueProperties() {
    return nonValueProperties;
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
