# Implementation Plan: Remove Saxon and JDOM2 Dependencies

## Overview

This plan removes Saxon-HE, xmlresolver, JDOM2, and jaxen dependencies from the schemagen module by replacing them with standard Java XML APIs.

## PR Structure

This work will be completed in a single PR since the changes are tightly coupled and cannot function independently.

---

## TDD Requirement (MANDATORY)

**All phases MUST follow Test-Driven Development.**

### For New Components (IndentingXMLStreamWriter)

1. **Write tests FIRST** - Before any implementation code
2. **Verify tests FAIL** - For the expected reason (not compilation errors)
3. **Write minimal implementation** - Just enough to pass
4. **Refactor** - Clean up while keeping tests green

### For Replacing Existing Components (JDOM2 → Standard DOM)

1. **Write characterization tests FIRST** - Tests that capture existing behavior of JDOM2 classes
2. **Verify tests PASS** - With current JDOM2 implementation
3. **Create new implementation** - Using standard DOM/XPath APIs
4. **Verify tests PASS** - With new implementation (behavioral equivalence)
5. **Delete old code** - Only after tests confirm equivalence

**Enforcement:** No implementation code may be written until corresponding tests exist and verify the expected behavior.

---

## PR 1: Remove Saxon and JDOM2 Dependencies

**Branch**: `refactor/remove-saxon-jdom2`
**Target**: `develop`

### Phase 1: Create IndentingXMLStreamWriter

Create a streaming XML indentation wrapper to replace Saxon XSLT post-processing.

#### Files Changed

| File | Change Type |
|------|-------------|
| `schemagen/src/test/java/dev/metaschema/schemagen/xml/impl/IndentingXMLStreamWriterTest.java` | Add |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/IndentingXMLStreamWriter.java` | Add |

#### TDD Sequence

1. **Write tests first** for `IndentingXMLStreamWriter` covering ALL text productions:

   **Element Structure Tests:**
   - Test single element indentation
   - Test nested element indentation (2+ levels)
   - Test sibling elements at same level
   - Test empty elements (`<element/>`)
   - Test elements with only whitespace content

   **Text Content Tests (CRITICAL - must not corrupt):**
   - Test text content is NOT indented (no added whitespace inside text)
   - Test mixed content (text + child elements) preserves text exactly
   - Test inline elements within text (e.g., `<p>text <b>bold</b> more</p>`)
   - Test whitespace-only text nodes are preserved
   - Test text with leading/trailing whitespace is preserved

   **Special Content Tests:**
   - Test CDATA sections are not indented internally
   - Test comments are properly indented
   - Test processing instructions are properly indented
   - Test attributes (no indentation effect)
   - Test namespace declarations

   **Schema Documentation Tests (xs:documentation with XHTML):**
   - Test `<xs:documentation>` containing `<xhtml:p>` elements
   - Test `<xs:documentation>` containing `<xhtml:b>` inline elements
   - Test nested XHTML (paragraphs containing bold/italic)
   - Verify no spurious whitespace added inside inline elements

2. **Verify tests fail** - Class doesn't exist yet
3. **Implement** `IndentingXMLStreamWriter`
4. **Verify tests pass**

#### Acceptance Criteria

- [ ] Create `IndentingXMLStreamWriterTest` with comprehensive tests for all text productions
- [ ] Tests cover element indentation, text preservation, mixed content, CDATA, comments, PIs
- [ ] Tests specifically verify XHTML documentation content is not corrupted
- [ ] Verify tests fail (class not found)
- [ ] Create `IndentingXMLStreamWriter` class implementing `XMLStreamWriter`
- [ ] Wrapper delegates all calls to underlying writer
- [ ] Inserts newline + indentation before start elements (when not in mixed content)
- [ ] Inserts newline before end elements (at parent indent level, when not in mixed content)
- [ ] Tracks nesting depth for proper indentation
- [ ] Tracks "mixed content mode" to suppress indentation when text has been written
- [ ] Handles edge cases (empty elements, CDATA, comments, PIs)
- [ ] All tests pass

---

### Phase 2: Replace JDOM2 with Standard DOM/XPath

Replace JDOM2 XML parsing and XPath with javax.xml APIs.

#### Files Changed

| File | Change Type |
|------|-------------|
| `schemagen/src/test/java/dev/metaschema/schemagen/xml/impl/XmlSchemaLoaderTest.java` | Add |
| `schemagen/src/test/java/dev/metaschema/schemagen/xml/impl/DomDatatypeContentTest.java` | Add |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/XmlSchemaLoader.java` | Add |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/DomDatatypeContent.java` | Add |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/JDom2XmlSchemaLoader.java` | Delete |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/JDom2DatatypeContent.java` | Delete |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/AbstractXmlDatatypeProvider.java` | Modify |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/XmlCoreDatatypeProvider.java` | Modify |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/AbstractXmlMarkupDatatypeProvider.java` | Modify |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/XmlProseBaseDatatypeProvider.java` | Modify |

#### TDD Sequence (Characterization Tests First)

**Step 1: Write characterization tests against existing JDOM2 implementation**

1. Create `XmlSchemaLoaderTest` that tests `JDom2XmlSchemaLoader`:
   - Test loading XSD from InputStream
   - Test XPath query `/xs:schema/xs:simpleType` returns expected element count and names
   - Test XPath query `/xs:schema/*` returns all child elements
   - Test XPath query `.//@base` returns attribute values
   - Test namespace handling in XPath
2. Create `DomDatatypeContentTest` that tests `JDom2DatatypeContent`:
   - Test serialization of JDOM2 element to XMLStreamWriter produces expected XML
   - Test multiple elements serialization
   - Test dependency list handling
3. **Verify tests PASS** with current JDOM2 implementation

**Step 2: Create new implementations that pass the same tests**

4. Create `XmlSchemaLoader` using standard `DocumentBuilderFactory` and `javax.xml.xpath.XPath`
5. Create `DomDatatypeContent` using `org.w3c.dom.Element` and `Transformer`
6. Update tests to use new implementations (or parameterize to test both)
7. **Verify tests PASS** with new implementations

**Step 3: Switch over and clean up**

8. Update provider classes to use new implementations
9. Delete JDOM2 classes after all tests confirm equivalence

#### Acceptance Criteria

- [ ] Create `XmlSchemaLoaderTest` with characterization tests for existing JDOM2 behavior
- [ ] Create `DomDatatypeContentTest` with characterization tests for existing JDOM2 behavior
- [ ] Verify characterization tests PASS with JDOM2 implementation
- [ ] Create `XmlSchemaLoader` using `DocumentBuilderFactory` and `javax.xml.xpath.XPath`
- [ ] Create `DomDatatypeContent` using `org.w3c.dom.Element` and `Transformer`
- [ ] Verify tests PASS with new implementations (behavioral equivalence confirmed)
- [ ] Update `AbstractXmlDatatypeProvider` to use new loader class
- [ ] Update `XmlCoreDatatypeProvider` with standard XPath for `.//@base` query
- [ ] Update `AbstractXmlMarkupDatatypeProvider` with standard DOM element handling
- [ ] Update `XmlProseBaseDatatypeProvider` if needed
- [ ] Delete `JDom2XmlSchemaLoader.java`
- [ ] Delete `JDom2DatatypeContent.java`
- [ ] All schemagen tests pass

---

### Phase 3: Update XmlSchemaGenerator

Replace Saxon XSLT transformation with IndentingXMLStreamWriter.

#### Files Changed

| File | Change Type |
|------|-------------|
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/XmlSchemaGenerator.java` | Modify |
| `schemagen/src/main/resources/identity.xsl` | Delete |

#### TDD Sequence (Characterization Tests First)

**Step 1: Verify existing integration tests capture current behavior**

1. Run existing `XmlSuiteTest` and other schema generation tests
2. Verify tests capture that generated schemas are:
   - Properly indented
   - Semantically correct
   - Contain expected elements and structure
3. If coverage is insufficient, add characterization tests for schema output format

**Step 2: Replace Saxon with IndentingXMLStreamWriter**

4. Modify `newWriter` to wrap `XMLStreamWriter2` with `IndentingXMLStreamWriter`
5. Remove the `generateFromModule` override that uses XSLT post-processing
6. Remove Saxon imports
7. **Verify existing tests PASS** - schemas still properly indented

**Step 3: Clean up**

8. Delete `identity.xsl` resource file

#### Acceptance Criteria

- [ ] Verify existing schema generation tests pass (baseline)
- [ ] Wrap `XMLStreamWriter2` with `IndentingXMLStreamWriter` in `newWriter` method
- [ ] Remove `generateFromModule` override that uses XSLT post-processing
- [ ] Remove Saxon imports
- [ ] Verify existing tests still pass (behavioral equivalence)
- [ ] Delete `identity.xsl` resource file
- [ ] Verify generated schemas are properly indented

---

### Phase 4: Update Module and Build Configuration

Remove dependencies from module-info.java and pom.xml files.

#### Files Changed

| File | Change Type |
|------|-------------|
| `schemagen/src/main/java/module-info.java` | Modify |
| `schemagen/pom.xml` | Modify |
| `pom.xml` | Modify |
| `THIRD_PARTY_LICENSES.md` | Modify |

#### Acceptance Criteria

- [ ] Remove `requires Saxon.HE;` from module-info.java
- [ ] Remove `requires org.jdom2;` from module-info.java
- [ ] Remove Saxon-HE dependency from schemagen/pom.xml
- [ ] Remove jdom2 dependency from schemagen/pom.xml
- [ ] Remove jaxen dependency from schemagen/pom.xml
- [ ] Remove Saxon-HE from root pom.xml dependencyManagement
- [ ] Remove xmlresolver from root pom.xml dependencyManagement (both library and data artifacts)
- [ ] Remove jdom2 from root pom.xml dependencyManagement
- [ ] Remove jaxen from root pom.xml dependencyManagement
- [ ] Remove version properties for removed dependencies (`dependency.saxon.version`, `dependency.xmlresolver.version`)
- [ ] Update THIRD_PARTY_LICENSES.md to remove Saxon and xmlresolver entries

---

### Phase 5: Update Tests

Update any tests that depend on JDOM2 or have whitespace-sensitive assertions.

#### Files Changed

| File | Change Type |
|------|-------------|
| `schemagen/src/test/java/dev/metaschema/schemagen/XmlSuiteTest.java` | Modify |

#### Acceptance Criteria

- [ ] Update `XmlSuiteTest` to use standard DOM/XPath instead of JDOM2
- [ ] Replace `StAXEventBuilder` with `DocumentBuilder`
- [ ] Replace JDOM2 XPath with `javax.xml.xpath.XPath`
- [ ] Verify all existing tests pass
- [ ] Add tests for `IndentingXMLStreamWriter`
- [ ] Add tests for `XmlSchemaLoader`

---

### Phase 6: Final Verification

#### Acceptance Criteria

- [ ] Run `mvn clean install -PCI -Prelease` - all checks pass
- [ ] Verify no Saxon or JDOM2 classes in compiled output
- [ ] Compare generated schema output before/after (semantic equivalence)
- [ ] Update PRD status in CLAUDE.md

---

## Files Changed Summary

### Test Files (TDD - Written First)

| File | Change Type |
|------|-------------|
| `schemagen/src/test/java/dev/metaschema/schemagen/xml/impl/IndentingXMLStreamWriterTest.java` | Add |
| `schemagen/src/test/java/dev/metaschema/schemagen/xml/impl/XmlSchemaLoaderTest.java` | Add |
| `schemagen/src/test/java/dev/metaschema/schemagen/xml/impl/DomDatatypeContentTest.java` | Add |
| `schemagen/src/test/java/dev/metaschema/schemagen/XmlSuiteTest.java` | Modify |

### Implementation Files

| File | Change Type |
|------|-------------|
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/IndentingXMLStreamWriter.java` | Add |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/XmlSchemaLoader.java` | Add |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/DomDatatypeContent.java` | Add |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/JDom2XmlSchemaLoader.java` | Delete |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/JDom2DatatypeContent.java` | Delete |
| `schemagen/src/main/resources/identity.xsl` | Delete |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/XmlSchemaGenerator.java` | Modify |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/AbstractXmlDatatypeProvider.java` | Modify |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/XmlCoreDatatypeProvider.java` | Modify |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/AbstractXmlMarkupDatatypeProvider.java` | Modify |
| `schemagen/src/main/java/dev/metaschema/schemagen/xml/impl/XmlProseBaseDatatypeProvider.java` | Modify |

### Configuration Files

| File | Change Type |
|------|-------------|
| `schemagen/src/main/java/module-info.java` | Modify |
| `schemagen/pom.xml` | Modify |
| `pom.xml` | Modify |
| `THIRD_PARTY_LICENSES.md` | Modify |

**Total files**: 19 (6 add, 3 delete, 10 modify)

---

## Technical Notes

### IndentingXMLStreamWriter Design

```java
public class IndentingXMLStreamWriter implements XMLStreamWriter {
    private final XMLStreamWriter delegate;
    private int depth = 0;
    private final Deque<Boolean> hasTextStack = new ArrayDeque<>();
    private boolean hasText = false;  // Current element's mixed content state
    private static final String INDENT = "  ";
    private static final String NEWLINE = "\n";

    // State tracking:
    // - depth: current nesting level for indentation
    // - hasTextStack: stack of hasText values for ancestor elements
    // - hasText: true if current element contains text (mixed content mode)
    //   When hasText is true, suppress indentation to preserve text formatting

    // Key methods:
    // - writeStartElement:
    //     if (!hasText) write newline + indent
    //     hasTextStack.push(hasText)  // save parent's state
    //     delegate.writeStartElement(...)
    //     depth++
    //     hasText = false  // reset for new element
    //
    // - writeEndElement:
    //     depth--
    //     if (!hasText) write newline + indent
    //     delegate.writeEndElement()
    //     hasText = hasTextStack.pop()  // restore parent's state
    //
    // - writeCharacters:
    //     if (text is not whitespace-only) hasText = true
    //     delegate.writeCharacters(...)
    //
    // - writeCData:
    //     hasText = true  // CDATA is text content
    //     delegate.writeCData(...)
    //
    // - writeComment:
    //     if (!hasText) write newline + indent
    //     delegate.writeComment(...)
    //
    // - writeProcessingInstruction:
    //     if (!hasText) write newline + indent
    //     delegate.writeProcessingInstruction(...)
}
```

**Stack-based parent state tracking:**

The `hasTextStack` preserves mixed content state across nested elements:

```text
<xs:documentation>           hasText=false, stack=[]
  Some text                  hasText=true,  stack=[]
  <xhtml:p>                  hasText=true,  stack=[true] (parent had text)
    More text                hasText=true,  stack=[true]
    <xhtml:b>                hasText=true,  stack=[true,true]
      bold                   hasText=true,  stack=[true,true]
    </xhtml:b>               pop → hasText=true
  </xhtml:p>                 pop → hasText=true
</xs:documentation>          no pop (root level)
```

This ensures that once text is written in an ancestor, all descendants suppress indentation.

**Critical behavior for mixed content:**

Input: `<p>Some text with <b>bold</b> words</p>`

- When `writeCharacters("Some text with ")` is called, set `hasText = true`
- When `writeStartElement("b")` is called, do NOT indent (hasText is true)
- When `writeEndElement()` for `b` is called, do NOT indent
- When `writeCharacters(" words")` is called, continue in text mode
- When `writeEndElement()` for `p` is called, do NOT indent

This preserves: `<p>Some text with <b>bold</b> words</p>` without spurious whitespace.

### XPath Namespace Handling

Standard Java XPath requires a `NamespaceContext` implementation:

```java
XPath xpath = XPathFactory.newInstance().newXPath();
xpath.setNamespaceContext(new NamespaceContext() {
    @Override
    public String getNamespaceURI(String prefix) {
        if ("xs".equals(prefix)) {
            return "http://www.w3.org/2001/XMLSchema";
        }
        return XMLConstants.NULL_NS_URI;
    }
    // ... other methods
});
```

### DOM Element Serialization

To write DOM elements to XMLStreamWriter:

```java
Transformer transformer = TransformerFactory.newInstance().newTransformer();
transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
transformer.transform(new DOMSource(element), new StAXResult(writer));
```

---

## Rollback Plan

If issues are discovered after merge:
1. Revert the PR commit
2. Re-add dependencies to pom.xml files
3. Restore deleted files from git history
