/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

class XmlAnyContentTest {

  private static Element createElement(String name) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    Document doc = factory.newDocumentBuilder().newDocument();
    return doc.createElement(name);
  }

  @Test
  void testEmptyListIsEmpty() {
    XmlAnyContent content = new XmlAnyContent(Collections.emptyList());
    assertTrue(content.isEmpty(), "isEmpty() should return true for empty list");
  }

  @Test
  void testEmptyListGetElements() {
    XmlAnyContent content = new XmlAnyContent(Collections.emptyList());
    assertTrue(content.getElements().isEmpty(), "getElements() should return empty list");
  }

  @Test
  void testNonEmptyListIsNotEmpty() throws ParserConfigurationException {
    Element elem = createElement("test");
    XmlAnyContent content = new XmlAnyContent(List.of(elem));
    assertFalse(content.isEmpty(), "isEmpty() should return false for non-empty list");
  }

  @Test
  void testNonEmptyListGetElements() throws ParserConfigurationException {
    Element elem1 = createElement("first");
    Element elem2 = createElement("second");
    List<Element> elements = List.of(elem1, elem2);

    XmlAnyContent content = new XmlAnyContent(elements);
    List<Element> result = content.getElements();

    assertEquals(2, result.size(), "getElements() should return all elements");
    assertEquals(elem1, result.get(0), "First element should match");
    assertEquals(elem2, result.get(1), "Second element should match");
  }

  @Test
  void testReturnedListIsUnmodifiable() throws ParserConfigurationException {
    Element elem = createElement("test");
    List<Element> mutableList = new ArrayList<>();
    mutableList.add(elem);

    XmlAnyContent content = new XmlAnyContent(mutableList);
    List<Element> returned = content.getElements();

    assertThrows(UnsupportedOperationException.class, () -> returned.add(createElement("extra")),
        "Returned list should be unmodifiable");
  }

  @Test
  void testDefensiveCopyFromMutableInput() throws ParserConfigurationException {
    Element elem = createElement("original");
    List<Element> mutableList = new ArrayList<>();
    mutableList.add(elem);

    XmlAnyContent content = new XmlAnyContent(mutableList);

    // Modify the original list after construction
    mutableList.add(createElement("added-after"));

    assertEquals(1, content.getElements().size(),
        "XmlAnyContent should not be affected by changes to the original list");
  }
}
