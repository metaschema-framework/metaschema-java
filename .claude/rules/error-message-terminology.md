# Error Message Terminology

## User-Friendly Language Required

Error messages displayed to users must use format-appropriate, user-friendly terminology. Users should NOT need to understand Metaschema concepts to understand error messages.

## Terminology Mapping

### Property Type Names

| Metaschema Concept | XML Term | JSON/YAML Term |
|--------------------|----------|----------------|
| Flag | "attribute" | "property" |
| Field | "element" | "property" |
| Assembly | "element" | "property" |

### Container Names

| Metaschema Concept | XML Term | JSON/YAML Term |
|--------------------|----------|----------------|
| Assembly (as parent) | "element" | "object" |
| Field (as parent) | "element" | "object" |

## Examples

### DO (Correct)

```text
Missing required property 'title' in 'metadata'
Missing required attribute 'id' in 'party'
Missing required element 'description' in 'control'
```

### DON'T (Incorrect - Metaschema jargon)

```text
Missing required flag 'id' in assembly 'party'
Missing required field 'description' in assembly 'control'
```

## Rationale

1. **Accessibility**: Most users work with specific formats (XML or JSON) and understand those concepts
2. **Actionable**: Users can immediately locate and fix issues using familiar terminology
3. **No prerequisite knowledge**: Error messages should not require reading Metaschema documentation
4. **Format-appropriate**: XML users expect "attribute" and "element", JSON users expect "property"

## Implementation

Use explicit switch statements on `Format` to handle terminology. Do NOT use `if (format != XML)` patterns as this doesn't accommodate future formats.

### Correct Pattern

```java
switch (format) {
case XML:
  return isFlag ? "attribute" : "element";
case JSON:
case YAML:
  return "property";
default:
  // Fallback for future formats - use generic terminology
  return "property";
}
```

### Incorrect Pattern

```java
// DON'T do this - doesn't handle future formats explicitly
if (format == Format.XML) {
  return isFlag ? "attribute" : "element";
} else {
  return "property";
}
```

## Adding New Formats

When adding a new format to the `Format` enum:

1. Update `AbstractProblemHandler.getFormatPropertyTypeName()` with explicit case
2. Update `AbstractProblemHandler.getFormatPropertyGroupLabel()` with explicit case
3. Consider what terminology is appropriate for the new format's users
