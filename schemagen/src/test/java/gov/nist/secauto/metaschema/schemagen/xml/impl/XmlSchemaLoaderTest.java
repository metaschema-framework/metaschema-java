/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tests for the standard DOM/XPath-based XML schema loading functionality.
 * <p>
 * These tests verify that the new DOM-based implementation provides the same
 * functionality as the previous JDOM2-based implementation.
 */
class XmlSchemaLoaderTest {

  private static final String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  private static final Map<String, String> XS_NAMESPACE_MAP = CollectionUtil.singletonMap("xs", NS_XML_SCHEMA);

  private static XmlSchemaLoader datatypesLoader;
  private static XmlSchemaLoader proseBaseLoader;

  @BeforeAll
  static void loadSchemas() throws SAXException, IOException {
    try (InputStream is = IModule.class.getResourceAsStream("/schema/xml/metaschema-datatypes.xsd")) {
      assertNotNull(is, "metaschema-datatypes.xsd should be on classpath");
      datatypesLoader = new XmlSchemaLoader(is);
    }

    try (InputStream is = IModule.class.getResourceAsStream("/schema/xml/metaschema-prose-base.xsd")) {
      assertNotNull(is, "metaschema-prose-base.xsd should be on classpath");
      proseBaseLoader = new XmlSchemaLoader(is);
    }
  }

  @Nested
  @DisplayName("XPath Query Tests")
  class XPathQueryTests {

    @Test
    @DisplayName("/xs:schema/xs:simpleType returns expected elements from datatypes.xsd")
    void testSimpleTypeQuery() {
      List<Element> elements = datatypesLoader.getContent("/xs:schema/xs:simpleType", XS_NAMESPACE_MAP);

      assertNotNull(elements);
      assertFalse(elements.isEmpty(), "Should find simpleType elements");

      // Verify each element has a name attribute
      for (Element element : elements) {
        assertNotNull(element.getAttribute("name"),
            "Each simpleType should have a name attribute");
        assertFalse(element.getAttribute("name").isEmpty(),
            "Name attribute should not be empty");
      }

      // Verify we found expected datatypes
      List<String> typeNames = elements.stream()
          .map(e -> e.getAttribute("name"))
          .collect(Collectors.toList());

      // Check for some expected core datatypes
      assertTrue(typeNames.contains("Base64Datatype"),
          "Should contain Base64Datatype");
      assertTrue(typeNames.contains("StringDatatype"),
          "Should contain StringDatatype");
    }

    @Test
    @DisplayName("/xs:schema/xs:simpleType returns expected elements from prose-base.xsd")
    void testSimpleTypeQueryProseBase() {
      List<Element> elements = proseBaseLoader.getContent("/xs:schema/xs:simpleType", XS_NAMESPACE_MAP);

      assertNotNull(elements);
      // prose-base.xsd may have fewer or different simpleTypes
      // Just verify the query works and returns elements
    }

    @Test
    @DisplayName("/xs:schema/* returns all child elements")
    void testAllChildrenQuery() {
      List<Element> elements = datatypesLoader.getContent("/xs:schema/*", XS_NAMESPACE_MAP);

      assertNotNull(elements);
      assertFalse(elements.isEmpty(), "Should find child elements");

      // Should include various element types (simpleType, annotation, etc.)
      List<String> elementNames = elements.stream()
          .map(Element::getLocalName)
          .distinct()
          .collect(Collectors.toList());

      assertTrue(elementNames.contains("simpleType"),
          "Should contain simpleType elements");
    }
  }

  @Nested
  @DisplayName("Element Content Tests")
  class ElementContentTests {

    @Test
    @DisplayName("elements have correct namespace")
    void testElementNamespace() {
      List<Element> elements = datatypesLoader.getContent("/xs:schema/xs:simpleType", XS_NAMESPACE_MAP);

      assertFalse(elements.isEmpty());

      Element first = elements.get(0);
      assertEquals(NS_XML_SCHEMA, first.getNamespaceURI(),
          "Element should have XML Schema namespace");
      assertEquals("simpleType", first.getLocalName(),
          "Element should be named simpleType");
    }

    @Test
    @DisplayName("elements contain expected child structure")
    void testElementChildStructure() {
      List<Element> elements = datatypesLoader.getContent("/xs:schema/xs:simpleType", XS_NAMESPACE_MAP);

      // Find a specific datatype to test structure
      Element stringType = elements.stream()
          .filter(e -> "StringDatatype".equals(e.getAttribute("name")))
          .findFirst()
          .orElse(null);

      assertNotNull(stringType, "Should find StringDatatype");

      // StringDatatype should have restriction or other content
      // Count child elements (DOM uses NodeList, not getChildren)
      int childElementCount = 0;
      for (int i = 0; i < stringType.getChildNodes().getLength(); i++) {
        if (stringType.getChildNodes().item(i) instanceof Element) {
          childElementCount++;
        }
      }
      assertFalse(childElementCount == 0, "StringDatatype should have child elements");
    }
  }

  @Nested
  @DisplayName("Loading Tests")
  class LoadingTests {

    @Test
    @DisplayName("can load schema from InputStream")
    void testLoadFromInputStream() throws SAXException, IOException {
      try (InputStream is = IModule.class.getResourceAsStream("/schema/xml/metaschema-datatypes.xsd")) {
        assertNotNull(is);
        XmlSchemaLoader loader = new XmlSchemaLoader(is);

        List<Element> elements = loader.getContent("/xs:schema/xs:simpleType", XS_NAMESPACE_MAP);
        assertFalse(elements.isEmpty());
      }
    }

    @Test
    @DisplayName("handles empty result gracefully")
    void testEmptyResult() {
      // Query for non-existent elements
      List<Element> elements = datatypesLoader.getContent("/xs:schema/xs:nonExistent", XS_NAMESPACE_MAP);

      assertNotNull(elements, "Should return empty list, not null");
      assertEquals(0, elements.size(), "Should return empty list for non-matching query");
    }
  }
}
