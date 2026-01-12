/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl;

import org.eclipse.jdt.annotation.Owning;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides prose base datatype from the metaschema-prose-base.xsd resource.
 */
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
  protected List<Element> queryElements(XmlSchemaLoader loader) {
    return loader.getContent(
        "/xs:schema/*",
        CollectionUtil.singletonMap("xs", XmlSchemaLoader.NS_XML_SCHEMA));
  }

  @Override
  @NonNull
  protected Map<String, IDatatypeContent> handleResults(@NonNull List<Element> items) {
    return CollectionUtil.singletonMap(
        DATATYPE_NAME,
        new DomDatatypeContent(
            DATATYPE_NAME,
            items,
            CollectionUtil.emptyList()));
  }
}
