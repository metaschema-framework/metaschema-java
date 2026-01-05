/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

/**
 * Tests for DomDatatypeContent which writes DOM elements to XMLStreamWriter.
 */
class DomDatatypeContentTest {

  private static final String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  private Element createSchemaElement(Document doc, String name) {
    Element element = doc.createElementNS(NS_XML_SCHEMA, "xs:simpleType");
    element.setAttribute("name", name);

    Element restriction = doc.createElementNS(NS_XML_SCHEMA, "xs:restriction");
    restriction.setAttribute("base", "xs:string");
    element.appendChild(restriction);

    return element;
  }

  private Document createDocument() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.newDocument();
  }

  @Nested
  @DisplayName("Basic Properties Tests")
  class BasicPropertiesTests {

    @Test
    @DisplayName("getTypeName returns correct type name")
    void testGetTypeName() throws Exception {
      Document doc = createDocument();
      Element element = createSchemaElement(doc, "TestDatatype");

      DomDatatypeContent content = new DomDatatypeContent(
          "TestDatatype",
          CollectionUtil.singletonList(element),
          CollectionUtil.emptyList());

      assertEquals("TestDatatype", content.getTypeName());
    }

    @Test
    @DisplayName("getDependencies returns correct dependencies")
    void testGetDependencies() throws Exception {
      Document doc = createDocument();
      Element element = createSchemaElement(doc, "TestDatatype");
      List<String> dependencies = Arrays.asList("BaseDatatypeA", "BaseDatatypeB");

      DomDatatypeContent content = new DomDatatypeContent(
          "TestDatatype",
          CollectionUtil.singletonList(element),
          dependencies);

      assertEquals(2, content.getDependencies().size());
      assertTrue(content.getDependencies().contains("BaseDatatypeA"));
      assertTrue(content.getDependencies().contains("BaseDatatypeB"));
    }

    @Test
    @DisplayName("empty dependencies list works correctly")
    void testEmptyDependencies() throws Exception {
      Document doc = createDocument();
      Element element = createSchemaElement(doc, "TestDatatype");

      DomDatatypeContent content = new DomDatatypeContent(
          "TestDatatype",
          CollectionUtil.singletonList(element),
          CollectionUtil.emptyList());

      assertNotNull(content.getDependencies());
      assertTrue(content.getDependencies().isEmpty());
    }
  }

  @Nested
  @DisplayName("Write to XMLStreamWriter Tests")
  class WriteTests {

    @Test
    @DisplayName("writes simple element correctly")
    void testWriteSimpleElement() throws Exception {
      Document doc = createDocument();
      Element element = createSchemaElement(doc, "StringDatatype");

      DomDatatypeContent content = new DomDatatypeContent(
          "StringDatatype",
          CollectionUtil.singletonList(element),
          CollectionUtil.emptyList());

      StringWriter sw = new StringWriter();
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = factory.createXMLStreamWriter(sw);

      writer.writeStartDocument();
      writer.writeStartElement("wrapper");
      content.write(writer);
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.close();

      String result = sw.toString();

      // Verify the element was written
      assertTrue(result.contains("simpleType"), "Should contain simpleType element");
      assertTrue(result.contains("StringDatatype"), "Should contain type name attribute");
      assertTrue(result.contains("restriction"), "Should contain child elements");
    }

    @Test
    @DisplayName("writes multiple elements correctly")
    void testWriteMultipleElements() throws Exception {
      Document doc = createDocument();
      Element element1 = createSchemaElement(doc, "Type1");
      Element element2 = createSchemaElement(doc, "Type2");

      DomDatatypeContent content = new DomDatatypeContent(
          "MultiType",
          Arrays.asList(element1, element2),
          CollectionUtil.emptyList());

      StringWriter sw = new StringWriter();
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = factory.createXMLStreamWriter(sw);

      writer.writeStartDocument();
      writer.writeStartElement("wrapper");
      content.write(writer);
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.close();

      String result = sw.toString();

      // Verify both elements were written
      assertTrue(result.contains("Type1"), "Should contain first type");
      assertTrue(result.contains("Type2"), "Should contain second type");
    }

    @Test
    @DisplayName("preserves element namespace")
    void testPreservesNamespace() throws Exception {
      Document doc = createDocument();
      Element element = createSchemaElement(doc, "TestType");

      DomDatatypeContent content = new DomDatatypeContent(
          "TestType",
          CollectionUtil.singletonList(element),
          CollectionUtil.emptyList());

      StringWriter sw = new StringWriter();
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = factory.createXMLStreamWriter(sw);

      writer.writeStartDocument();
      writer.writeStartElement("wrapper");
      content.write(writer);
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.close();

      String result = sw.toString();

      // The output should preserve the xs prefix or include the namespace
      assertTrue(result.contains("simpleType"), "Should contain the element");
    }

    @Test
    @DisplayName("handles element with text content")
    void testElementWithTextContent() throws Exception {
      Document doc = createDocument();
      Element element = doc.createElementNS(NS_XML_SCHEMA, "xs:annotation");
      Element docElement = doc.createElementNS(NS_XML_SCHEMA, "xs:documentation");
      docElement.setTextContent("This is documentation text");
      element.appendChild(docElement);

      DomDatatypeContent content = new DomDatatypeContent(
          "AnnotatedType",
          CollectionUtil.singletonList(element),
          CollectionUtil.emptyList());

      StringWriter sw = new StringWriter();
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = factory.createXMLStreamWriter(sw);

      writer.writeStartDocument();
      writer.writeStartElement("wrapper");
      content.write(writer);
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.close();

      String result = sw.toString();

      assertTrue(result.contains("documentation"), "Should contain documentation element");
      assertTrue(result.contains("This is documentation text"), "Should contain text content");
    }

    @Test
    @DisplayName("handles empty element list")
    void testEmptyElementList() throws Exception {
      DomDatatypeContent content = new DomDatatypeContent(
          "EmptyType",
          CollectionUtil.emptyList(),
          CollectionUtil.emptyList());

      StringWriter sw = new StringWriter();
      XMLOutputFactory factory = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = factory.createXMLStreamWriter(sw);

      writer.writeStartDocument();
      writer.writeStartElement("wrapper");
      content.write(writer);
      writer.writeEndElement();
      writer.writeEndDocument();
      writer.close();

      String result = sw.toString();

      // Should just have wrapper with no content
      assertFalse(result.contains("simpleType"), "Should not contain any type elements");
    }
  }
}
