/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.AbstractSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;
import gov.nist.secauto.metaschema.schemagen.json.impl.IJsonSchema;
import gov.nist.secauto.metaschema.schemagen.json.impl.JsonDatatypeManager;
import gov.nist.secauto.metaschema.schemagen.json.impl.JsonGenerationState;
import gov.nist.secauto.metaschema.schemagen.json.impl.JsonSchemaModule;

import java.io.IOException;
import java.io.Writer;

import edu.umd.cs.findbugs.annotations.NonNull;

public class JsonSchemaGenerator
    extends AbstractSchemaGenerator<JsonGenerator, JsonDatatypeManager, JsonGenerationState> {
  @NonNull
  private final JsonFactory jsonFactory;

  public JsonSchemaGenerator() {
    this(new JsonFactory());
  }

  public JsonSchemaGenerator(@NonNull JsonFactory jsonFactory) {
    this.jsonFactory = jsonFactory;
  }

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
    ObjectNode schemaNode = state.getJsonNodeFactory().objectNode();
    moduleSchema.generateInlineJsonSchema(schemaNode, state);

    try {
      state.writeObject(schemaNode);
    } catch (IOException ex) {
      throw new SchemaGenerationException(ex);
    }
  }
}
