
package gov.nist.secauto.metaschema.schemagen.json.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.schemagen.json.IJsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.impl.MetadataUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a JSON schema for a given Metaschema-based model object based on a
 * Metaschema module definition, which is part of a larger JSON schema.
 */
public interface IJsonSchemaDefinition extends IJsonSchemaDefinable {
  @Override
  default boolean isInline(IJsonGenerationState state) {
    return state.isInline(getDefinition());
  }

  @NonNull
  IDefinition getDefinition();

  @Override
  default void generateDefinitionJsonSchema(ObjectNode node, IJsonGenerationState state) {
    node.put("$id", JsonSchemaHelper.generateDefinitionJsonPointer(this));

    MetadataUtils.generateTitle(getDefinition(), node);
    MetadataUtils.generateDescription(getDefinition(), node);
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
  void generateBody(ObjectNode node, IJsonGenerationState state);
}
