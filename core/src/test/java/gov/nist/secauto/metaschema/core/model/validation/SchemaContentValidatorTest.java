/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Comprehensive tests for schema content validators including XML and JSON
 * validation.
 */
class SchemaContentValidatorTest {

  @NonNull
  private static final String VALID_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<root xmlns=\"http://example.com/test\">\n"
      + "    <name>John Doe</name>\n"
      + "    <age>30</age>\n"
      + "    <email>john@example.com</email>\n"
      + "</root>";

  @NonNull
  private static final String INVALID_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
      + "<root xmlns=\"http://example.com/test\">\n"
      + "    <name>Jane Doe</name>\n"
      + "    <age>-5</age>\n"
      + "</root>";

  @NonNull
  private static final String VALID_JSON = "{\n"
      + "  \"name\": \"John Doe\",\n"
      + "  \"age\": 30,\n"
      + "  \"email\": \"john@example.com\"\n"
      + "}";

  @NonNull
  private static final String INVALID_JSON = "{\n"
      + "  \"name\": \"Jane Doe\",\n"
      + "  \"age\": -5,\n"
      + "  \"extraField\": \"should not be here\"\n"
      + "}";

  @NonNull
  private static final URI TEST_URI = ObjectUtils.notNull(URI.create("file:///test-document"));

  /**
   * Test XML validation with valid content - should pass with no findings.
   */
  @Test
  void testXmlValidationWithValidContent() throws IOException {
    // Load the XSD schema from classpath
    XmlSchemaContentValidator validator = createXmlValidator();

    // Validate valid XML content
    InputStream is = new ByteArrayInputStream(VALID_XML.getBytes(StandardCharsets.UTF_8));
    IValidationResult result = validator.validate(is, TEST_URI);

    assertNotNull(result, "Validation result should not be null");
    assertTrue(result.isPassing(), "Validation should pass for valid content");
    assertEquals(Level.INFORMATIONAL, result.getHighestSeverity(),
        "Highest severity should be INFORMATIONAL for valid content");
    assertTrue(result.getFindings().isEmpty(), "No findings should be present for valid content");
  }

  /**
   * Test XML validation with invalid content - should return findings.
   */
  @Test
  void testXmlValidationWithInvalidContent() throws IOException {
    // Load the XSD schema from classpath
    XmlSchemaContentValidator validator = createXmlValidator();

    // Validate invalid XML content (negative age violates xs:positiveInteger)
    InputStream is = new ByteArrayInputStream(INVALID_XML.getBytes(StandardCharsets.UTF_8));
    IValidationResult result = validator.validate(is, TEST_URI);

    assertNotNull(result, "Validation result should not be null");
    assertFalse(result.isPassing(), "Validation should fail for invalid content");
    assertFalse(result.getFindings().isEmpty(), "Findings should be present for invalid content");
    assertTrue(result.getHighestSeverity().ordinal() >= Level.ERROR.ordinal(),
        "Highest severity should be ERROR or higher for invalid content");
  }

  /**
   * Test XML validation findings have proper metadata.
   */
  @Test
  void testXmlValidationFindingMetadata() throws IOException {
    XmlSchemaContentValidator validator = createXmlValidator();

    InputStream is = new ByteArrayInputStream(INVALID_XML.getBytes(StandardCharsets.UTF_8));
    IValidationResult result = validator.validate(is, TEST_URI);

    assertFalse(result.getFindings().isEmpty(), "Should have at least one finding");

    IValidationFinding finding = result.getFindings().get(0);
    assertNotNull(finding.getMessage(), "Finding should have a message");
    assertNotNull(finding.getCause(), "Finding should have a cause exception");
    assertEquals(IValidationFinding.Kind.FAIL, finding.getKind(), "Finding kind should be FAIL");
    assertNotNull(finding.getDocumentUri(), "Finding should have a document URI");
  }

  /**
   * Test JSON validation with valid content - should pass with no findings.
   */
  @Test
  void testJsonValidationWithValidContent() throws IOException {
    // Load the JSON schema from classpath
    JsonSchemaContentValidator validator = createJsonValidator();

    // Validate valid JSON content
    InputStream is = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
    IValidationResult result = validator.validate(is, TEST_URI);

    assertNotNull(result, "Validation result should not be null");
    assertTrue(result.isPassing(), "Validation should pass for valid content");
    assertEquals(Level.INFORMATIONAL, result.getHighestSeverity(),
        "Highest severity should be INFORMATIONAL for valid content");
    assertTrue(result.getFindings().isEmpty(), "No findings should be present for valid content");
  }

  /**
   * Test JSON validation with invalid content - should return findings.
   */
  @Test
  void testJsonValidationWithInvalidContent() throws IOException {
    JsonSchemaContentValidator validator = createJsonValidator();

    // Validate invalid JSON content (negative age, extra field)
    InputStream is = new ByteArrayInputStream(INVALID_JSON.getBytes(StandardCharsets.UTF_8));
    IValidationResult result = validator.validate(is, TEST_URI);

    assertNotNull(result, "Validation result should not be null");
    assertFalse(result.isPassing(), "Validation should fail for invalid content");
    assertFalse(result.getFindings().isEmpty(), "Findings should be present for invalid content");
    assertTrue(result.getHighestSeverity().ordinal() >= Level.ERROR.ordinal(),
        "Highest severity should be ERROR or higher for invalid content");
  }

  /**
   * Test JSON validation findings have proper metadata.
   */
  @Test
  void testJsonValidationFindingMetadata() throws IOException {
    JsonSchemaContentValidator validator = createJsonValidator();

    InputStream is = new ByteArrayInputStream(INVALID_JSON.getBytes(StandardCharsets.UTF_8));
    IValidationResult result = validator.validate(is, TEST_URI);

    assertFalse(result.getFindings().isEmpty(), "Should have at least one finding");

    IValidationFinding finding = result.getFindings().get(0);
    assertNotNull(finding.getMessage(), "Finding should have a message");
    assertNotNull(finding.getCause(), "Finding should have a cause exception");
    assertEquals(IValidationFinding.Kind.FAIL, finding.getKind(), "Finding kind should be FAIL");
    assertEquals(Level.CRITICAL, finding.getSeverity(), "JSON validation failures should be CRITICAL");
    assertEquals("JSON-pointer", finding.getPathKind(), "Path kind should be JSON-pointer");
    assertNotNull(finding.getDocumentUri(), "Finding should have a document URI");
  }

  /**
   * Test aggregating multiple validation results.
   */
  @Test
  void testAggregateValidationResult() throws IOException {
    XmlSchemaContentValidator xmlValidator = createXmlValidator();
    JsonSchemaContentValidator jsonValidator = createJsonValidator();

    // Validate invalid XML
    InputStream xmlIs = new ByteArrayInputStream(INVALID_XML.getBytes(StandardCharsets.UTF_8));
    IValidationResult xmlResult = xmlValidator.validate(xmlIs, TEST_URI);

    // Validate invalid JSON
    InputStream jsonIs = new ByteArrayInputStream(INVALID_JSON.getBytes(StandardCharsets.UTF_8));
    IValidationResult jsonResult = jsonValidator.validate(jsonIs, TEST_URI);

    // Aggregate both results
    IValidationResult aggregated = AggregateValidationResult.aggregate(xmlResult, jsonResult);

    assertNotNull(aggregated, "Aggregated result should not be null");
    assertFalse(aggregated.isPassing(), "Aggregated result should fail when any result fails");
    assertFalse(aggregated.getFindings().isEmpty(), "Aggregated result should have findings");

    int expectedFindingsCount = xmlResult.getFindings().size() + jsonResult.getFindings().size();
    assertEquals(expectedFindingsCount, aggregated.getFindings().size(),
        "Aggregated result should contain all findings from both results");
  }

  /**
   * Test aggregating results with different severity levels.
   */
  @Test
  void testAggregateValidationResultHighestSeverity() throws IOException {
    XmlSchemaContentValidator xmlValidator = createXmlValidator();

    // Validate valid XML (INFORMATIONAL)
    InputStream validIs = new ByteArrayInputStream(VALID_XML.getBytes(StandardCharsets.UTF_8));
    IValidationResult validResult = xmlValidator.validate(validIs, TEST_URI);

    // Validate invalid XML (CRITICAL)
    InputStream invalidIs = new ByteArrayInputStream(INVALID_XML.getBytes(StandardCharsets.UTF_8));
    IValidationResult invalidResult = xmlValidator.validate(invalidIs, TEST_URI);

    // Aggregate both results - highest severity should be from invalid result
    IValidationResult aggregated = AggregateValidationResult.aggregate(validResult, invalidResult);

    assertNotNull(aggregated, "Aggregated result should not be null");
    assertTrue(aggregated.getHighestSeverity().ordinal() >= Level.ERROR.ordinal(),
        "Aggregated highest severity should be ERROR or higher");
  }

  /**
   * Test aggregating all passing results.
   */
  @Test
  void testAggregateAllPassingResults() throws IOException {
    XmlSchemaContentValidator xmlValidator = createXmlValidator();
    JsonSchemaContentValidator jsonValidator = createJsonValidator();

    // Validate valid XML
    InputStream xmlIs = new ByteArrayInputStream(VALID_XML.getBytes(StandardCharsets.UTF_8));
    IValidationResult xmlResult = xmlValidator.validate(xmlIs, TEST_URI);

    // Validate valid JSON
    InputStream jsonIs = new ByteArrayInputStream(VALID_JSON.getBytes(StandardCharsets.UTF_8));
    IValidationResult jsonResult = jsonValidator.validate(jsonIs, TEST_URI);

    // Aggregate both passing results
    IValidationResult aggregated = AggregateValidationResult.aggregate(xmlResult, jsonResult);

    assertNotNull(aggregated, "Aggregated result should not be null");
    assertTrue(aggregated.isPassing(), "Aggregated result should pass when all results pass");
    assertTrue(aggregated.getFindings().isEmpty(), "Aggregated result should have no findings");
    assertEquals(Level.INFORMATIONAL, aggregated.getHighestSeverity(),
        "Aggregated highest severity should be INFORMATIONAL when all pass");
  }

  /**
   * Test the PASSING_RESULT constant.
   */
  @Test
  void testPassingResultConstant() {
    IValidationResult result = IValidationResult.PASSING_RESULT;

    assertNotNull(result, "PASSING_RESULT should not be null");
    assertTrue(result.isPassing(), "PASSING_RESULT should be passing");
    assertEquals(Level.INFORMATIONAL, result.getHighestSeverity(),
        "PASSING_RESULT highest severity should be INFORMATIONAL");
    assertTrue(result.getFindings().isEmpty(), "PASSING_RESULT should have no findings");
  }

  /**
   * Test validation result severity levels.
   */
  @Test
  void testValidationResultSeverityLevels() {
    // Test that severity levels are properly ordered
    assertTrue(Level.INFORMATIONAL.ordinal() < Level.WARNING.ordinal(),
        "INFORMATIONAL should be less severe than WARNING");
    assertTrue(Level.WARNING.ordinal() < Level.ERROR.ordinal(),
        "WARNING should be less severe than ERROR");
    assertTrue(Level.ERROR.ordinal() < Level.CRITICAL.ordinal(),
        "ERROR should be less severe than CRITICAL");

    // Test isPassing based on severity
    IValidationResult passingResult = IValidationResult.PASSING_RESULT;
    assertTrue(passingResult.isPassing(), "INFORMATIONAL severity should pass");
  }

  /**
   * Create an XML validator with the test schema.
   *
   * @return the XML validator
   * @throws IOException
   *           if the schema cannot be loaded
   */
  @NonNull
  private static XmlSchemaContentValidator createXmlValidator() throws IOException {
    // Don't use try-with-resources - the XmlSchemaContentValidator constructor
    // takes @Owning ownership of the stream and closes it in toSchema()
    InputStream schemaIs = SchemaContentValidatorTest.class
        .getResourceAsStream("/schema-validation/simple-test.xsd");
    assertNotNull(schemaIs, "Schema resource should be found on classpath");

    StreamSource schemaSource = new StreamSource(schemaIs);
    return new XmlSchemaContentValidator(ObjectUtils.notNull(List.of(schemaSource)));
  }

  /**
   * Create a JSON validator with the test schema.
   *
   * @return the JSON validator
   * @throws IOException
   *           if the schema cannot be loaded
   */
  @NonNull
  private static JsonSchemaContentValidator createJsonValidator() throws IOException {
    try (InputStream schemaIs = SchemaContentValidatorTest.class
        .getResourceAsStream("/schema-validation/simple-test-schema.json")) {
      assertNotNull(schemaIs, "Schema resource should be found on classpath");

      return new JsonSchemaContentValidator(schemaIs);
    }
  }
}
