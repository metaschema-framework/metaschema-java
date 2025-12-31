/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents datatype content backed by standard DOM elements.
 * <p>
 * This class stores XML Schema datatype definitions as DOM elements and
 * provides the capability to write them to an XML stream. It replaces the
 * JDOM2-based implementation with standard Java DOM APIs.
 */
public class DomDatatypeContent
    extends AbstractDatatypeContent {

  @NonNull
  private final List<Element> content;

  /**
   * Constructs a new DOM-backed datatype content instance.
   *
   * @param typeName
   *          the name of the datatype
   * @param content
   *          the list of DOM elements representing the datatype definition
   * @param dependencies
   *          the list of datatype names that this datatype depends on
   */
  public DomDatatypeContent(
      @NonNull String typeName,
      @NonNull List<Element> content,
      @NonNull List<String> dependencies) {
    super(typeName, dependencies);
    this.content = CollectionUtil.unmodifiableList(new ArrayList<>(content));
  }

  /**
   * Retrieves the DOM elements representing the datatype content.
   *
   * @return an unmodifiable list of DOM elements
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  protected List<Element> getContent() {
    return content;
  }

  @Override
  public void write(@NonNull XMLStreamWriter writer) throws XMLStreamException {
    for (Element element : getContent()) {
      writeElement(element, writer);
    }
  }

  /**
   * Writes a DOM element and its contents to the XMLStreamWriter.
   *
   * @param element
   *          the DOM element to write
   * @param writer
   *          the XMLStreamWriter to write to
   * @throws XMLStreamException
   *           if an error occurs during writing
   */
  private void writeElement(@NonNull Element element, @NonNull XMLStreamWriter writer) throws XMLStreamException {
    String namespaceURI = element.getNamespaceURI();
    String localName = element.getLocalName();
    String prefix = element.getPrefix();

    // Write the start element
    if (namespaceURI != null && !namespaceURI.isEmpty()) {
      if (prefix != null && !prefix.isEmpty()) {
        writer.writeStartElement(prefix, localName, namespaceURI);
      } else {
        writer.writeStartElement(namespaceURI, localName);
      }
    } else {
      writer.writeStartElement(localName != null ? localName : element.getTagName());
    }

    // Write namespace declarations if this element has them
    if (namespaceURI != null && !namespaceURI.isEmpty()) {
      String existingPrefix = writer.getPrefix(namespaceURI);
      if (existingPrefix == null) {
        if (prefix != null && !prefix.isEmpty()) {
          writer.writeNamespace(prefix, namespaceURI);
        } else {
          writer.writeDefaultNamespace(namespaceURI);
        }
      }
    }

    // Write attributes
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      String attrName = attr.getNodeName();
      String attrValue = attr.getNodeValue();

      // Skip xmlns declarations - they're handled separately
      if (attrName.startsWith("xmlns")) {
        continue;
      }

      String attrNamespaceURI = attr.getNamespaceURI();
      String attrLocalName = attr.getLocalName();
      String attrPrefix = attr.getPrefix();

      if (attrNamespaceURI != null && !attrNamespaceURI.isEmpty()) {
        if (attrPrefix != null && !attrPrefix.isEmpty()) {
          writer.writeAttribute(attrPrefix, attrNamespaceURI, attrLocalName, attrValue);
        } else {
          writer.writeAttribute(attrNamespaceURI, attrLocalName, attrValue);
        }
      } else {
        writer.writeAttribute(attrLocalName != null ? attrLocalName : attrName, attrValue);
      }
    }

    // Write child nodes
    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      switch (child.getNodeType()) {
      case Node.ELEMENT_NODE:
        writeElement((Element) child, writer);
        break;
      case Node.TEXT_NODE:
        String text = child.getNodeValue();
        if (text != null && !text.isEmpty()) {
          writer.writeCharacters(text);
        }
        break;
      case Node.CDATA_SECTION_NODE:
        String cdata = child.getNodeValue();
        if (cdata != null) {
          writer.writeCData(cdata);
        }
        break;
      case Node.COMMENT_NODE:
        String comment = child.getNodeValue();
        if (comment != null) {
          writer.writeComment(comment);
        }
        break;
      default:
        // Ignore other node types
        break;
      }
    }

    // Write end element
    writer.writeEndElement();
  }
}
