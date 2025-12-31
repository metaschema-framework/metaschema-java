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

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a base implementation for XML markup datatype providers.
 * <p>
 * This class handles the loading and processing of XML schema resources that
 * define markup datatypes (such as markup-line and markup-multiline).
 */
public abstract class AbstractXmlMarkupDatatypeProvider
    extends AbstractXmlDatatypeProvider {

  @SuppressWarnings("null")
  @Owning
  @Override
  protected InputStream getSchemaResource() {
    return IModule.class.getResourceAsStream(getSchemaResourcePath());
  }

  /**
   * Get the absolute classpath of the schema resource.
   *
   * @return the resource path
   */
  @NonNull
  protected abstract String getSchemaResourcePath();

  @Override
  protected List<Element> queryElements(XmlSchemaLoader loader) {
    return loader.getContent(
        "/xs:schema/*",
        CollectionUtil.singletonMap("xs", XmlSchemaLoader.NS_XML_SCHEMA));
  }

  /**
   * Get the name of the data type provided by this provider.
   *
   * @return the data type name
   */
  @NonNull
  protected abstract String getDataTypeName();

  @Override
  protected Map<String, IDatatypeContent> handleResults(@NonNull List<Element> items) {
    String dataTypeName = getDataTypeName();
    return CollectionUtil.singletonMap(
        dataTypeName,
        new DomDatatypeContent(
            dataTypeName,
            ObjectUtils.notNull(items.stream()
                .filter(element -> !"include".equals(element.getLocalName()))
                .collect(Collectors.toList())),
            CollectionUtil.emptyList()));
  }

}
