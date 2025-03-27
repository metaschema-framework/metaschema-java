
package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a JSON schema for a given Metaschema-based model object, which may
 * be part of a larger JSON schema.
 */
public interface IJsonSchema {

  /**
   * Generate an inline JSON schema.
   * 
   * @param node
   *          the property JSON object
   * @param state
   *          the JSON generation state
   */
  void generateInlineJsonSchema(@NonNull ObjectNode node, @NonNull IJsonGenerationState state);

  default void generateJsonSchemaOrDefinitionRef(@NonNull ObjectNode node, @NonNull IJsonGenerationState state) {
    generateInlineJsonSchema(node, state);
  }

  /**
   * Determine if the schema is defined inline or as a global definition.
   *
   * @param state
   *          the schema generation state used for context
   * @return {@code true} if the schema is to be defined inline or {@code false}
   *         if the schema is to be defined globally
   */
  default boolean isInline(@NonNull IJsonGenerationState state) {
    return true;
  }
}
