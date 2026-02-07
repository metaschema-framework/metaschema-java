/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io.xml;

import org.codehaus.stax2.XMLEventReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Utility methods for converting between StAX events and W3C DOM elements.
 *
 * <p>
 * These methods support the {@code any} content feature by converting unmodeled
 * XML content between the StAX event stream used during parsing and the DOM
 * representation stored in {@link XmlAnyContent}.
 */
public final class XmlDomUtil {

  private XmlDomUtil() {
    // disable construction
  }

  /**
   * Read an XML element from a StAX event reader and return it as a DOM
   * {@link Element}.
   *
   * <p>
   * The reader must be positioned so that the next event is a
   * {@link XMLStreamConstants#START_ELEMENT}. After this method returns, the
   * reader will be positioned just past the matching
   * {@link XMLStreamConstants#END_ELEMENT}.
   *
   * @param reader
   *          the StAX event reader, positioned before a start element
   * @return the DOM element containing the full subtree
   * @throws XMLStreamException
   *           if an error occurs while reading XML events
   */
  @NonNull
  public static Element staxToElement(@NonNull XMLEventReader2 reader)
      throws XMLStreamException {
    Document doc = newDocument();
    XMLEvent event = reader.nextEvent();
    if (!event.isStartElement()) {
      throw new XMLStreamException("Expected START_ELEMENT but found " + event.getEventType());
    }
    StartElement startElement = event.asStartElement();
    Element root = createDomElement(doc, startElement);
    doc.appendChild(root);

    readChildren(reader, doc, root);
    return root;
  }

  /**
   * Write a DOM {@link Element} to a StAX stream writer.
   *
   * <p>
   * This writes the complete element subtree including attributes, namespace
   * declarations, child elements, and text content.
   *
   * @param element
   *          the DOM element to write
   * @param writer
   *          the StAX stream writer to write to
   * @throws XMLStreamException
   *           if an error occurs while writing to the stream
   */
  public static void elementToStax(
      @NonNull Element element,
      @NonNull XMLStreamWriter2 writer)
      throws XMLStreamException {
    String namespaceUri = element.getNamespaceURI();
    String localName = element.getLocalName();
    String prefix = element.getPrefix();

    if (namespaceUri != null && !namespaceUri.isEmpty()) {
      if (prefix != null && !prefix.isEmpty()) {
        writer.writeStartElement(prefix, localName, namespaceUri);
        // Declare the namespace if the writer doesn't know about it
        String existingPrefix = writer.getNamespaceContext().getPrefix(namespaceUri);
        if (existingPrefix == null || !existingPrefix.equals(prefix)) {
          writer.writeNamespace(prefix, namespaceUri);
        }
      } else {
        writer.writeStartElement(namespaceUri, localName);
      }
    } else {
      writer.writeStartElement(localName);
    }

    // Write attributes
    writeAttributes(element, writer);

    // Write child nodes
    writeChildren(element, writer);

    writer.writeEndElement();
  }

  @NonNull
  private static Document newDocument() throws XMLStreamException {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      // Harden against XXE: deny external DTD and schema access
      dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
      DocumentBuilder builder = dbf.newDocumentBuilder();
      return builder.newDocument();
    } catch (ParserConfigurationException ex) {
      throw new XMLStreamException("Failed to create DOM DocumentBuilder", ex);
    }
  }

  @NonNull
  private static Element createDomElement(
      @NonNull Document doc,
      @NonNull StartElement startElement) {
    QName name = startElement.getName();
    String namespaceUri = name.getNamespaceURI();
    String localName = name.getLocalPart();
    String prefix = name.getPrefix();

    Element element;
    if (namespaceUri != null && !namespaceUri.isEmpty()) {
      String qualifiedName = (prefix != null && !prefix.isEmpty())
          ? prefix + ":" + localName
          : localName;
      element = doc.createElementNS(namespaceUri, qualifiedName);
    } else {
      element = doc.createElement(localName);
    }

    // Copy attributes
    @SuppressWarnings("unchecked")
    Iterator<Attribute> attrs = startElement.getAttributes();
    while (attrs.hasNext()) {
      Attribute attr = attrs.next();
      QName attrName = attr.getName();
      String attrNs = attrName.getNamespaceURI();
      String attrLocal = attrName.getLocalPart();
      String attrPrefix = attrName.getPrefix();

      if (attrNs != null && !attrNs.isEmpty()) {
        String attrQualified = (attrPrefix != null && !attrPrefix.isEmpty())
            ? attrPrefix + ":" + attrLocal
            : attrLocal;
        element.setAttributeNS(attrNs, attrQualified, attr.getValue());
      } else {
        element.setAttribute(attrLocal, attr.getValue());
      }
    }

    // Copy namespace declarations as xmlns attributes
    @SuppressWarnings("unchecked")
    Iterator<Namespace> namespaces = startElement.getNamespaces();
    while (namespaces.hasNext()) {
      Namespace ns = namespaces.next();
      String nsPrefix = ns.getPrefix();
      if (nsPrefix != null && !nsPrefix.isEmpty()) {
        element.setAttributeNS(
            "http://www.w3.org/2000/xmlns/",
            "xmlns:" + nsPrefix,
            ns.getNamespaceURI());
      }
      // default namespace is handled by createElementNS
    }

    return element;
  }

  private static void readChildren(
      @NonNull XMLEventReader2 reader,
      @NonNull Document doc,
      @NonNull Element parent) throws XMLStreamException {
    while (reader.hasNext()) {
      XMLEvent event = reader.peek();
      if (event.isEndElement()) {
        // Consume the end element and return
        reader.nextEvent();
        return;
      } else if (event.isStartElement()) {
        StartElement childStart = reader.nextEvent().asStartElement();
        Element child = createDomElement(doc, childStart);
        parent.appendChild(child);
        readChildren(reader, doc, child);
      } else if (event.isCharacters()) {
        Characters chars = reader.nextEvent().asCharacters();
        parent.appendChild(doc.createTextNode(chars.getData()));
      } else {
        // Skip other event types (comments, processing instructions, etc.)
        reader.nextEvent();
      }
    }
  }

  private static void writeAttributes(
      @NonNull Element element,
      @NonNull XMLStreamWriter2 writer) throws XMLStreamException {
    NamedNodeMap attrs = element.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Node attr = attrs.item(i);
      String attrNs = attr.getNamespaceURI();
      // getLocalName() may return null for attributes created without
      // namespace awareness; fall back to getNodeName()
      String attrName = attr.getLocalName();
      if (attrName == null) {
        attrName = attr.getNodeName();
      }
      String attrValue = attr.getNodeValue();

      // Skip xmlns declarations - they are handled by
      // writeStartElement/writeNamespace
      if ("http://www.w3.org/2000/xmlns/".equals(attrNs)) {
        continue;
      }

      if (attrNs != null && !attrNs.isEmpty()) {
        String attrPrefix = attr.getPrefix();
        if (attrPrefix != null && !attrPrefix.isEmpty()) {
          writer.writeAttribute(attrPrefix, attrNs, attrName, attrValue);
        } else {
          writer.writeAttribute(attrNs, attrName, attrValue);
        }
      } else {
        writer.writeAttribute(attrName, attrValue);
      }
    }
  }

  private static void writeChildren(
      @NonNull Element element,
      @NonNull XMLStreamWriter2 writer) throws XMLStreamException {
    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      switch (child.getNodeType()) {
      case Node.ELEMENT_NODE:
        elementToStax((Element) child, writer);
        break;
      case Node.TEXT_NODE:
        writer.writeCharacters(child.getTextContent());
        break;
      case Node.CDATA_SECTION_NODE:
        writer.writeCData(child.getTextContent());
        break;
      default:
        // Skip other node types
        break;
      }
    }
  }
}
