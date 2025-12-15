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

## Technical Approach

### Architecture

Both formatters implement `IPathFormatter` interface:

```
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
- [ ] Create `XPathFormatter.java` implementing `IPathFormatter`
- [ ] Implement `formatDocument()` - return empty string
- [ ] Implement `formatMetaschema()` - return empty string
- [ ] Implement `formatRootAssembly()` - return EQName
- [ ] Implement `formatAssembly()` - handle GROUPED/UNGROUPED, return EQName with position
- [ ] Implement `formatAssembly(IAssemblyInstanceGroupedNodeItem)` - same pattern
- [ ] Implement `formatField()` - handle GROUPED/UNGROUPED, return EQName with position
- [ ] Implement `formatFlag()` - return `@` + EQName
- [ ] Add helper method for XML group wrapper element
- [ ] Add `XPATH_PATH_FORMATTER` constant to `IPathFormatter`
- [ ] Create `XPathFormatterTest.java` with comprehensive tests

### Phase 2: JsonPointerFormatter
- [ ] Create `JsonPointerFormatter.java` implementing `IPathFormatter`
- [ ] Implement RFC 6901 escaping helper method (`escapeJsonPointer`)
- [ ] Implement `formatDocument()` - return empty string
- [ ] Implement `formatMetaschema()` - return empty string
- [ ] Implement `formatRootAssembly()` - return escaped JSON name
- [ ] Implement `formatAssembly()` - handle all JSON grouping behaviors
- [ ] Implement `formatAssembly(IAssemblyInstanceGroupedNodeItem)` - same pattern
- [ ] Implement `formatField()` - handle grouping + optional value key
- [ ] Implement `formatFlag()` - return escaped JSON name (no @ prefix)
- [ ] Add helper for KEYED key value extraction
- [ ] Add helper for sibling count check (SINGLETON_OR_LIST)
- [ ] Add helper for JSON value key resolution
- [ ] Add `JSON_POINTER_PATH_FORMATTER` constant to `IPathFormatter`
- [ ] Create `JsonPointerFormatterTest.java` with comprehensive tests

### Phase 3: Cleanup & Documentation
- [ ] Remove or replace commented `JsonPathFormatter.java`
- [ ] Update `package-info.java` documentation
- [ ] Add Javadoc to all public APIs
- [ ] Run full test suite
- [ ] Run `mvn install -PCI -Prelease` verification

## File Changes

### New Files
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/XPathFormatter.java`
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/JsonPointerFormatter.java`
- `core/src/test/java/gov/nist/secauto/metaschema/core/metapath/format/XPathFormatterTest.java`
- `core/src/test/java/gov/nist/secauto/metaschema/core/metapath/format/JsonPointerFormatterTest.java`

### Modified Files
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/IPathFormatter.java` - Add constants
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/package-info.java` - Update docs

### Deleted Files
- `core/src/main/java/gov/nist/secauto/metaschema/core/metapath/format/JsonPathFormatter.java` - Remove commented code

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

### Test Infrastructure

- Use existing Metaschema test suite modules to load real documents
- Create mock node items for isolated unit tests
- Reference patterns from `DefaultConstraintValidatorTest.java`

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
