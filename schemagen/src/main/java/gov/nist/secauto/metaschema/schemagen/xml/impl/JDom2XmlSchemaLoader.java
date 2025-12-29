/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Loads and queries XML Schema documents using JDOM2.
 * <p>
 * This class provides functionality to load XML Schema documents from various
 * sources and query their content using XPath expressions.
 */
public class JDom2XmlSchemaLoader {
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
   * @throws JDOMException
   *           if an error occurs parsing the XML
   * @throws IOException
   *           if an I/O error occurs reading the file
   */
  @SuppressWarnings("null")
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public JDom2XmlSchemaLoader(@NonNull Path path) throws JDOMException, IOException {
    this(new SAXBuilder().build(path.toFile()));
  }

  /**
   * Constructs a new XML Schema loader from an input stream.
   *
   * @param is
   *          the input stream containing the XML Schema content
   * @throws JDOMException
   *           if an error occurs parsing the XML
   * @throws IOException
   *           if an I/O error occurs reading the stream
   */
  @SuppressWarnings("null")
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public JDom2XmlSchemaLoader(@NonNull InputStream is) throws JDOMException, IOException {
    this(new SAXBuilder().build(is));
  }

  /**
   * Constructs a new XML Schema loader from a JDOM2 document.
   *
   * @param document
   *          the JDOM2 document containing the XML Schema
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public JDom2XmlSchemaLoader(@NonNull Document document) {
    this.document = document;
  }

  /**
   * Retrieves the underlying JDOM2 document.
   *
   * @return the JDOM2 document
   */
  protected Document getNode() {
    return document;
  }

  /**
   * Executes an XPath query and returns matching elements.
   *
   * @param path
   *          the XPath expression to evaluate
   * @param prefixToNamespaceMap
   *          a map of namespace prefixes to URIs for use in the XPath query
   * @return a list of matching JDOM2 elements
   */
  @SuppressWarnings("null")
  @NonNull
  public List<Element> getContent(
      @NonNull String path,
      @NonNull Map<String, String> prefixToNamespaceMap) {

    Collection<Namespace> namespaces = prefixToNamespaceMap.entrySet().stream()
        .map(entry -> Namespace.getNamespace(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
    XPathExpression<Element> xpath = XPathFactory.instance().compile(path, Filters.element(), null, namespaces);
    return xpath.evaluate(getNode());
  }
}
