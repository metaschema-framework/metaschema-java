/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
import java.util.stream.Collectors;

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
   * <p>
   * The provided elements are imported into a fresh Document to ensure thread
   * safety when schema generation runs in parallel. This prevents issues with
   * Xerces' internal NodeList cache which is not thread-safe - each instance gets
   * a completely independent DOM tree with its own caches.
   *
   * @param typeName
   *          the name of the datatype
   * @param content
   *          the list of DOM elements representing the datatype definition
   * @param dependencies
   *          the list of datatype names that this datatype depends on
   */
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW",
      justification = "Fail-fast on ParserConfigurationException is intentional; "
          + "partial initialization cannot occur since exception is thrown before field assignment")
  public DomDatatypeContent(
      @NonNull String typeName,
      @NonNull List<Element> content,
      @NonNull List<String> dependencies) {
    super(typeName, dependencies);
    // Import elements into a fresh Document for complete thread isolation
    this.content = CollectionUtil.unmodifiableList(importElements(content));
  }

  /**
   * Imports elements into a fresh Document to ensure complete DOM isolation.
   * <p>
   * Xerces' DOM implementation uses internal caches (fNodeListCache) that are not
   * thread-safe. By importing elements into a new Document, we ensure each
   * DomDatatypeContent instance has its own DOM tree with independent caches.
   *
   * @param elements
   *          the elements to import
   * @return a list of elements owned by a new Document
   * @throws IllegalStateException
   *           if the DocumentBuilder cannot be created for DOM isolation
   */
  @NonNull
  private static List<Element> importElements(@NonNull List<Element> elements) {
    if (elements.isEmpty()) {
      return CollectionUtil.emptyList();
    }

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.newDocument();

      return elements.stream()
          .map(e -> (Element) doc.importNode(e, true))
          .collect(Collectors.toList());
    } catch (ParserConfigurationException ex) {
      throw new IllegalStateException("Failed to create DocumentBuilder for DOM isolation", ex);
    }
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

    // Write child nodes - use snapshot to avoid thread-safety issues with live
    // NodeList
    for (Node child : snapshotNodeList(element.getChildNodes())) {
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

  /**
   * Creates a thread-safe snapshot of a DOM NodeList.
   * <p>
   * DOM NodeList objects are "live" - they dynamically reflect changes to the
   * underlying DOM tree. This causes thread-safety issues when tests run in
   * parallel, as Xerces' internal node list cache is not synchronized. This
   * method creates a static snapshot that can be safely iterated without
   * concurrent modification issues.
   *
   * @param nodeList
   *          the live NodeList to snapshot
   * @return an immutable List containing the nodes at the time of the call
   */
  @NonNull
  private static List<Node> snapshotNodeList(@NonNull NodeList nodeList) {
    int length = nodeList.getLength();
    if (length == 0) {
      return CollectionUtil.emptyList();
    }
    return new NodeListSnapshot(nodeList, length);
  }

  /**
   * An immutable, random-access list that captures a snapshot of a DOM NodeList.
   * <p>
   * This class captures node references at construction time, providing a
   * thread-safe view of the NodeList contents that won't be affected by
   * concurrent DOM modifications.
   */
  private static final class NodeListSnapshot
      extends AbstractList<Node>
      implements RandomAccess {

    private final Node[] nodes;

    NodeListSnapshot(@NonNull NodeList nodeList, int length) {
      this.nodes = new Node[length];
      for (int i = 0; i < length; i++) {
        this.nodes[i] = nodeList.item(i);
      }
    }

    @Override
    public Node get(int index) {
      return nodes[index];
    }

    @Override
    public int size() {
      return nodes.length;
    }
  }
}
