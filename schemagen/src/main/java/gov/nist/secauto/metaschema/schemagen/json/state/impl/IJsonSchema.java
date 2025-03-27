
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a JSON schema for a given Metaschema-based model object, which may
 * be part of a larger JSON schema.
 */
// TODO: remove IDefineableJsonSchema
public interface IJsonSchema {

  /**
   * Generate an inline JSON schema for use in a JSON property.
   * 
   * @param state
   *          the JSON generation state
   * @return a JSON schema representing the Metaschema-based model object
   *         associated with this object
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
