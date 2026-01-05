/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import dev.metaschema.core.model.IDefinition;
import dev.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

/**
 * A JSON schema for a given Metaschema-based model object based on a module
 * definition, which is part of a larger JSON schema.
 *
 * @param <D>
 *          the Java type of the Metaschema module definition
 */
public abstract class AbstractJsonSchemaDefinition<D extends IDefinition> implements IJsonSchemaDefinition {
  @NonNull
  private final D definition;
  @NonNull
  private final Lazy<String> name;

  /**
   * Construct a new JSON schema definition based on a Metaschema module
   * definition.
   *
   * @param definition
   *          the Metaschema module definition
   * @param state
   *          the generation state used to generate this JSON schema
   */
  public AbstractJsonSchemaDefinition(
      @NonNull D definition,
      @NonNull IJsonGenerationState state) {
    this.definition = definition;
    this.name = ObjectUtils.notNull(Lazy.of(() -> generateDefinitionName(state)));
  }

  @Override
  public String getDefinitionName() {
    return ObjectUtils.notNull(name.get());
  }

  @Override
  public final D getDefinition() {
    return definition;
  }

  /**
   * Generate a unique JSON definition name for this schema.
   *
   * @param state
   *          the generation state used to generate this JSON schema
   * @return the name of the JSON defintiion for this schema
   */
  protected abstract String generateDefinitionName(@NonNull IJsonGenerationState state);
}
