/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;

/**
 * Tests for required field validation during deserialization.
 */
class RequiredFieldValidationTest
    extends AbstractMetaschemaTest {

  private static final Path METASCHEMA_PATH
      = Paths.get("src/test/resources/metaschema/required-fields/metaschema.xml");
  private static final Path VALID_XML_PATH
      = Paths.get("src/test/resources/metaschema/required-fields/valid-example.xml");
  private static final Path MISSING_FLAG_XML_PATH
      = Paths.get("src/test/resources/metaschema/required-fields/missing-required-flag.xml");
  private static final Path MISSING_FIELD_XML_PATH
      = Paths.get("src/test/resources/metaschema/required-fields/missing-required-field.xml");
  private static final Path MISSING_ASSEMBLY_XML_PATH
      = Paths.get("src/test/resources/metaschema/required-fields/missing-required-assembly.xml");
  private static final Path VALID_JSON_PATH
      = Paths.get("src/test/resources/metaschema/required-fields/valid-example.json");
  private static final Path MISSING_FLAG_JSON_PATH
      = Paths.get("src/test/resources/metaschema/required-fields/missing-required-flag.json");
  private static final Path MISSING_FIELD_JSON_PATH
      = Paths.get("src/test/resources/metaschema/required-fields/missing-required-field.json");
  private static final String ROOT_CLASS_NAME
      = "gov.nist.csrc.ns.metaschema.testing.required_fields.Root";

  private static Class<? extends IBoundObject> rootClass;

  @BeforeAll
  static void setup() throws IOException, ClassNotFoundException, MetaschemaException, BindingException {
    RequiredFieldValidationTest test = new RequiredFieldValidationTest();
    rootClass = test.compileModule(
        ObjectUtils.notNull(METASCHEMA_PATH),
        null,
        ROOT_CLASS_NAME,
        ObjectUtils.notNull(Paths.get("target/generated-test-sources/required-fields")));
  }

  @Test
  void testValidXmlParsesSuccessfully() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    assertDoesNotThrow(() -> {
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      Object result = deserializer.deserialize(ObjectUtils.notNull(VALID_XML_PATH));
      assertNotNull(result, "Valid XML should parse successfully");
    });
  }

  @Test
  void testValidJsonParsesSuccessfully() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    assertDoesNotThrow(() -> {
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.JSON, ObjectUtils.notNull(rootClass));
      Object result = deserializer.deserialize(ObjectUtils.notNull(VALID_JSON_PATH));
      assertNotNull(result, "Valid JSON should parse successfully");
    });
  }

  @Test
  void testMissingRequiredFlagInXmlThrowsError() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
    deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

    IOException exception = assertThrows(IOException.class, () -> {
      deserializer.deserialize(ObjectUtils.notNull(MISSING_FLAG_XML_PATH));
    });

    String message = exception.getMessage();
    assertTrue(message != null && message.contains("required"),
        "Error message should indicate missing required field: " + message);
  }

  @Test
  void testMissingRequiredFieldInXmlThrowsError() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
    deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

    IOException exception = assertThrows(IOException.class, () -> {
      deserializer.deserialize(ObjectUtils.notNull(MISSING_FIELD_XML_PATH));
    });

    String message = exception.getMessage();
    assertTrue(message != null && message.contains("required"),
        "Error message should indicate missing required field: " + message);
  }

  @Test
  void testMissingRequiredAssemblyInXmlThrowsError() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
    deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

    IOException exception = assertThrows(IOException.class, () -> {
      deserializer.deserialize(ObjectUtils.notNull(MISSING_ASSEMBLY_XML_PATH));
    });

    String message = exception.getMessage();
    assertTrue(message != null && message.contains("required"),
        "Error message should indicate missing required field: " + message);
  }

  @Test
  void testMissingRequiredFlagInJsonThrowsError() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.JSON, ObjectUtils.notNull(rootClass));
    deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

    IOException exception = assertThrows(IOException.class, () -> {
      deserializer.deserialize(ObjectUtils.notNull(MISSING_FLAG_JSON_PATH));
    });

    String message = exception.getMessage();
    assertTrue(message != null && message.contains("required"),
        "Error message should indicate missing required field: " + message);
  }

  @Test
  void testMissingRequiredFieldInJsonThrowsError() throws IOException {
    IBindingContext bindingContext = newBindingContext();
    IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.JSON, ObjectUtils.notNull(rootClass));
    deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

    IOException exception = assertThrows(IOException.class, () -> {
      deserializer.deserialize(ObjectUtils.notNull(MISSING_FIELD_JSON_PATH));
    });

    String message = exception.getMessage();
    assertTrue(message != null && message.contains("required"),
        "Error message should indicate missing required field: " + message);
  }

  @Test
  void testValidationCanBeToggledViaFeatureFlag() throws IOException {
    IBindingContext bindingContext = newBindingContext();

    // First verify that validation is enabled by default (throws error)
    IDeserializer<?> deserializer1 = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
    assertThrows(IOException.class, () -> {
      deserializer1.deserialize(ObjectUtils.notNull(MISSING_FLAG_XML_PATH));
    }, "Should throw when validation is enabled by default");

    // Now disable validation and verify it parses without error
    IDeserializer<?> deserializer2 = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
    deserializer2.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);
    assertDoesNotThrow(() -> {
      Object result = deserializer2.deserialize(ObjectUtils.notNull(MISSING_FLAG_XML_PATH));
      assertNotNull(result, "Should parse without error when validation is disabled");
    });
  }

  @ParameterizedTest
  @ValueSource(strings = { "XML", "JSON" })
  void testErrorMessageIncludesFieldName(String formatName) throws IOException {
    Format format = Format.valueOf(formatName);
    Path missingFlagPath = format == Format.XML ? MISSING_FLAG_XML_PATH : MISSING_FLAG_JSON_PATH;

    IBindingContext bindingContext = newBindingContext();
    IDeserializer<?> deserializer = bindingContext.newDeserializer(format, ObjectUtils.notNull(rootClass));
    deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

    IOException exception = assertThrows(IOException.class, () -> {
      deserializer.deserialize(ObjectUtils.notNull(missingFlagPath));
    });

    String message = exception.getMessage();
    assertTrue(message != null && message.contains("required-flag"),
        "Error message should include the field name 'required-flag': " + message);
  }
}
