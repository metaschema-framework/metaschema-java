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
 * Supports generation of a JSON schema, which can be generated inline or as a
 * JSON schema definition.
 * <p>
 * Inline JSON schema generation is supported using the
 * {@link #generateInlineJsonSchema(ObjectNode, IJsonGenerationState)} method.
 * <p>
 * JSON schema definition generation is supported using the
 * {@link #generateDefinitionJsonSchema(ObjectNode, IJsonGenerationState)} and a
 * definition reference can be generated using
 * {@link #generateDefinitionReference(ObjectNode, IJsonGenerationState)}.
 * <p>
 * The
 * {@link #generateJsonSchemaOrDefinitionRef(ObjectNode, IJsonGenerationState)}
 * method can be used to ensure that an inline schema or reference is generated
 * based on the inline behavior (see {@link #isInline(IJsonGenerationState)}.
 */
public interface IJsonSchemaDefinable extends IJsonSchema {

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

  @Override
  default void generateJsonSchemaOrDefinitionRef(ObjectNode node, IJsonGenerationState state) {
    if (isInline(state)) {
      generateInlineJsonSchema(node, state);
    } else {
      generateDefinitionReference(node, state);
    }
  }

  /**
   * The JSON schema definition name that will be used by definition references.
   *
   * @param state
   *          the generation state used to generate this JSON schema
   * @return the name, without the definition path
   * @see #generateDefinitionReference(ObjectNode, IJsonGenerationState)
   */
  @NonNull
  String getDefinitionName();

  /**
   * Generate a JSON schema definition reference for the JSON schema definition
   * representing the Metaschema-based model object associated with this object.
   *
   * @param node
   *          the JSON node to generate the reference within
   * @param state
   *          the generation state used to generate this JSON schema
   */
  default void generateDefinitionReference(@NonNull ObjectNode node, @NonNull IJsonGenerationState state) {
    node.put("$ref", JsonSchemaHelper.generateDefinitionJsonPointer(this));
  }

  /**
   * Generate a JSON schema representing the Metaschema-based model object
   * associated with this object.
   *
   * @param node
   *          the JSON node to generate the schema within
   * @param state
   *          the generation state used to generate this JSON schema
   */
  void generateDefinitionJsonSchema(@NonNull ObjectNode node, @NonNull IJsonGenerationState state);
}
