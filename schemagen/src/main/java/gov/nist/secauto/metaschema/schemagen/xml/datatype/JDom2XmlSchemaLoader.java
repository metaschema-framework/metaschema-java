/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.datatype;

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

public class JDom2XmlSchemaLoader {
  @NonNull
  public static final String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

  @NonNull
  private final Document document;

  @SuppressWarnings("null")
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public JDom2XmlSchemaLoader(@NonNull Path path) throws JDOMException, IOException {
    this(new SAXBuilder().build(path.toFile()));
  }

  @SuppressWarnings("null")
  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public JDom2XmlSchemaLoader(@NonNull InputStream is) throws JDOMException, IOException {
    this(new SAXBuilder().build(is));
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public JDom2XmlSchemaLoader(@NonNull Document document) {
    this.document = document;
  }

  protected Document getNode() {
    return document;
  }

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
