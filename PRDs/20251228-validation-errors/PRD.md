# PRD: Validation Error Message Improvements

## Problem Statement

Validation error messages during deserialization lack sufficient context for users to debug content issues effectively. Current problems include:

1. **Wrong property names**: Error messages use JSON property names even when parsing XML content, confusing users working with XML-only scenarios
2. **Missing location info**: Required field validation errors lack file URI, line/column numbers that other parsing errors include
3. **No path context**: Errors don't indicate where in the document structure the problem occurred
4. **Generic terminology**: Messages use "property" instead of distinguishing between flags, fields, and assemblies
5. **Poor null handling**: Null field values cause NPE without meaningful context (issue #205)

## Goals

1. Use format-appropriate names in error messages (XML names for XML, JSON names for JSON)
2. Include file location (URI, line, column) in all validation error messages
3. Include document path (XPath-style for XML, JSON Pointer-style for JSON)
4. Distinguish property types (flag, field, assembly) in error messages
5. Replace NPE for null field values with informative validation errors
6. Maintain consistency with existing error message formats in the codebase
7. Leverage existing `IResourceLocation` infrastructure

## Non-Goals

- Internationalization of error messages (future enhancement)
- Array index tracking in paths (can be added later)
- Changes to constraint validation error messages (separate scope)

## Success Metrics

- All validation errors include location information when available
- Error messages correctly identify property types
- XML parsing errors use XML element/attribute names
- JSON parsing errors use JSON property names
- No NPE thrown for null field values
- All new functionality covered by tests

## Requirements

### R1: Format-Appropriate Names (Issue #595)

Error messages must use names appropriate to the parsing format:

| Format | Name Source |
|--------|-------------|
| XML | `getEffectiveName()` returns XML element/attribute name |
| JSON | `getEffectiveName()` returns JSON property name |
| YAML | Same as JSON |

### R2: Location Information (Issue #596)

All validation errors must include:

- **Source URI**: File path or resource identifier
- **Line number**: 1-based line number
- **Column number**: 1-based column number (when available)

Use existing `IResourceLocation` interface for consistency.

### R3: Path Context (Issue #596)

Errors must include document path showing location in structure:

- **XML**: XPath-style path (`/catalog/metadata/party`)
- **JSON/YAML**: JSON Pointer-style path (`/catalog/metadata/party`)

Path is built during parsing using a lightweight `PathTracker` utility.

### R4: User-Friendly Terminology (Issue #596)

Error messages must use format-appropriate, user-friendly terminology that does not require understanding of Metaschema concepts:

| Format | Flag Instance | Field/Assembly Instance |
|--------|---------------|------------------------|
| XML | "attribute" | "element" |
| JSON | "property" | "property" |
| YAML | "property" | "property" |

This approach:
- Uses terms familiar to users of each format
- Avoids Metaschema-specific jargon ("flag", "field", "assembly")
- Makes error messages actionable without Metaschema knowledge

### R5: Null Field Value Handling (Issue #205)

Replace `ObjectUtils.requireNonNull()` in `IBoundDefinitionModelFieldComplex.getFieldValue()` with validation during deserialization:

- Detect null field values during parsing
- Report with full context (location, path, property info)
- Use new `handleNullFieldValue()` method in problem handler

### R6: ValidationContext Object

Introduce `ValidationContext` class to bundle parsing context:

```java
public class ValidationContext {
  private final IResourceLocation location;
  private final String path;
  private final Format format;
}
```

### R7: Error Message Format

Standard format for validation errors:

```text
Missing required {type} '{name}' in '{parentName}'
  Location: {uri} at {line}:{column}
  Path: {path}
```

Where `{type}` uses format-appropriate terminology per R4.

For multiple missing properties of different types:

```text
Missing required properties in '{parentName}':
  Attributes: attr1, attr2  (XML only)
  Elements: elem1, elem2    (XML only)
  Properties: prop1, prop2  (JSON/YAML)
  Location: {uri} at {line}:{column}
  Path: {path}
```

## Edge Cases

### Location Edge Cases

- **Unknown location**: Display "unknown location" or omit location line
- **Column unavailable**: Show "line 15" without column
- **No source URI**: Show "(no source)" or omit

### Path Edge Cases

- **Root level errors**: Display "at document root" or "/"
- **Special characters**: Properly escape names in paths

### Property Edge Cases

- **Choice groups**: Only report missing if ALL alternatives are missing
- **Default values**: Properties with defaults are NOT reported as missing
- **Empty vs null**: Distinguish between empty values and null

## Related Issues

- [#595](https://github.com/metaschema-framework/metaschema-java/issues/595) - Format-appropriate names
- [#596](https://github.com/metaschema-framework/metaschema-java/issues/596) - Location and contextual information
- [#205](https://github.com/metaschema-framework/metaschema-java/issues/205) - Null field value handling

## Implementation Plan

See [implementation-plan.md](./implementation-plan.md) for detailed task breakdown.
