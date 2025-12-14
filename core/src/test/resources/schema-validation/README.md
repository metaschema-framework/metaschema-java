# Schema Validation Test Resources

This directory contains test schemas and instances for validating the schema content validator implementations.

## Files

### XML Schema Files
- `simple-test.xsd` - A simple XML Schema for testing XML validation
  - Defines a `root` element with `name` (string), `age` (positiveInteger), and optional `email` fields
  - Used by `SchemaContentValidatorTest` to test `XmlSchemaContentValidator`

### JSON Schema Files
- `simple-test-schema.json` - A JSON Schema (draft-07) for testing JSON validation
  - Defines an object with `name` (string), `age` (positive integer), and optional `email` fields
  - Enforces `additionalProperties: false` to detect extra fields
  - Used by `SchemaContentValidatorTest` to test `JsonSchemaContentValidator`

### XML Test Instances
- `valid-instance.xml` - Valid XML document that conforms to `simple-test.xsd`
  - Contains valid data: name="John Doe", age=30, email="john@example.com"

- `invalid-instance.xml` - Invalid XML document that violates `simple-test.xsd`
  - Contains invalid data: age=-5 (violates positiveInteger constraint)

### JSON Test Instances
- `valid-instance.json` - Valid JSON document that conforms to `simple-test-schema.json`
  - Contains valid data matching the schema requirements

- `invalid-instance.json` - Invalid JSON document that violates `simple-test-schema.json`
  - Contains multiple violations: age=-5 (violates minimum constraint), extraField (violates additionalProperties)

## Usage

These test resources are loaded by the test classes in the `gov.nist.secauto.metaschema.core.model.validation` package:

- `SchemaContentValidatorTest` - Main comprehensive test suite
- `ValidationFindingTest` - Tests for individual validation finding classes

The tests verify:
1. Valid content passes validation with no findings
2. Invalid content fails validation with appropriate findings
3. Finding metadata (severity, messages, locations) is properly populated
4. Validation result aggregation works correctly
5. Severity levels are properly handled
