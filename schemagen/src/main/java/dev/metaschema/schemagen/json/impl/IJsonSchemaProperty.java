/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A JSON schema property construction for a given Metaschema instance, which is
 * part of a larger JSON schema.
 */
public interface IJsonSchemaProperty {
  /**
   * Used to recursively collect definitions that are used within the model graph
   * for descendants of this node.
   *
   * @param visited
   *          the ancestor assembly definitions that have been visited
   * @param state
   *          the generation state used to generate this JSON schema
   * @return a stream of JSON schema definition object referenced within
   *         descendants within this node's graph
   */
  @NonNull
  Stream<IJsonSchemaDefinable> collectDefinitions(
      @NonNull Set<IJsonSchemaDefinitionAssembly> visited,
      @NonNull IJsonGenerationState state);

  /**
   * Generate the property contents.
   *
   * @param node
   *          the property JSON object
   * @param state
   *          the schema generation state used for context
   */
  void generate(@NonNull ObjectNode node, @NonNull IJsonGenerationState state);
}
