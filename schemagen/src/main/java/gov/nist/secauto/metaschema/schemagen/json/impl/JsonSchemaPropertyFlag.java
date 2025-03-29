/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema
 * {@link IFlagInstance}, which can be generated inline or as a JSON schema
 * definition.
 */
public class JsonSchemaPropertyFlag
    extends AbstractJsonSchemaPropertyNamed<IFlagInstance>
    implements IJsonSchemaPropertyFlag {
  private final IJsonSchemaDefinition definitionSchema;

  /**
   * Construct a new JSON schema property.
   *
   * @param instance
   *          the instance to construct the property for
   * @param state
   *          the JSON generation state used to get JSON schema information
   */
  public JsonSchemaPropertyFlag(
      @NonNull IFlagInstance instance,
      @NonNull IJsonGenerationState state) {
    super(instance, instance.getJsonName());
    this.definitionSchema = state.getFlagDefinition(instance.getDefinition());
  }

  @Override
  public String getName() {
    return getInstance().getJsonName();
  }

  @Override
  public boolean isRequired() {
    return getInstance().isRequired();
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    return definitionSchema.collectDefinitions(visited, state);
  }

  @Override
  protected void generateMetadata(ObjectNode obj, IJsonGenerationState state) {
    IFlagInstance instance = getInstance();
    JsonSchemaHelper.generateTitle(instance, obj);
    JsonSchemaHelper.generateDescription(instance, obj);
    JsonSchemaHelper.generateDefault(instance, obj);
  }

  @Override
  protected void generateBody(ObjectNode obj, IJsonGenerationState state) {
    definitionSchema.generateJsonSchemaOrDefinitionRef(obj, state);
    assert !obj.isEmpty();
  }
}
