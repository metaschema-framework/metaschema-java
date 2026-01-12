/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;
import java.util.stream.Stream;

import dev.metaschema.core.model.IFieldInstance;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.util.CollectionUtil;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema
 * {@link IFieldInstanceAbsolute}, which can be generated inline or as a JSON
 * schema definition.
 */
public class JsonSchemaPropertyField
    extends AbstractJsonSchemaPropertyNamed<IFieldInstanceAbsolute> {
  @NonNull
  private final IJsonSchemaModelDefinition definitionSchema;
  private final IFlagInstance jsonKey;

  /**
   * Construct a new JSON schema property.
   *
   * @param instance
   *          the instance to construct the property for
   * @param state
   *          the JSON generation state used to get JSON schema information
   */
  public JsonSchemaPropertyField(
      @NonNull IFieldInstanceAbsolute instance,
      @NonNull IJsonGenerationState state) {
    super(instance, instance.getJsonName());
    this.jsonKey = instance.getJsonKey();
    this.definitionSchema = state.getFieldDefinition(
        instance.getDefinition(),
        jsonKey == null ? null : jsonKey.getQName());
  }

  @Override
  public boolean isRequired() {
    return getInstance().getMinOccurs() > 0;
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    return definitionSchema.collectDefinitions(visited, state);
  }

  @Override
  protected void generateMetadata(ObjectNode obj, IJsonGenerationState state) {
    IFieldInstance instance = getInstance();
    JsonSchemaHelper.generateTitle(instance, obj);
    JsonSchemaHelper.generateDescription(instance, obj);
    // TODO: handle complex case?
    JsonSchemaHelper.generateDefault(instance, obj);
  }

  @Override
  protected void generateBody(ObjectNode obj, IJsonGenerationState state) {
    ICardinalityBehavior.behaviorFor(getInstance())
        .generate(obj, getInstance(), CollectionUtil.singleton(definitionSchema), state);
    assert !obj.isEmpty();
  }
}
