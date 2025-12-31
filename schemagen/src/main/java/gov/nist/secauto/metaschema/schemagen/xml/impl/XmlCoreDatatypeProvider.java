/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.eclipse.jdt.annotation.Owning;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides core XML Schema datatypes from the metaschema-datatypes.xsd
 * resource.
 */
public class XmlCoreDatatypeProvider
    extends AbstractXmlDatatypeProvider {

  @SuppressWarnings("resource")
  @Override
  @Owning
  protected InputStream getSchemaResource() {
    return ObjectUtils.requireNonNull(IModule.class.getResourceAsStream("/schema/xml/metaschema-datatypes.xsd"));
  }

  @Override
  protected List<Element> queryElements(XmlSchemaLoader loader) {
    return loader.getContent(
        "/xs:schema/xs:simpleType",
        CollectionUtil.singletonMap("xs", XmlSchemaLoader.NS_XML_SCHEMA));
  }

  @NonNull
  private static List<String> analyzeDependencies(@NonNull Element element) {
    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      NodeList nodes = (NodeList) xpath.evaluate(".//@base", element, XPathConstants.NODESET);

      List<String> dependencies = new ArrayList<>();
      for (int i = 0; i < nodes.getLength(); i++) {
        String value = nodes.item(i).getNodeValue();
        if (value != null && !value.startsWith("xs:")) {
          if (!dependencies.contains(value)) {
            dependencies.add(value);
          }
        }
      }
      return dependencies;
    } catch (XPathExpressionException ex) {
      throw new IllegalStateException("Failed to analyze dependencies", ex);
    }
  }

  @Override
  @NonNull
  protected Map<String, IDatatypeContent> handleResults(
      @NonNull List<Element> items) {
    return ObjectUtils.notNull(items.stream()
        .map(element -> new DomDatatypeContent(
            ObjectUtils.requireNonNull(element.getAttribute("name")),
            CollectionUtil.singletonList(element),
            analyzeDependencies(element)))
        .collect(Collectors.toMap((Function<? super IDatatypeContent, ? extends String>) IDatatypeContent::getTypeName,
            Function.identity(), (e1, e2) -> e2,
            LinkedHashMap::new)));
  }
}
