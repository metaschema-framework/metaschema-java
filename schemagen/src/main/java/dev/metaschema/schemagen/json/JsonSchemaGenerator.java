/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dev.metaschema.core.configuration.IConfiguration;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.schemagen.AbstractSchemaGenerator;
import dev.metaschema.schemagen.SchemaGenerationException;
import dev.metaschema.schemagen.SchemaGenerationFeature;
import dev.metaschema.schemagen.json.impl.IJsonSchema;
import dev.metaschema.schemagen.json.impl.JsonDatatypeManager;
import dev.metaschema.schemagen.json.impl.JsonGenerationState;
import dev.metaschema.schemagen.json.impl.JsonSchemaModule;

import java.io.IOException;
import java.io.Writer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Generates JSON Schema documents from Metaschema modules.
 * <p>
 * This generator produces JSON Schema draft-07 compatible schemas that can be
 * used to validate JSON and YAML content conforming to the Metaschema model.
 */
public class JsonSchemaGenerator
    extends AbstractSchemaGenerator<JsonGenerator, JsonDatatypeManager, JsonGenerationState> {
  @NonNull
  private final JsonFactory jsonFactory;

  /**
   * Constructs a new JSON schema generator using a default JSON factory.
   */
  public JsonSchemaGenerator() {
    this(new JsonFactory());
  }

  /**
   * Constructs a new JSON schema generator using the specified JSON factory.
   *
   * @param jsonFactory
   *          the Jackson JSON factory to use for creating JSON generators
   */
  public JsonSchemaGenerator(@NonNull JsonFactory jsonFactory) {
    this.jsonFactory = jsonFactory;
  }

  /**
   * Retrieves the JSON factory used by this generator.
   *
   * @return the JSON factory instance
   */
  @NonNull
  public JsonFactory getJsonFactory() {
    return jsonFactory;
  }

  @SuppressWarnings("resource")
  @Override
  protected JsonGenerator newWriter(Writer out) {
    try {
      return ObjectUtils.notNull(getJsonFactory().createGenerator(out)
          .setCodec(new ObjectMapper())
          .useDefaultPrettyPrinter()
          .disable(Feature.AUTO_CLOSE_TARGET));
    } catch (IOException ex) {
      throw new SchemaGenerationException(ex);
    }
  }

  @Override
  protected JsonGenerationState newGenerationState(
      IModule module,
      JsonGenerator schemaWriter,
      IConfiguration<SchemaGenerationFeature<?>> configuration) {
    return new JsonGenerationState(module, schemaWriter, configuration);
  }

  @Override
  protected void generateSchema(JsonGenerationState state) {
    IModule module = state.getModule();

    IJsonSchema moduleSchema = new JsonSchemaModule(module, state);
    ObjectNode schemaNode = ObjectUtils.notNull(state.getJsonNodeFactory().objectNode());
    moduleSchema.generateInlineJsonSchema(schemaNode, state);

    try {
      state.writeObject(schemaNode);
    } catch (IOException ex) {
      throw new SchemaGenerationException(ex);
    }
  }
}
