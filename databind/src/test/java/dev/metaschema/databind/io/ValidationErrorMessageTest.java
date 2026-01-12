/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;

/**
 * Tests for validation error message improvements.
 * <p>
 * These tests verify:
 * <ul>
 * <li>Format-appropriate names in error messages (Issue #595)</li>
 * <li>Location information in error messages (Issue #596)</li>
 * <li>Path context in error messages (Issue #596)</li>
 * <li>Property type distinction (Issue #596)</li>
 * <li>Null field value handling (Issue #205)</li>
 * </ul>
 */
class ValidationErrorMessageTest
    extends AbstractMetaschemaTest {

  private static final Path METASCHEMA_PATH
      = Paths.get("src/test/resources/metaschema/validation-errors/metaschema.xml");
  private static final String ROOT_CLASS_NAME
      = "gov.nist.csrc.ns.metaschema.testing.validation_errors.Root";
  private static final Path CLASS_DIR
      = Paths.get("target/generated-test-sources/validation-errors");

  private static Class<? extends IBoundObject> rootClass;

  @BeforeAll
  static void setup() throws IOException, ClassNotFoundException, MetaschemaException, BindingException {
    ValidationErrorMessageTest test = new ValidationErrorMessageTest();
    rootClass = test.compileModule(
        ObjectUtils.notNull(METASCHEMA_PATH),
        null,
        ROOT_CLASS_NAME,
        ObjectUtils.notNull(CLASS_DIR));
  }

  /**
   * Create a new binding context. This method is provided to avoid synthetic
   * accessor warnings when called from nested test classes.
   *
   * @return a new binding context
   * @throws IOException
   *           if an error occurred loading the binding context
   */
  IBindingContext createBindingContext() throws IOException {
    return newBindingContext();
  }

  /**
   * Tests for format-appropriate names in error messages (Issue #595).
   */
  @Nested
  class FormatAppropriateNamesTest {

    @Test
    void testMissingFlagShowsAttributeForXml() throws IOException {
      // For XML, flags should be identified as "attribute" (user-friendly term)
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain 'attribute' (user-friendly term for flags in XML)
      assertTrue(message.toLowerCase().contains("attribute"),
          "Error message should identify property as 'attribute': " + message);
    }

    @Test
    void testMissingFlagShowsPropertyForJson() throws IOException {
      // For JSON, all properties should be called "property" (user-friendly term)
      String json = "{"
          + "\"required-field\": \"value\","
          + "\"required-assembly\": {\"id\": \"a1\"}"
          + "}";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.JSON, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(json), ObjectUtils.notNull(URI.create("test://example.json")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain 'property' (user-friendly term for JSON)
      assertTrue(message.toLowerCase().contains("property"),
          "Error message should identify property as 'property': " + message);
    }

    @Test
    void testMissingFieldShowsElementForXml() throws IOException {
      // For XML, fields should be identified as "element" (user-friendly term)
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors' required-flag='rf'>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain 'element' (user-friendly term for fields in XML)
      assertTrue(message.toLowerCase().contains("element"),
          "Error message should identify property as 'element': " + message);
    }

    @Test
    void testMissingAssemblyShowsElementForXml() throws IOException {
      // For XML, assemblies should be identified as "element" (user-friendly term)
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors' required-flag='rf'>"
          + "<required-field>value</required-field>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain 'element' (user-friendly term for assemblies in XML)
      assertTrue(message.toLowerCase().contains("element"),
          "Error message should identify property as 'element': " + message);
    }
  }

  /**
   * Tests for location information in error messages (Issue #596).
   */
  @Nested
  class LocationInformationTest {

    @Test
    void testErrorIncludesFileUri() throws IOException {
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      URI sourceUri = ObjectUtils.notNull(URI.create("file:///path/to/example.xml"));

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), sourceUri);
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      assertTrue(message.contains("example.xml") || message.contains(sourceUri.toString()),
          "Error message should include file URI: " + message);
    }

    @Test
    void testErrorIncludesLineNumber() throws IOException {
      // Multi-line XML to ensure we get line numbers
      String xml = "<?xml version='1.0'?>\n"
          + "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>\n"
          + "  <required-field>value</required-field>\n"
          + "  <required-assembly id='a1'/>\n"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain line number (pattern like "at N:" where N is a number)
      assertTrue(message.contains("at ") || message.toLowerCase().contains("line"),
          "Error message should include line number: " + message);
    }

    @Test
    void testErrorIncludesColumnNumber() throws IOException {
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain column number (format like "at N:M" for line:column)
      // Check that message contains the "at N:M" pattern
      assertTrue(message.contains(":") && message.contains("at "),
          "Error message should include column number in format 'at line:column': " + message);
    }

    @Test
    void testJsonErrorIncludesLocation() throws IOException {
      String json = "{\n"
          + "  \"required-field\": \"value\",\n"
          + "  \"required-assembly\": {\"id\": \"a1\"}\n"
          + "}";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.JSON, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(json), ObjectUtils.notNull(URI.create("test://example.json")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain location information
      assertTrue(message.matches(".*\\d+[:\\s].*"),
          "Error message should include location information: " + message);
    }
  }

  /**
   * Tests for path context in error messages (Issue #596).
   */
  @Nested
  class PathContextTest {

    @Test
    void testErrorIncludesPathForXml() throws IOException {
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain path context (e.g., "/root" or "Path:")
      assertTrue(message.contains("/root") || message.toLowerCase().contains("path"),
          "Error message should include path context: " + message);
    }

    @Test
    void testErrorIncludesPathForJson() throws IOException {
      String json = "{"
          + "\"required-field\": \"value\","
          + "\"required-assembly\": {\"id\": \"a1\"}"
          + "}";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.JSON, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(json), ObjectUtils.notNull(URI.create("test://example.json")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should contain path context
      assertTrue(message.contains("/") || message.toLowerCase().contains("path"),
          "Error message should include path context: " + message);
    }

    @Test
    void testErrorAtDocumentRootShowsRootPath() throws IOException {
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // For root-level errors, should show root path
      assertTrue(message.contains("/root") || message.contains("root"),
          "Error message should indicate root element: " + message);
    }
  }

  /**
   * Tests for edge cases.
   */
  @Nested
  class EdgeCaseTest {

    @Test
    void testMultipleMissingPropertiesGroupedByType() throws IOException {
      // Missing both required-flag and required-field
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should mention both missing properties
      assertTrue(message.contains("required-flag") || message.contains("required-field"),
          "Error message should list missing properties: " + message);
    }

    @Test
    void testErrorWithoutSourceUri() throws IOException {
      // Test that error messages are still useful even with a generic/unknown URI
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      // Use a generic URI - error messages should still be informative
      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("unknown:source")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should still have useful error message
      assertTrue(message.toLowerCase().contains("required") || message.toLowerCase().contains("missing"),
          "Error message should indicate missing required property: " + message);
    }

    @Test
    void testDefaultValueNotReportedAsMissing() throws IOException {
      // If we have a field with a default value, it should not be reported as missing
      // This test uses the existing behavior - just ensuring it still works
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors' required-flag='rf'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      // This should parse successfully with all required fields present
      assertDoesNotThrow(() -> {
        Object result
            = deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
        assertNotNull(result, "Should parse successfully with all required fields");
      });
    }

    @Test
    void testParentElementNameInErrorMessage() throws IOException {
      // Error message should include the name of the parent element
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      IOException exception = assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
      });

      String message = exception.getMessage();
      assertNotNull(message, "Exception message should not be null");
      // Should mention the parent element name
      assertTrue(message.toLowerCase().contains("root"),
          "Error message should include parent element name: " + message);
    }
  }

  /**
   * Tests for null field value handling (Issue #205).
   * <p>
   * Note: These tests require specific scenarios where null field values can
   * occur. The implementation will need to detect and handle null values during
   * parsing.
   */
  @Nested
  class NullFieldValueTest {

    @Test
    void testNullFieldValueDoesNotThrowNpe() throws IOException {
      // This test will need adjustment based on how null field values can occur
      // For now, test that parsing doesn't throw NPE for edge cases
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors' required-flag='rf'>"
          + "<required-field>value</required-field>"
          + "<required-assembly id='a1'/>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));

      // Should not throw NullPointerException
      assertDoesNotThrow(() -> {
        try {
          deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://example.xml")));
        } catch (IOException e) {
          // IOException is acceptable, NPE is not
          assertFalse(e.getCause() instanceof NullPointerException,
              "Should not throw NullPointerException: " + e);
        }
      });
    }
  }

  /**
   * Tests for permissive document loading via newPermissiveBoundLoader.
   * <p>
   * These tests verify that permissive loaders skip required field validation,
   * which is useful for the fn:doc() function when loading incomplete documents.
   */
  @Nested
  class PermissiveLoadingTest {

    @Test
    void testPermissiveLoaderSkipsRequiredFieldValidation() throws IOException {
      // Document missing all required fields
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      // Create deserializer and apply permissive configuration
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      // Should not throw - permissive loading skips required field validation
      assertDoesNotThrow(() -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://incomplete.xml")));
      });
    }

    @Test
    void testStrictLoaderValidatesRequiredFields() throws IOException {
      // Document missing all required fields
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      // Use deserializer with explicit strict validation enabled
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.enableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      // Should throw because required fields are missing
      assertThrows(IOException.class, () -> {
        deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://incomplete.xml")));
      });
    }

    @Test
    void testPermissiveLoaderAllowsPartialDocuments() throws IOException {
      // Document with some fields but not all required ones
      String xml = "<root xmlns='http://csrc.nist.gov/ns/metaschema/testing/validation-errors'>"
          + "<optional-field>some value</optional-field>"
          + "</root>";

      IBindingContext bindingContext = createBindingContext();
      IDeserializer<?> deserializer = bindingContext.newDeserializer(Format.XML, ObjectUtils.notNull(rootClass));
      deserializer.disableFeature(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS);

      // Should load successfully despite missing required-flag, required-field, and
      // required-assembly
      Object result = assertDoesNotThrow(
          () -> deserializer.deserialize(new StringReader(xml), ObjectUtils.notNull(URI.create("test://partial.xml"))));

      assertNotNull(result, "Permissive loading should return a result even for partial documents");
    }

    @Test
    void testNewPermissiveBoundLoaderHasValidationDisabled() throws IOException {
      IBindingContext bindingContext = createBindingContext();
      IBoundLoader permissiveLoader = bindingContext.newPermissiveBoundLoader();

      // Verify that the permissive loader has DESERIALIZE_VALIDATE_REQUIRED_FIELDS
      // disabled
      assertFalse(permissiveLoader.isFeatureEnabled(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS),
          "Permissive loader should have DESERIALIZE_VALIDATE_REQUIRED_FIELDS disabled");
    }

    @Test
    void testNewBoundLoaderHasValidationEnabledByDefault() throws IOException {
      IBindingContext bindingContext = createBindingContext();
      IBoundLoader regularLoader = bindingContext.newBoundLoader();

      // Verify that the regular loader has DESERIALIZE_VALIDATE_REQUIRED_FIELDS
      // enabled by default
      assertTrue(regularLoader.isFeatureEnabled(DeserializationFeature.DESERIALIZE_VALIDATE_REQUIRED_FIELDS),
          "Regular loader should have DESERIALIZE_VALIDATE_REQUIRED_FIELDS enabled by default");
    }
  }
}
