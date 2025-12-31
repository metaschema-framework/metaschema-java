/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Tests for {@link IndentingXMLStreamWriter}.
 * <p>
 * These tests verify that the indenting wrapper correctly formats XML output
 * while preserving text content exactly (no spurious whitespace in mixed
 * content).
 */
class IndentingXMLStreamWriterTest {

  private static final String NEWLINE = "\n";
  private static final String INDENT = "  ";

  /**
   * Helper to create an IndentingXMLStreamWriter wrapping a StringWriter.
   */
  private static IndentingXMLStreamWriter createWriter(StringWriter stringWriter) throws XMLStreamException {
    XMLOutputFactory factory = XMLOutputFactory.newFactory();
    XMLStreamWriter delegate = factory.createXMLStreamWriter(stringWriter);
    return new IndentingXMLStreamWriter(delegate);
  }

  @Nested
  @DisplayName("Element Structure Tests")
  class ElementStructureTests {

    @Test
    @DisplayName("single element is indented")
    void testSingleElementIndentation() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      // Note: StAX implementations may use single or double quotes in XML declaration
      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root/>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("nested elements are indented at each level")
    void testNestedElementIndentation() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("child");
        writer.writeStartElement("grandchild");
        writer.writeEndElement(); // grandchild
        writer.writeEndElement(); // child
        writer.writeEndElement(); // root
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>" + NEWLINE
          + INDENT + "<child>" + NEWLINE
          + INDENT + INDENT + "<grandchild/>" + NEWLINE
          + INDENT + "</child>" + NEWLINE
          + "</root>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("sibling elements at same level")
    void testSiblingElements() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("child1");
        writer.writeEndElement();
        writer.writeStartElement("child2");
        writer.writeEndElement();
        writer.writeStartElement("child3");
        writer.writeEndElement();
        writer.writeEndElement(); // root
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>" + NEWLINE
          + INDENT + "<child1/>" + NEWLINE
          + INDENT + "<child2/>" + NEWLINE
          + INDENT + "<child3/>" + NEWLINE
          + "</root>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("empty element with attributes")
    void testEmptyElementWithAttributes() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("element");
        writer.writeAttribute("name", "value");
        writer.writeAttribute("other", "data");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>" + NEWLINE
          + INDENT + "<element name=\"value\" other=\"data\"/>" + NEWLINE
          + "</root>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("namespace declarations")
    void testNamespaceDeclarations() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("xs", "schema", "http://www.w3.org/2001/XMLSchema");
        writer.writeNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        writer.writeStartElement("xs", "element", "http://www.w3.org/2001/XMLSchema");
        writer.writeAttribute("name", "test");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      // Just verify it doesn't throw and produces valid output
      String result = sw.toString();
      assertTrue(result.contains("<xs:schema"));
      assertTrue(result.contains("<xs:element"));
    }
  }

  @Nested
  @DisplayName("Text Content Tests - CRITICAL: must not corrupt text")
  class TextContentTests {

    @Test
    @DisplayName("text content is NOT indented")
    void testTextContentNotIndented() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("element");
        writer.writeCharacters("Some text content");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>" + NEWLINE
          + INDENT + "<element>Some text content</element>" + NEWLINE
          + "</root>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("mixed content preserves text exactly")
    void testMixedContentPreservesText() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("p");
        writer.writeCharacters("Some text with ");
        writer.writeStartElement("b");
        writer.writeCharacters("bold");
        writer.writeEndElement(); // b
        writer.writeCharacters(" words");
        writer.writeEndElement(); // p
        writer.writeEndDocument();
      }

      // Mixed content must NOT have any added whitespace
      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<p>Some text with <b>bold</b> words</p>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("inline elements within text do not gain whitespace")
    void testInlineElementsNoWhitespace() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("p");
        writer.writeCharacters("Click ");
        writer.writeStartElement("a");
        writer.writeAttribute("href", "http://example.com");
        writer.writeCharacters("here");
        writer.writeEndElement(); // a
        writer.writeCharacters(" for more info.");
        writer.writeEndElement(); // p
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<p>Click <a href=\"http://example.com\">here</a> for more info.</p>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("text with leading/trailing whitespace is preserved")
    void testTextWhitespacePreserved() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("element");
        writer.writeCharacters("  leading and trailing spaces  ");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>" + NEWLINE
          + INDENT + "<element>  leading and trailing spaces  </element>" + NEWLINE
          + "</root>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("whitespace-only text triggers mixed content mode")
    void testWhitespaceOnlyTextPreserved() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("element");
        writer.writeCharacters("   ");
        writer.writeStartElement("child");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      // Once whitespace text is written, we're in mixed content mode
      // The child element should NOT get indentation added
      String result = sw.toString();
      assertTrue(result.contains("<element>   <child/></element>"));
    }

    @Test
    @DisplayName("nested mixed content elements")
    void testNestedMixedContent() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("doc");
        writer.writeCharacters("Text ");
        writer.writeStartElement("em");
        writer.writeCharacters("with ");
        writer.writeStartElement("strong");
        writer.writeCharacters("nested");
        writer.writeEndElement(); // strong
        writer.writeCharacters(" emphasis");
        writer.writeEndElement(); // em
        writer.writeCharacters(" content.");
        writer.writeEndElement(); // doc
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<doc>Text <em>with <strong>nested</strong> emphasis</em> content.</doc>";
      assertEquals(expected, sw.toString());
    }
  }

  @Nested
  @DisplayName("Special Content Tests")
  class SpecialContentTests {

    @Test
    @DisplayName("CDATA sections are not indented internally")
    void testCDataNotIndented() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("element");
        writer.writeCData("CDATA content with <special> chars");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      String result = sw.toString();
      assertTrue(result.contains("<![CDATA[CDATA content with <special> chars]]>"));
    }

    @Test
    @DisplayName("comments are properly indented")
    void testCommentsIndented() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeComment("This is a comment");
        writer.writeStartElement("child");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>" + NEWLINE
          + INDENT + "<!--This is a comment-->" + NEWLINE
          + INDENT + "<child/>" + NEWLINE
          + "</root>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("processing instructions are properly indented")
    void testProcessingInstructionsIndented() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeProcessingInstruction("target", "data");
        writer.writeStartElement("child");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>" + NEWLINE
          + INDENT + "<?target data?>" + NEWLINE
          + INDENT + "<child/>" + NEWLINE
          + "</root>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("comments in mixed content do not add whitespace")
    void testCommentsInMixedContent() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("p");
        writer.writeCharacters("Before ");
        writer.writeComment("inline comment");
        writer.writeCharacters(" after");
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<p>Before <!--inline comment--> after</p>";
      assertEquals(expected, sw.toString());
    }
  }

  @Nested
  @DisplayName("Schema Documentation Tests (xs:documentation with XHTML)")
  class SchemaDocumentationTests {

    @Test
    @DisplayName("xs:documentation with xhtml:p elements")
    void testDocumentationWithParagraphs() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("xs", "schema", "http://www.w3.org/2001/XMLSchema");
        writer.writeStartElement("xs", "annotation", "http://www.w3.org/2001/XMLSchema");
        writer.writeStartElement("xs", "documentation", "http://www.w3.org/2001/XMLSchema");
        writer.writeStartElement("p", "p", "http://www.w3.org/1999/xhtml");
        writer.writeCharacters("This is documentation.");
        writer.writeEndElement(); // p
        writer.writeEndElement(); // documentation
        writer.writeEndElement(); // annotation
        writer.writeEndElement(); // schema
        writer.writeEndDocument();
      }

      String result = sw.toString();
      // The p element should be indented as element-only content
      // But the text inside p should not have added whitespace
      assertTrue(result.contains("<p:p>This is documentation.</p:p>"));
    }

    @Test
    @DisplayName("xs:documentation with inline xhtml:b elements")
    void testDocumentationWithBoldInline() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("xs", "documentation", "http://www.w3.org/2001/XMLSchema");
        writer.writeStartElement("p", "p", "http://www.w3.org/1999/xhtml");
        writer.writeCharacters("Text with ");
        writer.writeStartElement("b", "b", "http://www.w3.org/1999/xhtml");
        writer.writeCharacters("bold");
        writer.writeEndElement(); // b
        writer.writeCharacters(" content.");
        writer.writeEndElement(); // p
        writer.writeEndElement(); // documentation
        writer.writeEndDocument();
      }

      String result = sw.toString();
      // No whitespace corruption in mixed content
      assertTrue(result.contains("<p:p>Text with <b:b>bold</b:b> content.</p:p>"));
    }

    @Test
    @DisplayName("nested XHTML with paragraphs containing bold/italic")
    void testNestedXhtml() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("doc");
        writer.writeStartElement("p");
        writer.writeCharacters("A paragraph with ");
        writer.writeStartElement("b");
        writer.writeCharacters("bold and ");
        writer.writeStartElement("i");
        writer.writeCharacters("italic");
        writer.writeEndElement(); // i
        writer.writeEndElement(); // b
        writer.writeCharacters(" text.");
        writer.writeEndElement(); // p
        writer.writeEndElement(); // doc
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<doc>" + NEWLINE
          + INDENT + "<p>A paragraph with <b>bold and <i>italic</i></b> text.</p>" + NEWLINE
          + "</doc>";
      assertEquals(expected, sw.toString());
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCaseTests {

    @Test
    @DisplayName("deeply nested elements")
    void testDeeplyNestedElements() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("l1");
        writer.writeStartElement("l2");
        writer.writeStartElement("l3");
        writer.writeStartElement("l4");
        writer.writeStartElement("l5");
        writer.writeEndElement(); // l5
        writer.writeEndElement(); // l4
        writer.writeEndElement(); // l3
        writer.writeEndElement(); // l2
        writer.writeEndElement(); // l1
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<l1>" + NEWLINE
          + INDENT + "<l2>" + NEWLINE
          + INDENT + INDENT + "<l3>" + NEWLINE
          + INDENT + INDENT + INDENT + "<l4>" + NEWLINE
          + INDENT + INDENT + INDENT + INDENT + "<l5/>" + NEWLINE
          + INDENT + INDENT + INDENT + "</l4>" + NEWLINE
          + INDENT + INDENT + "</l3>" + NEWLINE
          + INDENT + "</l2>" + NEWLINE
          + "</l1>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("mixed siblings: some with text, some without")
    void testMixedSiblings() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("empty");
        writer.writeEndElement();
        writer.writeStartElement("withText");
        writer.writeCharacters("content");
        writer.writeEndElement();
        writer.writeStartElement("alsoEmpty");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>" + NEWLINE
          + INDENT + "<empty/>" + NEWLINE
          + INDENT + "<withText>content</withText>" + NEWLINE
          + INDENT + "<alsoEmpty/>" + NEWLINE
          + "</root>";
      assertEquals(expected, sw.toString());
    }

    @Test
    @DisplayName("text after child element in parent")
    void testTextAfterChildElement() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeStartElement("child");
        writer.writeCharacters("child text");
        writer.writeEndElement();
        writer.writeCharacters("parent text after child");
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      // Once text is written to parent, subsequent content should not be indented
      String result = sw.toString();
      // The child was indented, but after the text, parent is in mixed content mode
      assertTrue(result.contains("</child>parent text after child</root>"));
    }

    @Test
    @DisplayName("element after text in same parent")
    void testElementAfterTextInSameParent() throws XMLStreamException {
      StringWriter sw = new StringWriter();
      try (IndentingXMLStreamWriter writer = createWriter(sw)) {
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("root");
        writer.writeCharacters("text first ");
        writer.writeStartElement("child");
        writer.writeEndElement();
        writer.writeCharacters(" text last");
        writer.writeEndElement();
        writer.writeEndDocument();
      }

      // Parent has text, so child should not be indented
      String expected = "<?xml version='1.0' encoding='UTF-8'?>" + NEWLINE
          + "<root>text first <child/> text last</root>";
      assertEquals(expected, sw.toString());
    }
  }
}
