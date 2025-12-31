/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Loads and queries XML Schema documents using standard Java DOM and XPath.
 * <p>
 * This class provides functionality to load XML Schema documents from various
 * sources and query their content using XPath expressions. It replaces the
 * JDOM2-based implementation with standard Java XML APIs.
 */
public class XmlSchemaLoader {
  /** The XML Schema namespace URI. */
  @NonNull
  public static final String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  @NonNull
  private final Document document;

  /**
   * Constructs a new XML Schema loader from a file path.
   *
   * @param path
   *          the path to the XML Schema file
   * @throws SAXException
   *           if an error occurs parsing the XML
   * @throws IOException
   *           if an I/O error occurs reading the file
   */
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public XmlSchemaLoader(@NonNull Path path) throws SAXException, IOException {
    this(parseDocument(path));
  }

  /**
   * Constructs a new XML Schema loader from an input stream.
   *
   * @param is
   *          the input stream containing the XML Schema content
   * @throws SAXException
   *           if an error occurs parsing the XML
   * @throws IOException
   *           if an I/O error occurs reading the stream
   */
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public XmlSchemaLoader(@NonNull InputStream is) throws SAXException, IOException {
    this(parseDocument(is));
  }

  /**
   * Constructs a new XML Schema loader from a DOM document.
   *
   * @param document
   *          the DOM document containing the XML Schema
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public XmlSchemaLoader(@NonNull Document document) {
    this.document = document;
  }

  @NonNull
  private static Document parseDocument(@NonNull Path path) throws SAXException, IOException {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(path.toFile());
      // Normalize to ensure deferred DOM nodes are fully loaded for XPath evaluation
      doc.normalizeDocument();
      return doc;
    } catch (ParserConfigurationException ex) {
      throw new IllegalStateException("Failed to create document builder", ex);
    }
  }

  @NonNull
  private static Document parseDocument(@NonNull InputStream is) throws SAXException, IOException {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(is);
      // Normalize to ensure deferred DOM nodes are fully loaded for XPath evaluation
      doc.normalizeDocument();
      return doc;
    } catch (ParserConfigurationException ex) {
      throw new IllegalStateException("Failed to create document builder", ex);
    }
  }

  /**
   * Retrieves the underlying DOM document.
   *
   * @return the DOM document
   */
  @SuppressFBWarnings("EI_EXPOSE_REP")
  protected Document getDocument() {
    return document;
  }

  /**
   * Executes an XPath query and returns matching elements.
   *
   * @param path
   *          the XPath expression to evaluate
   * @param prefixToNamespaceMap
   *          a map of namespace prefixes to URIs for use in the XPath query
   * @return a list of matching DOM elements
   */
  @SuppressWarnings("null")
  @NonNull
  public List<Element> getContent(
      @NonNull String path,
      @NonNull Map<String, String> prefixToNamespaceMap) {

    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      xpath.setNamespaceContext(new MapNamespaceContext(prefixToNamespaceMap));

      NodeList nodeList = (NodeList) xpath.evaluate(path, document, XPathConstants.NODESET);

      List<Element> result = new ArrayList<>(nodeList.getLength());
      for (int i = 0; i < nodeList.getLength(); i++) {
        if (nodeList.item(i) instanceof Element) {
          result.add((Element) nodeList.item(i));
        }
      }
      return result;
    } catch (XPathExpressionException ex) {
      throw new IllegalArgumentException("Invalid XPath expression: " + path, ex);
    }
  }

  /**
   * A simple NamespaceContext implementation backed by a Map.
   */
  private static final class MapNamespaceContext implements NamespaceContext {
    private final Map<String, String> prefixToNamespace;

    MapNamespaceContext(Map<String, String> prefixToNamespace) {
      this.prefixToNamespace = prefixToNamespace;
    }

    @Override
    public String getNamespaceURI(String prefix) {
      if (prefix == null) {
        throw new IllegalArgumentException("prefix cannot be null");
      }
      String uri = prefixToNamespace.get(prefix);
      return uri != null ? uri : XMLConstants.NULL_NS_URI;
    }

    @Override
    public String getPrefix(String namespaceURI) {
      if (namespaceURI == null) {
        throw new IllegalArgumentException("namespaceURI cannot be null");
      }
      for (Map.Entry<String, String> entry : prefixToNamespace.entrySet()) {
        if (namespaceURI.equals(entry.getValue())) {
          return entry.getKey();
        }
      }
      return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
      List<String> prefixes = new ArrayList<>();
      for (Map.Entry<String, String> entry : prefixToNamespace.entrySet()) {
        if (namespaceURI.equals(entry.getValue())) {
          prefixes.add(entry.getKey());
        }
      }
      return prefixes.iterator();
    }
  }
}
