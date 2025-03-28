/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IDefinition;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports generation of a JSON schema based on a Metaschema definition, which
 * can be generated inline or as a JSON schema definition.
 */
public interface IJsonSchemaDefinition extends IJsonSchemaDefinable {
  @Override
  default boolean isInline(IJsonGenerationState state) {
    return state.isInline(getDefinition());
  }

  /**
   * Get the associated definition.
   *
   * @return the definition
   */
  @NonNull
  IDefinition getDefinition();

  @Override
  default void generateDefinitionJsonSchema(ObjectNode node, IJsonGenerationState state) {
    node.put("$id", JsonSchemaHelper.generateDefinitionJsonPointer(this));

    JsonSchemaHelper.generateTitle(getDefinition(), node);
    JsonSchemaHelper.generateDescription(getDefinition(), node);
    generateBody(node, state);
  }

  @Override
  default void generateInlineJsonSchema(ObjectNode node, IJsonGenerationState state) {
    // do not generate the metadata, since this will be the responsibility of the
    // property
    generateBody(node, state);
  }

  /**
   * Generate the body of the JSON schema.
   *
   * @param node
   *          the JSON node to generate the schema within
   * @param state
   *          the generation state used to generate this JSON schema
   */
  void generateBody(@NonNull ObjectNode node, @NonNull IJsonGenerationState state);
}
