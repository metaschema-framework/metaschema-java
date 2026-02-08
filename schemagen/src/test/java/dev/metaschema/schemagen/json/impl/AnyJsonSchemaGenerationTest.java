/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.json.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class AnyJsonSchemaGenerationTest {

  private static final Path METASCHEMA_FILE
      = ObjectUtils.notNull(Paths.get("src/test/resources/metaschema/any-test_metaschema.xml"));

  private static IBindingContext getBindingContext() throws IOException {
    return IBindingContext.builder()
        .compilePath(ObjectUtils.notNull(Files.createTempDirectory(Paths.get("target"), "modules-")))
        .build();
  }

  @Test
  void testAnyAssemblyHasAdditionalPropertiesTrue() throws MetaschemaException, IOException {
    IBindingContext bindingContext = getBindingContext();
    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();

    IModule module = loader.load(METASCHEMA_FILE);

    IMutableConfiguration<SchemaGenerationFeature<?>> features
        = new DefaultConfiguration<>();
    features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);

    ISchemaGenerator schemaGenerator = new JsonSchemaGenerator();
    StringWriter writer = new StringWriter();
    schemaGenerator.generateFromModule(module, writer, features);

    String schemaJson = writer.toString();
    assertNotNull(schemaJson, "Generated schema should not be null");

    ObjectMapper mapper = new ObjectMapper();
    JsonNode schema = mapper.readTree(schemaJson);

    // The root assembly 'root' should be in the definitions
    JsonNode definitions = schema.get("definitions");
    assertNotNull(definitions, "Schema should have definitions");

    // Find the root assembly definition (named "AssemblyAnyTestRootType")
    JsonNode rootDef = null;
    var fieldNames = definitions.fieldNames();
    while (fieldNames.hasNext()) {
      String name = fieldNames.next();
      if (name.contains("Root")) {
        rootDef = definitions.get(name);
        break;
      }
    }
    assertNotNull(rootDef, "Should find 'root' assembly definition");

    // The root assembly has <any/>, so additionalProperties should be true
    JsonNode additionalProperties = rootDef.get("additionalProperties");
    assertNotNull(additionalProperties,
        "Root assembly with <any/> should have additionalProperties defined. Schema: " + rootDef);
    assertTrue(additionalProperties.isBoolean(),
        "additionalProperties should be a boolean value");
    assertEquals(true, additionalProperties.booleanValue(),
        "additionalProperties should be true for assembly with <any/>");
  }

  @Test
  void testAssemblyWithoutAnyHasAdditionalPropertiesFalse() throws MetaschemaException, IOException {
    IBindingContext bindingContext = getBindingContext();
    IBindingModuleLoader loader = bindingContext.newModuleLoader();
    loader.allowEntityResolution();

    // Use the metaschema-module-metaschema as an example of an assembly without
    // <any/>
    Path metaschemaFile = ObjectUtils.notNull(
        Paths.get("../core/metaschema/schema/metaschema/metaschema-module-metaschema.xml"));
    IModule module = loader.load(metaschemaFile);

    IMutableConfiguration<SchemaGenerationFeature<?>> features
        = new DefaultConfiguration<>();
    features.disableFeature(SchemaGenerationFeature.INLINE_DEFINITIONS);

    ISchemaGenerator schemaGenerator = new JsonSchemaGenerator();
    StringWriter writer = new StringWriter();
    schemaGenerator.generateFromModule(module, writer, features);

    String schemaJson = writer.toString();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode schema = mapper.readTree(schemaJson);

    JsonNode definitions = schema.get("definitions");
    assertNotNull(definitions, "Schema should have definitions");

    // Check that at least one definition has additionalProperties: false
    // (meaning assemblies without <any/> retain the existing behavior)
    var fieldNames = definitions.fieldNames();
    boolean foundFalse = false;
    while (fieldNames.hasNext()) {
      String name = fieldNames.next();
      JsonNode def = definitions.get(name);
      JsonNode additionalProperties = def.get("additionalProperties");
      if (additionalProperties != null && additionalProperties.isBoolean()
          && !additionalProperties.booleanValue()) {
        foundFalse = true;
        break;
      }
    }
    assertTrue(foundFalse,
        "At least one assembly without <any/> should have additionalProperties: false");
  }
}
