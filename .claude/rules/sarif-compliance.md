# SARIF Compliance

## Standard Compliance Required (BLOCKING)

All SARIF output produced by this project MUST conform to the [SARIF 2.1.0 specification](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html).

## Official Schema Location

The official SARIF 2.1.0 JSON schema is stored locally at:

```text
databind-modules/modules/sarif/sarif-schema-2.1.0.json
```

This is the authoritative reference for validating SARIF output.

## Model Alignment

The SARIF Metaschema module (`databind-modules/modules/sarif/sarif-module.xml`) models a subset of the SARIF 2.1.0 specification:

- All modeled types MUST use the exact field names and types from the SARIF spec
- Required fields in the SARIF spec MUST be marked with `min-occurs="1"` in the module
- Optional SARIF fields MAY be omitted from the module (partial models are acceptable)
- No custom assembly or field types that don't exist in SARIF 2.1.0

## Extension Points

The SARIF specification defines `propertyBag` with `additionalProperties: true` as the standard extension mechanism for tool-specific data. Using `propertyBag` for custom properties (e.g., timing data) is standard-compliant and NOT a model extension.

## Testing Requirements

Any code that produces SARIF output MUST include a test that validates the output against the official schema at `databind-modules/modules/sarif/sarif-schema-2.1.0.json`.

### Validation Pattern

```java
// Path is relative to databind-modules/ working directory
Path sarifSchema = Paths.get("modules/sarif/sarif-schema-2.1.0.json");

try (Reader schemaReader = Files.newBufferedReader(sarifSchema, StandardCharsets.UTF_8)) {
    JsonNode schemaNode = new OrgJsonNode(new JSONObject(new JSONTokener(schemaReader)));
    JsonNode instanceNode = new OrgJsonNode(new JSONObject(sarifOutput));

    Validator.Result result = new ValidatorFactory()
        .withJsonNodeFactory(new OrgJsonNode.Factory())
        .withDialect(new Dialects.Draft2020Dialect())
        .validate(schemaNode, instanceNode);
    assertTrue(result.isValid(), "SARIF output failed schema validation");
}
```

## Existing SARIF Tests

| Test Class | What It Validates |
|------------|-------------------|
| `SarifValidationHandlerTest` | Core SARIF output structure and schema compliance |
| `SarifValidationHandlerTimingTest` | Timing enrichment and schema compliance with timing data |

## When Modifying SARIF Output

1. Check the official schema for field names, types, and required status
2. Update the Metaschema module if adding new SARIF types
3. Add or update schema validation tests
4. Verify output passes the official SARIF 2.1.0 JSON schema
