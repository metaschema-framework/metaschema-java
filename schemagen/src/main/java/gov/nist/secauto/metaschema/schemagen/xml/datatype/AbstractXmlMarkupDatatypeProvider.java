/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.datatype;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.datatype.IDatatypeContent;

import org.eclipse.jdt.annotation.Owning;
import org.jdom2.Element;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

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
  protected List<Element> queryElements(JDom2XmlSchemaLoader loader) {
    return loader.getContent(
        "/xs:schema/*",
        CollectionUtil.singletonMap("xs", JDom2XmlSchemaLoader.NS_XML_SCHEMA));
  }

  @NonNull
  protected abstract String getDataTypeName();

  @Override
  protected Map<String, IDatatypeContent> handleResults(@NonNull List<Element> items) {
    String dataTypeName = getDataTypeName();
    return CollectionUtil.singletonMap(
        dataTypeName,
        new JDom2DatatypeContent(
            dataTypeName,
            ObjectUtils.notNull(items.stream()
                .filter(element -> !"include".equals(element.getName()))
                .collect(Collectors.toList())),
            CollectionUtil.emptyList()));
  }

}
