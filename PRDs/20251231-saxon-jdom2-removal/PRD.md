# PRD: Remove Saxon and JDOM2 Dependencies

## Problem Statement

The `schemagen` module depends on Saxon-HE (~5MB) and JDOM2 + jaxen (~500KB) for XML schema generation. These dependencies add significant size to the distribution and introduce external library dependencies where standard Java XML APIs would suffice.

### Current State

- **Saxon-HE** is used solely for an XSLT identity transform that adds indentation to generated XML schemas
- **xmlresolver** is a transitive dependency of Saxon (comment in pom.xml: "for saxon")
- **JDOM2** is used for:
  - Parsing XSD resource files containing datatype definitions
  - Executing XPath queries to extract schema elements
  - Writing DOM elements to XMLStreamWriter via StAXStreamOutputter
- **jaxen** provides XPath support for JDOM2

### XSD Resource Files

The following XSD files are loaded and processed via JDOM2 XPath:

| Resource | Loaded By | XPath Query |
|----------|-----------|-------------|
| `/schema/xml/metaschema-datatypes.xsd` | `XmlCoreDatatypeProvider` | `/xs:schema/xs:simpleType` |
| `/schema/xml/metaschema-markup-line.xsd` | `XmlMarkupLineDatatypeProvider` | `/xs:schema/*` |
| `/schema/xml/metaschema-markup-multiline.xsd` | `XmlMarkupMultilineDatatypeProvider` | `/xs:schema/*` |
| `/schema/xml/metaschema-prose-base.xsd` | `XmlProseBaseDatatypeProvider` | `/xs:schema/xs:simpleType` |

These files are packaged in the `core` module and accessed via `IModule.class.getResourceAsStream()`.

### Why This Matters

1. **Dependency bloat**: Saxon-HE alone is ~5MB, larger than many core modules
2. **Maintenance burden**: External dependencies require version updates and security monitoring
3. **Standard alternatives exist**: Java's built-in DOM, XPath, and Transformer APIs provide equivalent functionality
4. **Performance**: Current approach buffers entire schema for XSLT post-processing; streaming indentation is more efficient

## Goals

1. Remove Saxon-HE dependency from the project
2. Remove JDOM2 and jaxen dependencies from the project
3. Replace with standard Java XML APIs (no new dependencies)
4. Maintain identical schema generation output (except minor whitespace differences in documentation)
5. Improve performance through streaming indentation

## Non-Goals

- Changing the structure or content of generated schemas
- Modifying the public API of the schemagen module
- Adding new XML processing capabilities

## Development Methodology

### Test-Driven Development (MANDATORY)

All implementation MUST follow strict TDD:

1. **Write tests first** - Before any implementation code exists
2. **Watch tests fail** - Verify tests fail for the expected reason (not compilation errors)
3. **Write minimal code** - Implement just enough to make tests pass
4. **Refactor** - Clean up while keeping tests green

### TDD Sequence for This PRD

| Component | Test First | Then Implement |
|-----------|------------|----------------|
| `IndentingXMLStreamWriter` | Test indentation behavior with mock writer | Implement wrapper class |
| `XmlSchemaLoader` | Test XPath queries return expected elements | Implement DOM/XPath loader |
| `DomDatatypeContent` | Test DOM element serialization to XMLStreamWriter | Implement serialization |
| `XmlSchemaGenerator` changes | Verify existing tests pass with new approach | Remove Saxon, use IndentingXMLStreamWriter |

### Test Requirements

- **Characterization tests first**: Before replacing any existing code, write tests that capture current behavior
- **Verify tests pass**: With existing JDOM2/Saxon implementation
- **New classes**: 100% test coverage for public methods
- **Behavioral equivalence**: New implementations must pass the same tests as old implementations
- **Edge cases**: Empty documents, missing elements, malformed XML handling
- **Integration**: End-to-end schema generation tests must produce equivalent output

### Text Production Testing (CRITICAL)

The `IndentingXMLStreamWriter` must be tested against ALL XML text productions to ensure content is not corrupted:

| Production | Test Requirement |
|------------|------------------|
| Element content | Proper indentation at each nesting level |
| Text content | NO added whitespace - text must be preserved exactly |
| Mixed content | Text + child elements must not have spurious whitespace |
| CDATA sections | Content must not be modified |
| Comments | Properly indented, content preserved |
| Processing instructions | Properly indented |
| Attributes | No indentation effect |
| XHTML in xs:documentation | Inline elements (`<b>`, `<i>`) must not gain whitespace |

**Why this matters**: The original Saxon XSLT used `suppress-indentation="xhtml:b xhtml:p"` specifically to prevent whitespace corruption in schema documentation. Our replacement must handle this correctly through mixed content detection.

## Requirements

### Functional Requirements

1. **FR-1**: XML schemas generated after the change must be semantically equivalent to those generated before
2. **FR-2**: All existing schemagen tests must pass without modification (except whitespace assertions if any)
3. **FR-3**: XSD datatype resources must continue to be loaded and processed correctly
4. **FR-4**: Generated schemas must be properly indented for readability

### Technical Requirements

1. **TR-1**: Create `IndentingXMLStreamWriter` wrapper for streaming indentation
2. **TR-2**: Replace JDOM2 XML parsing with `javax.xml.parsers.DocumentBuilder`
3. **TR-3**: Replace JDOM2 XPath with `javax.xml.xpath.XPath`
4. **TR-4**: Replace JDOM2 element serialization with `javax.xml.transform.Transformer`
5. **TR-5**: Update `module-info.java` to remove Saxon.HE and org.jdom2 requirements
6. **TR-6**: Remove dependency declarations from pom.xml files

## Success Metrics

| Metric | Target |
|--------|--------|
| Dependencies removed | 4 (Saxon-HE, xmlresolver, jdom2, jaxen) |
| New dependencies added | 0 |
| Existing tests passing | 100% |
| JAR size reduction | ~5.5MB |
| Build verification | `mvn clean install -PCI -Prelease` passes |

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Whitespace differences in output | High | Low | Document as expected; only affects formatting in xs:documentation |
| XPath behavior differences | Low | Medium | Comprehensive test coverage for XSD loading |
| Performance regression | Low | Low | Streaming approach should be faster than buffered XSLT |

## Dependencies

- No blocking dependencies on other work
- This change is isolated to the `schemagen` module

## Design Decisions

### Interface Compatibility

The implementation will use the standard `XMLStreamWriter` interface rather than Woodstox's `XMLStreamWriter2` extension. Analysis shows that only standard `XMLStreamWriter` methods are used:

- `writeStartDocument`, `writeEndDocument`
- `writeStartElement`, `writeEndElement`
- `writeDefaultNamespace`, `writeNamespace`
- `writeAttribute`
- `flush`

This ensures compatibility with any StAX implementation.

### Mixed Content Detection

The `IndentingXMLStreamWriter` will use dynamic mixed content detection rather than element-specific suppression:

1. Track `hasText` flag per element level using a stack
2. When `writeCharacters()` is called with non-whitespace text, set `hasText = true`
3. When `hasText` is true, suppress indentation for child elements
4. When closing an element, pop the stack to restore parent's state

This approach:
- Is simpler than the Saxon XSLT's `suppress-indentation="xhtml:b xhtml:p"` approach
- Works correctly for any inline elements, not just a hardcoded list
- Automatically handles mixed content regardless of element names

### Line Endings and Configurability

| Setting | Value | Rationale |
|---------|-------|-----------|
| Line ending | `\n` (Unix) | Consistent across platforms; matches Saxon output |
| Indent size | 2 spaces | Fixed; matches existing output; no configurability needed |

### Acceptable Whitespace Differences

The following whitespace differences between Saxon XSLT and IndentingXMLStreamWriter output are acceptable:

1. **xs:documentation content**: Saxon preserves original formatting; new approach adds structure indentation
2. **Empty element spacing**: Minor differences in element-only content are acceptable
3. **Trailing whitespace**: Any trailing whitespace differences are acceptable

Semantic equivalence (XML parses to identical DOM) is required; formatting differences are acceptable.

## Existing Test Coverage

### Current Tests
- `XmlSuiteTest` - Integration tests for XML schema generation (uses JDOM2 for assertions)
- `JsonSuiteTest` - JSON schema generation (unaffected by this change)
- `MetaschemaModuleTest` - Module loading tests

### Tests Requiring Update
- `XmlSuiteTest` uses JDOM2 (`StAXEventBuilder`, `XPathExpression`) for test assertions
- These must be converted to standard DOM/XPath APIs

### New Tests Required
- `IndentingXMLStreamWriterTest` - Comprehensive text production tests
- `XmlSchemaLoaderTest` - Characterization tests for XPath queries
- `DomDatatypeContentTest` - DOM serialization tests

## Related Documents

- [Implementation Plan](./implementation-plan.md)
