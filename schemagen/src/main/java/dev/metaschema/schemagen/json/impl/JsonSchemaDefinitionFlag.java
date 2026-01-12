/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;
import java.util.stream.Stream;

import dev.metaschema.core.model.IFlagDefinition;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a means to generate a JSON schema based on a Metaschema flag
 * definition.
 */
public class JsonSchemaDefinitionFlag
    extends AbstractJsonSchemaDefinition<IFlagDefinition> {

  /**
   * Construct a new JSON schema definition based on a Metaschema module
   * definition.
   *
   * @param definition
   *          the Metaschema module definition
   * @param state
   *          the JSON generation state
   */
  public JsonSchemaDefinitionFlag(
      @NonNull IFlagDefinition definition,
      @NonNull IJsonGenerationState state) {
    super(definition, state);
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    return ObjectUtils.notNull(Stream.of(this));
  }

  @Override
  public String generateDefinitionName(IJsonGenerationState state) {
    return state.generateJsonSchemaDefinitionName(getDefinition(), null, null);
  }

  @Override
  public void generateBody(ObjectNode node, IJsonGenerationState state) {
    IDataTypeJsonSchema schema = state.getDataTypeSchemaForDefinition(getDefinition());
    schema.generateJsonSchemaOrDefinitionRef(node, state);
  }
}
