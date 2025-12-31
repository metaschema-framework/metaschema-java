/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.eclipse.jdt.annotation.Owning;
import org.jdom2.Element;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

public class XmlProseBaseDatatypeProvider
    extends AbstractXmlDatatypeProvider {
  private static final String DATATYPE_NAME = "ProseBase";

  @SuppressWarnings("resource")
  @Override
  @Owning
  protected InputStream getSchemaResource() {
    return ObjectUtils.requireNonNull(IModule.class.getResourceAsStream("/schema/xml/metaschema-prose-base.xsd"));
  }

  @Override
  protected List<Element> queryElements(JDom2XmlSchemaLoader loader) {
    return loader.getContent(
        "/xs:schema/*",
        CollectionUtil.singletonMap("xs", JDom2XmlSchemaLoader.NS_XML_SCHEMA));
  }

  @Override
  @NonNull
  protected Map<String, IDatatypeContent> handleResults(@NonNull List<Element> items) {
    return CollectionUtil.singletonMap(
        DATATYPE_NAME,
        new JDom2DatatypeContent(
            DATATYPE_NAME,
            items,
            CollectionUtil.emptyList()));
  }
}
