
package gov.nist.secauto.metaschema.schemagen.json.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IJsonSchemaDefinable extends IJsonSchema {

  @NonNull
  Stream<IJsonSchemaDefinable> collectDefinitions(
      @NonNull Set<IJsonSchemaDefinition> visited,
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
   * @see #generateDefinitionReference(ObjectNode, ISchemaData)
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
  void generateDefinitionJsonSchema(ObjectNode node, IJsonGenerationState state);
}
