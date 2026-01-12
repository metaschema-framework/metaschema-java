/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.metaschema.core.model.IInstance;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A JSON schema for a given Metaschema-based definition instance, which is part
 * of a larger JSON schema.
 *
 * @param <I>
 *          the Java type of the Metaschema definition instance
 */
public abstract class AbstractJsonSchemaProperty<I extends IInstance>
    implements IJsonSchemaProperty {
  @NonNull
  private final I instance;

  /**
   * Construct a new JSON schema property based on a Metaschema definition
   * instance.
   *
   * @param instance
   *          the Metaschema definition instance
   */
  protected AbstractJsonSchemaProperty(@NonNull I instance) {
    this.instance = instance;
  }

  /**
   * Get the associated Metaschema instance.
   *
   * @return the instance
   */
  @NonNull
  public I getInstance() {
    return instance;
  }

  @Override
  public void generate(@NonNull ObjectNode node, @NonNull IJsonGenerationState state) {
    generateMetadata(node, state);

    generateBody(node, state);
    assert !node.isEmpty();
  }

  /**
   * Generate human-focused documentation and other metadata.
   *
   * @param node
   *          the property JSON object
   * @param state
   *          the schema generation state used for context
   */
  protected void generateMetadata(
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state) {
    // do nothing by default
  }

  /**
   * Generate the JSON schema body.
   *
   * @param node
   *          the property JSON object
   * @param state
   *          the schema generation state used for context
   */
  protected abstract void generateBody(
      @NonNull ObjectNode node,
      @NonNull IJsonGenerationState state);
}
