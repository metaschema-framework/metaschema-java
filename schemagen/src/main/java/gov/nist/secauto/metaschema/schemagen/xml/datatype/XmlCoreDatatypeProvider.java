/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.datatype;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.datatype.IDatatypeContent;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

public class XmlCoreDatatypeProvider
    extends AbstractXmlDatatypeProvider {

  @Override
  protected InputStream getSchemaResource() {
    return IModule.class.getResourceAsStream("/schema/xml/metaschema-datatypes.xsd");
  }

  @Override
  protected List<Element> queryElements(JDom2XmlSchemaLoader loader) {
    return loader.getContent(
        "/xs:schema/xs:simpleType",
        CollectionUtil.singletonMap("xs", JDom2XmlSchemaLoader.NS_XML_SCHEMA));
  }

  @NonNull
  private static List<String> analyzeDependencies(@NonNull Element element) {
    XPathExpression<Attribute> xpath = XPathFactory.instance().compile(".//@base", Filters.attribute());
    return ObjectUtils.notNull(xpath.evaluate(element).stream()
        .map(Attribute::getValue)
        .filter(type -> !type.startsWith("xs:"))
        .distinct()
        .collect(Collectors.toList()));
  }

  @Override
  @NonNull
  protected Map<String, IDatatypeContent> handleResults(
      @NonNull List<Element> items) {
    return ObjectUtils.notNull(items.stream()
        .map(element -> new JDom2DatatypeContent(
            ObjectUtils.requireNonNull(element.getAttributeValue("name")),
            CollectionUtil.singletonList(element),
            analyzeDependencies(element)))
        .collect(Collectors.toMap((Function<? super IDatatypeContent, ? extends String>) IDatatypeContent::getTypeName,
            Function.identity(), (e1, e2) -> e2,
            LinkedHashMap::new)));
  }
}
