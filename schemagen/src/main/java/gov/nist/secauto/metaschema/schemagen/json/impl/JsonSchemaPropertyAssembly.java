/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * Supports generation of a JSON schema based on a Metaschema
 * {@link IAssemblyInstanceAbsolute}, which can be generated inline or as a JSON
 * schema definition.
 */
public class JsonSchemaPropertyAssembly
    extends AbstractJsonSchemaPropertyNamed<IAssemblyInstanceAbsolute> {
  @NonNull
  private final Lazy<IJsonSchemaModelDefinition> definitionSchema;
  private final IFlagInstance jsonKey;

  /**
   * Construct a new JSON schema property.
   *
   * @param instance
   *          the instance to construct the property for
   * @param state
   *          the JSON generation state used to get JSON schema information
   */
  public JsonSchemaPropertyAssembly(
      @NonNull IAssemblyInstanceAbsolute instance,
      @NonNull IJsonGenerationState state) {
    super(instance, instance.getJsonName());
    this.jsonKey = instance.getJsonKey();
    this.definitionSchema = ObjectUtils.notNull(Lazy.of(() -> state.getAssemblyDefinition(
        instance.getDefinition(),
        jsonKey == null ? null : jsonKey.getQName())));
  }

  @Override
  public boolean isRequired() {
    return getInstance().getMinOccurs() > 0;
  }

  @NonNull
  private IJsonSchemaModelDefinition getDefinitionSchema() {
    return ObjectUtils.notNull(definitionSchema.get());
  }

  @Override
  protected void generateMetadata(ObjectNode obj, IJsonGenerationState state) {
    IAssemblyInstance instance = getInstance();
    JsonSchemaHelper.generateTitle(instance, obj);
    JsonSchemaHelper.generateDescription(instance, obj);
  }

  @Override
  protected void generateBody(ObjectNode obj, IJsonGenerationState state) {
    ICardinalityBehavior.behaviorFor(getInstance())
        .generate(obj, getInstance(), CollectionUtil.singleton(getDefinitionSchema()), state);
    assert !obj.isEmpty();
  }

  @Override
  public Stream<IJsonSchemaDefinable> collectDefinitions(
      Set<IJsonSchemaDefinitionAssembly> visited,
      IJsonGenerationState state) {
    return getDefinitionSchema().collectDefinitions(visited, state);
  }
}
