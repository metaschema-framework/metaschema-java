/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.metaschema.core.configuration.DefaultConfiguration;
import dev.metaschema.core.configuration.IMutableConfiguration;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.model.metaschema.IBindingModuleLoader;
import dev.metaschema.schemagen.ISchemaGenerator;
import dev.metaschema.schemagen.SchemaGenerationFeature;
import dev.metaschema.schemagen.json.JsonSchemaGenerator;

/**
 * Tests for JSON Schema generation of field value properties, covering all
 * json-value-key variations.
 */
class FieldValuePropertyTest {
  private static final Path TEST_SUITE_BASE
      = ObjectUtils.notNull(Paths.get("../core/metaschema/test-suite/schema-generation/"));

  /**
   * Generates a JSON schema for the given Metaschema module and returns the
   * parsed JSON tree.
   */
  private static JsonNode generateJsonSchema(Path metaschemaPath) throws IOException, MetaschemaException {
    Path generationDir = Paths.get("target/generated-modules");
    Files.createDirectories(generationDir);

    IBindingContext bindingContext = IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(generationDir, "modules-")))
        .build();

    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();
    IModule module = loader.load(metaschemaPath);

    ISchemaGenerator schemaGenerator = new JsonSchemaGenerator();
    IMutableConfiguration<SchemaGenerationFeature<?>> features = new DefaultConfiguration<>();
    features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);

    StringWriter writer = new StringWriter();
    schemaGenerator.generateFromModule(module, writer, features);

    ObjectMapper mapper = new ObjectMapper();
    return ObjectUtils.notNull(mapper.readTree(writer.toString()));
  }

  /**
   * Tests that a field with a static json-value-key label generates the correct
   * JSON Schema property structure without duplication.
   *
   * <p>
   * Given a field definition:
   *
   * <pre>{@code
   * <define-field name="link" as-type="string">
   *   <json-value-key>text</json-value-key>
   *   <flag ref="href" required="yes"/>
   * </define-field>
   * }</pre>
   *
   * <p>
   * The generated JSON Schema for the "text" property should be:
   *
   * <pre>{@code
   * "text": { "$ref": "#/definitions/StringDatatype" }
   * }</pre>
   *
   * <p>
   * NOT the incorrectly nested:
   *
   * <pre>{@code
   * "text": { "text": { "$ref": "#/definitions/StringDatatype" } }
   * }</pre>
   */
  @Test
  void testStaticJsonValueKeyLabel() throws IOException, MetaschemaException {
    Path metaschemaPath = TEST_SUITE_BASE
        .resolve("json-value-key/json-value-key-label_metaschema.xml");
    JsonNode schema = generateJsonSchema(metaschemaPath);

    // Find the field definition - look for FieldJsonValueKeyLabelLinkType or
    // similar
    JsonNode definitions = schema.get("definitions");
    assertNotNull(definitions, "Schema should have definitions");

    // Find the link field definition that has the json-value-key
    JsonNode fieldDef = findDefinitionContaining(definitions, "Link");
    assertNotNull(fieldDef, "Should find the Link field definition");

    // The field should be an object type with properties
    assertEquals("object", fieldDef.path("type").asText(),
        "Link field definition should be type 'object'");

    JsonNode properties = fieldDef.get("properties");
    assertNotNull(properties, "Link field should have properties");

    // The "text" property should exist (from json-value-key)
    JsonNode textProperty = properties.get("text");
    assertNotNull(textProperty, "Should have a 'text' property from json-value-key");

    // The "text" property should NOT contain another nested "text" object (the bug)
    assertFalse(textProperty.has("text"),
        "The 'text' property should NOT contain a nested 'text' key - "
            + "this indicates the json-value-key name is being duplicated. "
            + "Actual content: " + textProperty);

    // The "text" property should contain a $ref directly (or allOf/anyOf with $ref)
    assertTrue(textProperty.has("$ref") || textProperty.has("allOf") || textProperty.has("anyOf"),
        "The 'text' property should contain a schema reference ($ref, allOf, or anyOf). "
            + "Actual content: " + textProperty);
  }

  /**
   * Tests that a field with a json-value-key-flag generates the correct JSON
   * Schema structure using additionalProperties for dynamic keys.
   *
   * <p>
   * Fields with json-value-key-flag use the flag's value as the property name at
   * runtime, so the schema uses additionalProperties instead of a fixed property
   * name.
   */
  @Test
  void testJsonValueKeyFlag() throws IOException, MetaschemaException {
    Path metaschemaPath = TEST_SUITE_BASE
        .resolve("json-value-key/json-value-key-field_metaschema.xml");
    JsonNode schema = generateJsonSchema(metaschemaPath);

    JsonNode definitions = schema.get("definitions");
    assertNotNull(definitions, "Schema should have definitions");

    // Find the prop field definition
    JsonNode fieldDef = findDefinitionContaining(definitions, "Property");
    assertNotNull(fieldDef, "Should find the Property field definition");

    assertEquals("object", fieldDef.path("type").asText(),
        "Property field definition should be type 'object'");

    // For json-value-key-flag, the schema should use additionalProperties
    // instead of a fixed value property name
    JsonNode additionalProperties = fieldDef.get("additionalProperties");
    assertNotNull(additionalProperties,
        "Property field with json-value-key-flag should have additionalProperties");
    assertFalse(additionalProperties.isBoolean(),
        "additionalProperties should be a schema object, not a boolean");

    // The "name" flag should NOT appear in properties (it's used as the value key)
    JsonNode properties = fieldDef.get("properties");
    if (properties != null) {
      assertFalse(properties.has("name"),
          "The 'name' flag used as json-value-key-flag should not appear in properties");
    }
  }

  /**
   * Tests that flag properties on a field with json-value-key are correctly
   * generated without duplication.
   *
   * <p>
   * Verifies that the "href" flag property on the Link field (which uses a static
   * json-value-key) is properly structured with a direct schema reference.
   */
  @Test
  void testFlagPropertiesNotDuplicated() throws IOException, MetaschemaException {
    Path metaschemaPath = TEST_SUITE_BASE
        .resolve("json-value-key/json-value-key-label_metaschema.xml");
    JsonNode schema = generateJsonSchema(metaschemaPath);

    // Verify the schema is valid JSON Schema structure
    assertNotNull(schema.get("$schema"), "Schema should have $schema property");
    assertNotNull(schema.get("definitions"), "Schema should have definitions");

    // Verify the "href" flag property is correctly generated (not duplicated)
    JsonNode definitions = schema.get("definitions");
    JsonNode fieldDef = findDefinitionContaining(definitions, "Link");
    assertNotNull(fieldDef, "Should find the Link field definition");

    JsonNode properties = fieldDef.get("properties");
    assertNotNull(properties, "Link field should have properties");

    // The "href" flag property should be properly structured
    JsonNode hrefProperty = properties.get("href");
    assertNotNull(hrefProperty, "Should have an 'href' flag property");

    // href should contain a schema reference, not a nested "href" key
    assertFalse(hrefProperty.has("href"),
        "The 'href' flag property should not contain a nested 'href' key");
    assertTrue(hrefProperty.has("$ref") || hrefProperty.has("title") || hrefProperty.has("allOf"),
        "The 'href' flag property should contain schema metadata or reference. "
            + "Actual content: " + hrefProperty);
  }

  /**
   * Tests that the generated schema for a static json-value-key field does not
   * contain duplicated key names that would be treated as unknown keywords by
   * strict JSON Schema validators.
   *
   * <p>
   * This test catches the specific bug where the json-value-key name is used as
   * both the property name AND as a nested wrapper object, producing invalid
   * schema like: {@code "text": { "text": { "$ref": "..." } }}
   */
  @Test
  void testStaticJsonValueKeyNoUnknownKeywords() throws IOException, MetaschemaException {
    Path metaschemaPath = TEST_SUITE_BASE
        .resolve("json-value-key/json-value-key-label_metaschema.xml");
    JsonNode schema = generateJsonSchema(metaschemaPath);

    // Verify the generated schema structure would allow valid content
    JsonNode definitions = schema.get("definitions");
    JsonNode fieldDef = findDefinitionContaining(definitions, "Link");
    assertNotNull(fieldDef, "Should find the Link field definition");

    JsonNode properties = fieldDef.get("properties");
    assertNotNull(properties, "Should have properties");

    // "text" property should be present and properly structured
    JsonNode textProp = properties.get("text");
    assertNotNull(textProp, "Should have 'text' property");

    // Count the number of keys in the text property node
    // A correct schema has keys like "$ref", "title", "description", "allOf", etc.
    // A buggy schema has a single key "text" pointing to a nested object
    int fieldCount = textProp.size();
    if (fieldCount == 1 && textProp.has("text")) {
      // This is the bug condition - only one field and it's the duplicated key
      throw new AssertionError(
          "BUG DETECTED: 'text' property contains only a nested 'text' object. "
              + "The json-value-key name is being used as both the property name AND "
              + "as an inner wrapper. Expected a direct schema reference. "
              + "Actual: " + textProp);
    }
  }

  /**
   * Finds a definition node whose "title" field contains the given text.
   */
  @SuppressWarnings("deprecation")
  private static JsonNode findDefinitionContaining(JsonNode definitions, String titleSubstring) {
    var fieldIterator = definitions.fields();
    while (fieldIterator.hasNext()) {
      var entry = fieldIterator.next();
      JsonNode def = entry.getValue();
      JsonNode title = def.get("title");
      if (title != null && title.asText().contains(titleSubstring)) {
        return def;
      }
    }
    return null;
  }
}
