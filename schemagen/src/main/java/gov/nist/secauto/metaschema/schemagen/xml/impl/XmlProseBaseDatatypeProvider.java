/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.jdom2.Element;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public class XmlProseBaseDatatypeProvider
    extends AbstractXmlDatatypeProvider {
  private static final String DATATYPE_NAME = "ProseBase";

  @Override
  protected InputStream getSchemaResource() {
    return IModule.class
        .getResourceAsStream("/schema/xml/metaschema-prose-base.xsd");
  }

  @Override
  protected List<Element> queryElements(JDom2XmlSchemaLoader loader) {
    return loader.getContent(
        "/xs:schema/*",
        CollectionUtil.singletonMap("xs", JDom2XmlSchemaLoader.NS_XML_SCHEMA));
  }

  @Override
  protected @NonNull Map<String, IDatatypeContent> handleResults(@NonNull List<Element> items) {
    return CollectionUtil.singletonMap(
        DATATYPE_NAME,
        new JDom2DatatypeContent(
            DATATYPE_NAME,
            items,
            CollectionUtil.emptyList()));
  }
}
