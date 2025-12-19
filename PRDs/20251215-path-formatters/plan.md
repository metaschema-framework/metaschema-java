# PRD: XPath and JSON Pointer Path Formatters

## Problem Statement

The Metaschema framework currently provides a `MetapathFormatter` that produces Metapath expressions for node paths (e.g., `/root[1]/child[2]/@flag`). However, users need additional path formats for different use cases:

1. **XPath 3.1 with namespaces** - For XML tooling integration where namespace-qualified paths are required
2. **JSON Pointer (RFC 6901)** - For JSON tooling integration and JSON-based error reporting

A commented-out `JsonPathFormatter` exists but uses outdated APIs and implements JSONPath (dot notation) rather than JSON Pointer.

## Goals

1. Implement `XPathFormatter` producing namespace-qualified XPath 3.1 paths using EQName format
2. Implement `JsonPointerFormatter` producing RFC 6901 compliant JSON Pointer paths
3. Properly handle XML grouping (`GROUPED`/`UNGROUPED`) in XPath paths
4. Properly handle JSON grouping (`KEYED`/`LIST`/`SINGLETON_OR_LIST`/`NONE`) in JSON Pointer paths
5. Support JSON key flag values for KEYED collections
6. Support JSON value key for field values
7. Expose both formatters as constants on `IPathFormatter` for easy access
8. Achieve comprehensive test coverage for new formatters
9. Clean up or remove the obsolete `JsonPathFormatter.java`

## Non-Goals

- JSONPath (dot notation) support - not in scope
- Namespace prefix resolution from external context - using self-contained EQName format instead

## Future: Format-Based Selection and CLI Integration

### Problem

Validation results currently always use `MetapathFormatter` for paths. Users need:
1. **Automatic format selection** - JSON Pointer paths when validating JSON, XPath when validating XML
2. **CLI override** - Explicit path format selection regardless of document format

### Design

#### Path Format Selection Enum

```java
package gov.nist.secauto.metaschema.core.metapath.format;

/**
 * Enumeration of path format selection options for validation output.
 */
public enum PathFormatSelection {
  /** Auto-select based on document format (JSON/YAML → JSON Pointer, XML → XPath) */
  AUTO,
  /** Always use Metapath format */
  METAPATH,
  /** Always use XPath 3.1 EQName format */
  XPATH,
  /** Always use RFC 6901 JSON Pointer format */
  JSON_POINTER
}
```

#### Resolver Method on IPathFormatter

```java
/**
 * Resolve the appropriate path formatter based on selection and document format.
 *
 * @param selection the path format selection
 * @param documentFormat the document format being validated, may be null
 * @return the resolved path formatter
 */
@NonNull
static IPathFormatter resolveFormatter(
    @NonNull PathFormatSelection selection,
    @Nullable Format documentFormat) {

  if (selection == PathFormatSelection.AUTO && documentFormat != null) {
    return switch (documentFormat) {
      case JSON, YAML -> JSON_POINTER_PATH_FORMATTER;
      case XML -> XPATH_PATH_FORMATTER;
    };
  }

  return switch (selection) {
    case XPATH -> XPATH_PATH_FORMATTER;
    case JSON_POINTER -> JSON_POINTER_PATH_FORMATTER;
    case METAPATH -> METAPATH_PATH_FORMATTER;
    case AUTO -> METAPATH_PATH_FORMATTER; // fallback when no format info
  };
}
```

#### CLI Option (metaschema-cli)

```java
private static final Option PATH_FORMAT_OPTION = ObjectUtils.notNull(
    Option.builder()
        .longOpt("path-format")
        .hasArg()
        .argName("FORMAT")
        .desc("path format in output: auto (default - selects based on document format), "
            + "metapath, xpath, jsonpointer")
        .build());
```

#### Integration Flow

```text
CLI Command
    ↓ parses --path-format option
PathFormatSelection
    ↓ combined with
Format (from document or --as option)
    ↓ resolved via
IPathFormatter.resolveFormatter()
    ↓ passed to
AbstractConstraintValidationHandler.setPathFormatter()
    ↓ used by
Validation result messages
```

#### Handler Configuration

Option 1: Direct setter (existing):
```java
handler.setPathFormatter(IPathFormatter.resolveFormatter(selection, format));
```

Option 2: Convenience method on handler:
```java
// In AbstractConstraintValidationHandler
public void configurePathFormat(
    @NonNull PathFormatSelection selection,
    @Nullable Format documentFormat) {
  setPathFormatter(IPathFormatter.resolveFormatter(selection, documentFormat));
}
```

### Benefits

1. **Smart defaults** - Users get format-appropriate paths automatically (JSON Pointer for JSON/YAML, XPath for XML)
2. **Explicit override** - Users can force a specific format for consistency across mixed documents
3. **Reusable** - Core logic in `IPathFormatter.resolveFormatter()` usable outside CLI (API, plugins)

### Default Behavior

When `--path-format` is not specified, the default is `AUTO`:
- JSON documents → JSON Pointer paths
- YAML documents → JSON Pointer paths
- XML documents → XPath paths

Users who prefer the original Metapath format can explicitly use `--path-format=metapath`.

### Implementation Tasks (Phase 4)

- [ ] Create `PathFormatSelection` enum in `core/.../metapath/format/`
- [ ] Add `resolveFormatter()` static method to `IPathFormatter`
- [ ] Add convenience method to `AbstractConstraintValidationHandler`
- [ ] Add `--path-format` option to `AbstractValidateContentCommand`
- [ ] Parse option in validation executor
- [ ] Pass resolved formatter to validation handler
- [ ] Update `LoggingValidationHandler` to use configured formatter
- [ ] Add unit tests for resolver logic
- [ ] Add integration tests for CLI option
- [ ] Update CLI help documentation

## Technical Approach

### Architecture

Both formatters implement `IPathFormatter` interface:

```text
IPathFormatter (interface)
├── MetapathFormatter (existing)
├── XPathFormatter (new)
└── JsonPointerFormatter (new)
```

### XPathFormatter Specification

**Format**: `/Q{namespace}element[position]/@Q{namespace}flag`

Uses EQName format (XPath 3.1 standard) for namespace qualification via `IEnhancedQName.toEQName()`.

#### Node Type Formatting

| Node Type | Condition | Format | Example |
|-----------|-----------|--------|---------|
| Document | - | `` (empty, produces leading `/`) | `/` |
| Module | - | `` (empty, produces leading `/`) | `/` |
| Root Assembly | - | `Q{ns}name` | `Q{http://example.com}catalog` |
| Assembly/Field | UNGROUPED | `Q{ns}name[pos]` | `Q{http://example.com}control[1]` |
| Assembly/Field | GROUPED | `Q{ns}wrapper[1]/Q{ns}name[pos]` | `Q{http://example.com}groups[1]/Q{http://example.com}group[1]` |
| Flag | - | `@Q{ns}name` | `@Q{http://example.com}id` |

#### XML Grouping Behavior

- **UNGROUPED**: Item elements appear directly: `/parent[1]/item[1]`
- **GROUPED**: Wrapper element included: `/parent[1]/items[1]/item[1]`

The wrapper element name comes from `IGroupable.getEffectiveXmlGroupAsQName()`.

### JsonPointerFormatter Specification

**Format**: `/property/index/property` (RFC 6901 compliant)

#### Node Type Formatting

| Node Type | Condition | Format | Example |
|-----------|-----------|--------|---------|
| Document | - | `` (empty, produces leading `/`) | `/` |
| Module | - | `` (empty, produces leading `/`) | `/` |
| Root Assembly | - | `jsonName` | `catalog` |
| Assembly/Field | NONE | `jsonName` | `control` |
| Assembly/Field | LIST | `jsonName/index` (0-based) | `controls/0` |
| Assembly/Field | SINGLETON_OR_LIST, 1 sibling | `jsonName` | `control` |
| Assembly/Field | SINGLETON_OR_LIST, >1 siblings | `jsonName/index` (0-based) | `controls/0` |
| Assembly/Field | KEYED | `jsonName/keyValue` | `controls/ac-1` |
| Flag | - | `jsonName` | `id` |
| Field Value | has value key | `jsonName/valueKeyName` | `field/STRVALUE` |

#### JSON Grouping Behavior

- **NONE**: Single object, no indexing
- **LIST**: Always array, use 0-based index
- **SINGLETON_OR_LIST**: Check sibling count at runtime
  - 1 sibling → singleton, no index
  - >1 siblings → array, use 0-based index
- **KEYED**: Object keyed by flag value, use actual key value from JSON key flag

#### JSON Key Handling (KEYED collections)

For KEYED collections, read the key value from the node item:
1. Get JSON key flag instance via `getInstance().getJsonKey()` or `getInstance().getEffectiveJsonKey()`
2. Get flag node item from the current node
3. Read flag's atomic value as the key

#### JSON Value Key Handling

For fields with JSON value key configured:
1. Check `IFieldDefinition.hasJsonValueKeyFlagInstance()`
2. If flag-based: get value from `getJsonValueKeyFlagInstance()`
3. If static: get name from `getEffectiveJsonValueKeyName()`

#### RFC 6901 Escaping

All property names must be escaped:
- `~` → `~0`
- `/` → `~1`

### API Changes

Add constants to `IPathFormatter`:

```java
/**
 * A path formatter that produces XPath 3.1 paths with EQName-qualified names.
 */
@NonNull
IPathFormatter XPATH_PATH_FORMATTER = new XPathFormatter();

/**
 * A path formatter that produces RFC 6901 JSON Pointer paths.
 */
@NonNull
IPathFormatter JSON_POINTER_PATH_FORMATTER = new JsonPointerFormatter();
```

## Implementation Tasks

### Phase 1: XPathFormatter
- [x] Create `XPathFormatter.java` implementing `IPathFormatter`
- [x] Implement `formatDocument()` - return empty string
- [x] Implement `formatMetaschema()` - return empty string
- [x] Implement `formatRootAssembly()` - return EQName
- [x] Implement `formatAssembly()` - handle GROUPED/UNGROUPED, return EQName with position
- [x] Implement `formatAssembly(IAssemblyInstanceGroupedNodeItem)` - same pattern
- [x] Implement `formatField()` - handle GROUPED/UNGROUPED, return EQName with position
- [x] Implement `formatFlag()` - return `@` + EQName
- [x] Add helper method for XML group wrapper element
- [x] Add `XPATH_PATH_FORMATTER` constant to `IPathFormatter`
- [x] Create `XPathFormatterTest.java` with comprehensive tests (26 tests)

### Phase 2: JsonPointerFormatter
- [x] Create `JsonPointerFormatter.java` implementing `IPathFormatter`
- [x] Implement RFC 6901 escaping helper method (`escapeJsonPointer`)
- [x] Implement `formatDocument()` - return empty string
- [x] Implement `formatMetaschema()` - return empty string
- [x] Implement `formatRootAssembly()` - return escaped JSON name
- [x] Implement `formatAssembly()` - handle all JSON grouping behaviors
- [x] Implement `formatAssembly(IAssemblyInstanceGroupedNodeItem)` - same pattern
- [x] Implement `formatField()` - handle grouping + optional value key
- [x] Implement `formatFlag()` - return escaped JSON name (no @ prefix)
- [x] Add helper for KEYED key value extraction (`getJsonKeyValue()`) with warning on fallback
- [x] Add helper for sibling count check (SINGLETON_OR_LIST) - `countSiblings()` method
- [ ] Add helper for JSON value key resolution (deferred - not needed for basic path formatting)
- [x] Add `JSON_POINTER_PATH_FORMATTER` constant to `IPathFormatter`
- [x] Create `JsonPointerFormatterTest.java` with comprehensive tests (21 tests)

### Phase 3: Cleanup & Documentation
- [x] Remove or replace commented `JsonPathFormatter.java`
- [x] Update `package-info.java` documentation
- [x] Add Javadoc to all public APIs
- [x] Run full test suite
- [x] Run `mvn install -PCI -Prelease` verification

### Phase 4: Format-Based Selection and CLI Integration
- [ ] Create `PathFormatSelection` enum in `core/.../metapath/format/`
- [ ] Add `resolveFormatter()` static method to `IPathFormatter`
- [ ] Add convenience method to `AbstractConstraintValidationHandler`
- [ ] Add `--path-format` option to `AbstractValidateContentCommand`
- [ ] Parse option in validation command executor
- [ ] Pass resolved formatter to validation handler
- [ ] Update `LoggingValidationHandler` to use configured formatter for constraint findings
- [ ] Add unit tests for `resolveFormatter()` logic
- [ ] Add integration tests for `--path-format` CLI option
- [ ] Update CLI help documentation

## File Changes

### Phase 1-3 Files (Completed)

#### New Files
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/XPathFormatter.java`
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/JsonPointerFormatter.java`
- `core/src/test/java/gov/nist/secauto/metaschema/core/metapath/format/XPathFormatterTest.java`
- `core/src/test/java/gov/nist/secauto/metaschema/core/metapath/format/JsonPointerFormatterTest.java`

#### Modified Files
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/IPathFormatter.java` - Add constants
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/package-info.java` - Update docs

#### Deleted Files
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/JsonPathFormatter.java` - Remove commented code

### Phase 4 Files (Planned)

#### New Files
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/PathFormatSelection.java` - Enum for path format options
- `core/src/test/java/gov/nist/secauto/metaschema/core/metapath/format/PathFormatterResolverTest.java` - Tests for resolver logic

#### Modified Files
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/IPathFormatter.java` - Add `resolveFormatter()` method
- `core/src/main/java/gov/nist/secauto/metaschema/core/model/constraint/AbstractConstraintValidationHandler.java` - Add convenience method
- `metaschema-cli/src/main/java/gov/nist/secauto/metaschema/cli/commands/AbstractValidateContentCommand.java` - Add `--path-format` option
- `metaschema-cli/src/main/java/gov/nist/secauto/metaschema/cli/processor/LoggingValidationHandler.java` - Use configured formatter

## Code Examples

### XPathFormatter - Handling GROUPED

```java
@Override
public String formatAssembly(IAssemblyNodeItem assembly) {
  StringBuilder builder = new StringBuilder();

  // Check for XML grouping wrapper
  INamedModelInstance instance = assembly.getInstance();
  if (instance != null && instance.getXmlGroupAsBehavior() == XmlGroupAsBehavior.GROUPED) {
    IEnhancedQName wrapperQName = instance.getEffectiveXmlGroupAsQName();
    if (wrapperQName != null) {
      builder.append(wrapperQName.toEQName())
          .append("[1]/");
    }
  }

  builder.append(assembly.getQName().toEQName())
      .append('[')
      .append(assembly.getPosition())
      .append(']');

  return builder.toString();
}
```

### JsonPointerFormatter - Handling KEYED

```java
private String formatModelItem(IModelNodeItem<?, ?> item) {
  INamedModelInstance instance = item.getInstance();
  if (instance == null) {
    return escapeJsonPointer(item.getQName().getLocalName());
  }

  String jsonName = escapeJsonPointer(instance.getJsonName());
  JsonGroupAsBehavior behavior = instance.getJsonGroupAsBehavior();

  switch (behavior) {
    case KEYED:
      String keyValue = getJsonKeyValue(item, instance);
      return jsonName + "/" + escapeJsonPointer(keyValue);
    case LIST:
      return jsonName + "/" + (item.getPosition() - 1);
    case SINGLETON_OR_LIST:
      int siblingCount = countSiblings(item);
      if (siblingCount > 1) {
        return jsonName + "/" + (item.getPosition() - 1);
      }
      return jsonName;
    case NONE:
    default:
      return jsonName;
  }
}

private String getJsonKeyValue(IModelNodeItem<?, ?> item, INamedModelInstance instance) {
  IFlagInstance keyFlag = instance.getEffectiveJsonKey();
  if (keyFlag != null) {
    IFlagNodeItem flagItem = item.getFlagByName(keyFlag.getEffectiveName());
    if (flagItem != null) {
      return flagItem.toAtomicItem().asString();
    }
  }
  return String.valueOf(item.getPosition() - 1); // fallback
}
```

### RFC 6901 Escaping

```java
@NonNull
private static String escapeJsonPointer(@NonNull String value) {
  // Order matters: escape ~ first, then /
  return value.replace("~", "~0").replace("/", "~1");
}
```

## Testing Strategy

### XPathFormatter Test Cases

1. **Document path** - Verify produces leading `/`
2. **Root assembly** - Verify `Q{ns}name` format
3. **Nested assembly UNGROUPED** - Verify `/Q{ns}parent[1]/Q{ns}child[1]`
4. **Nested assembly GROUPED** - Verify `/Q{ns}parent[1]/Q{ns}wrapper[1]/Q{ns}child[1]`
5. **Field with position** - Verify position predicates
6. **Flag** - Verify `@Q{ns}name` format
7. **Empty namespace** - Verify handles empty namespace correctly
8. **Special characters** - Verify EQName handles special chars

### JsonPointerFormatter Test Cases

1. **Document path** - Verify produces leading `/`
2. **Root assembly** - Verify `/name` format
3. **NONE grouping** - Verify `/parent/child`
4. **LIST grouping** - Verify `/parent/children/0` (0-based)
5. **SINGLETON_OR_LIST single** - Verify `/parent/child` (no index)
6. **SINGLETON_OR_LIST multiple** - Verify `/parent/children/0` (with index)
7. **KEYED grouping** - Verify `/parent/children/key-value`
8. **Flag** - Verify no `@` prefix
9. **Field with value key** - Verify `/field/STRVALUE`
10. **RFC 6901 escaping** - Verify `~` → `~0`, `/` → `~1`
11. **JSON name vs effective name** - Verify uses `getJsonName()`

### Phase 4: Path Format Selection Test Cases

1. **resolveFormatter AUTO with JSON format** - Returns `JSON_POINTER_PATH_FORMATTER`
2. **resolveFormatter AUTO with YAML format** - Returns `JSON_POINTER_PATH_FORMATTER`
3. **resolveFormatter AUTO with XML format** - Returns `XPATH_PATH_FORMATTER`
4. **resolveFormatter AUTO with null format** - Returns `METAPATH_PATH_FORMATER` (fallback)
5. **resolveFormatter METAPATH explicit** - Returns `METAPATH_PATH_FORMATER` regardless of format
6. **resolveFormatter XPATH explicit** - Returns `XPATH_PATH_FORMATTER` regardless of format
7. **resolveFormatter JSON_POINTER explicit** - Returns `JSON_POINTER_PATH_FORMATTER` regardless of format
8. **CLI --path-format=auto** - Auto-selects based on document format
9. **CLI --path-format=metapath** - Forces Metapath format regardless of document type
10. **CLI --path-format=jsonpointer** - Forces JSON Pointer format regardless of document type
11. **CLI --path-format=xpath** - Forces XPath format regardless of document type
12. **CLI no --path-format option** - Uses AUTO (format-based selection) as default
13. **CLI validate JSON without option** - Uses JSON Pointer paths
14. **CLI validate XML without option** - Uses XPath paths

### Test Infrastructure

- Use existing Metaschema test suite modules to load real documents
- Create mock node items for isolated unit tests
- Reference patterns from `DefaultConstraintValidatorTest.java`
- CLI integration tests in `metaschema-cli` module using Maven Invoker plugin

## Risks and Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| `toEQName()` returns unexpected format | XPath format breaks | Unit tests verifying expected output |
| `getJsonName()` returns null | NPE | Null check, fallback to `getEffectiveName()` |
| JSON key flag not found | Invalid path | Fallback to numeric index with warning |
| Sibling count expensive | Performance | Cache or optimize traversal |
| Special characters not escaped | Invalid JSON Pointer | Comprehensive escaping tests |

## Success Metrics

1. All new tests pass
2. Full build passes: `mvn install -PCI -Prelease`
3. No regressions in existing path formatting
4. Javadoc coverage for all public APIs
5. Checkstyle/PMD compliance
