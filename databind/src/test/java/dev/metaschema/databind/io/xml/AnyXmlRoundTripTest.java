/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import dev.metaschema.core.model.IAnyContent;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;
import dev.metaschema.databind.codegen.AbstractMetaschemaTest;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.test.AnyAssembly;

class AnyXmlRoundTripTest
    extends AbstractMetaschemaTest {
  private static final String NS = "https://csrc.nist.gov/ns/test/xml";
  private static final String FOREIGN_NS = "http://example.com/ns/foreign";

  @Test
  void testReadCapturesUnknownElements() throws IOException, XMLStreamException {
    String xml = "<any-assembly xmlns='" + NS + "'>"
        + "  <known-field>hello</known-field>"
        + "  <foreign:extra xmlns:foreign='" + FOREIGN_NS + "'>foreign-value</foreign:extra>"
        + "</any-assembly>";

    IBindingContext bindingContext = newBindingContext();
    IBoundDefinitionModelAssembly assembly
        = ObjectUtils.requireNonNull(
            (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(AnyAssembly.class));

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(xml));

    URI source = ObjectUtils.notNull(URI.create("https://example.com/test"));
    MetaschemaXmlReader reader = new MetaschemaXmlReader(eventReader, source);

    AnyAssembly result = reader.read(assembly);

    // Known field should be parsed normally
    assertEquals("hello", result.getKnownField());

    // Any content should capture the foreign element
    IAnyContent anyContent = result.getAny();
    assertNotNull(anyContent, "Any content should not be null");
    assertInstanceOf(XmlAnyContent.class, anyContent);

    XmlAnyContent xmlAny = (XmlAnyContent) anyContent;
    List<Element> elements = xmlAny.getElements();
    assertEquals(1, elements.size(), "Should capture exactly one foreign element");

    Element foreignElement = elements.get(0);
    assertEquals("extra", foreignElement.getLocalName());
    assertEquals(FOREIGN_NS, foreignElement.getNamespaceURI());
    assertEquals("foreign-value", foreignElement.getTextContent());
  }

  @Test
  void testReadWithNoUnknownElements() throws IOException, XMLStreamException {
    String xml = "<any-assembly xmlns='" + NS + "'>"
        + "  <known-field>hello</known-field>"
        + "</any-assembly>";

    IBindingContext bindingContext = newBindingContext();
    IBoundDefinitionModelAssembly assembly
        = ObjectUtils.requireNonNull(
            (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(AnyAssembly.class));

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(xml));

    URI source = ObjectUtils.notNull(URI.create("https://example.com/test"));
    MetaschemaXmlReader reader = new MetaschemaXmlReader(eventReader, source);

    AnyAssembly result = reader.read(assembly);

    // Known field should be parsed normally
    assertEquals("hello", result.getKnownField());

    // Any content should be null when there are no unknown elements
    IAnyContent anyContent = result.getAny();
    assertEquals(null, anyContent, "Any content should be null when no unknown elements");
  }

  @Test
  void testWriteSerializesAnyContent() throws IOException, XMLStreamException {
    // Build an AnyAssembly with known field and any content
    AnyAssembly obj = new AnyAssembly();
    obj.setKnownField("hello");

    // Create a DOM element to use as foreign content
    javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    javax.xml.parsers.DocumentBuilder docBuilder;
    try {
      docBuilder = dbf.newDocumentBuilder();
    } catch (javax.xml.parsers.ParserConfigurationException ex) {
      throw new IOException(ex);
    }
    org.w3c.dom.Document doc = docBuilder.newDocument();
    Element foreignEl = doc.createElementNS(FOREIGN_NS, "foreign:extra");
    foreignEl.setTextContent("foreign-value");
    obj.setAny(new XmlAnyContent(List.of(foreignEl)));

    IBindingContext bindingContext = newBindingContext();
    IBoundDefinitionModelAssembly assembly
        = ObjectUtils.requireNonNull(
            (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(AnyAssembly.class));

    // Write to XML using writeRoot with namespace-repairing factory
    StringWriter sw = new StringWriter();
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
    XMLStreamWriter2 xmlWriter = (XMLStreamWriter2) outputFactory.createXMLStreamWriter(sw);

    xmlWriter.writeStartDocument("UTF-8", "1.0");
    MetaschemaXmlWriter writer = new MetaschemaXmlWriter(xmlWriter);
    writer.writeRoot(assembly, obj);
    xmlWriter.writeEndDocument();
    xmlWriter.close();

    String xmlOutput = sw.toString();

    // Verify the output contains the foreign element
    assertNotNull(xmlOutput);
    // The output should contain the foreign element
    assertTrue(xmlOutput.contains("extra"),
        "Output should contain foreign element 'extra': " + xmlOutput);
    assertTrue(xmlOutput.contains("foreign-value"),
        "Output should contain foreign element text: " + xmlOutput);
  }

  @Test
  void testRoundTrip() throws IOException, XMLStreamException {
    String xml = "<any-assembly xmlns='" + NS + "'>"
        + "  <known-field>hello</known-field>"
        + "  <foreign:extra xmlns:foreign='" + FOREIGN_NS + "' attr1='val1'>foreign-value</foreign:extra>"
        + "</any-assembly>";

    IBindingContext bindingContext = newBindingContext();
    IBoundDefinitionModelAssembly assembly
        = ObjectUtils.requireNonNull(
            (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(AnyAssembly.class));

    // Read
    XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    XMLEventReader2 eventReader = (XMLEventReader2) inputFactory.createXMLEventReader(new StringReader(xml));
    URI source = ObjectUtils.notNull(URI.create("https://example.com/test"));
    MetaschemaXmlReader reader = new MetaschemaXmlReader(eventReader, source);
    AnyAssembly result = reader.read(assembly);

    // Write back using writeRoot with namespace-repairing factory
    StringWriter sw = new StringWriter();
    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
    outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
    XMLStreamWriter2 xmlWriter = (XMLStreamWriter2) outputFactory.createXMLStreamWriter(sw);
    xmlWriter.writeStartDocument("UTF-8", "1.0");
    MetaschemaXmlWriter writer = new MetaschemaXmlWriter(xmlWriter);
    writer.writeRoot(assembly, result);
    xmlWriter.writeEndDocument();
    xmlWriter.close();

    String xmlOutput = sw.toString();

    // Re-read the output
    XMLEventReader2 eventReader2 = (XMLEventReader2) inputFactory.createXMLEventReader(new StringReader(xmlOutput));
    MetaschemaXmlReader reader2 = new MetaschemaXmlReader(eventReader2, source);
    AnyAssembly result2 = reader2.read(assembly);

    // Verify the round-trip preserved data
    assertEquals("hello", result2.getKnownField());
    assertNotNull(result2.getAny());
    assertInstanceOf(XmlAnyContent.class, result2.getAny());

    XmlAnyContent xmlAny = (XmlAnyContent) result2.getAny();
    assertEquals(1, xmlAny.getElements().size());
    assertEquals("extra", xmlAny.getElements().get(0).getLocalName());
    assertEquals(FOREIGN_NS, xmlAny.getElements().get(0).getNamespaceURI());
    assertEquals("foreign-value", xmlAny.getElements().get(0).getTextContent());
    assertEquals("val1", xmlAny.getElements().get(0).getAttribute("attr1"));
  }

  @Test
  void testReadMultipleUnknownElements() throws IOException, XMLStreamException {
    String xml = "<any-assembly xmlns='" + NS + "'>"
        + "  <known-field>hello</known-field>"
        + "  <foreign:item1 xmlns:foreign='" + FOREIGN_NS + "'>value1</foreign:item1>"
        + "  <foreign:item2 xmlns:foreign='" + FOREIGN_NS + "'>value2</foreign:item2>"
        + "</any-assembly>";

    IBindingContext bindingContext = newBindingContext();
    IBoundDefinitionModelAssembly assembly
        = ObjectUtils.requireNonNull(
            (IBoundDefinitionModelAssembly) bindingContext.getBoundDefinitionForClass(AnyAssembly.class));

    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLEventReader2 eventReader = (XMLEventReader2) factory.createXMLEventReader(new StringReader(xml));
    URI source = ObjectUtils.notNull(URI.create("https://example.com/test"));
    MetaschemaXmlReader reader = new MetaschemaXmlReader(eventReader, source);
    AnyAssembly result = reader.read(assembly);

    assertEquals("hello", result.getKnownField());
    assertNotNull(result.getAny());

    XmlAnyContent xmlAny = (XmlAnyContent) result.getAny();
    assertEquals(2, xmlAny.getElements().size(), "Should capture two foreign elements");
    assertEquals("item1", xmlAny.getElements().get(0).getLocalName());
    assertEquals("item2", xmlAny.getElements().get(1).getLocalName());
  }
}
