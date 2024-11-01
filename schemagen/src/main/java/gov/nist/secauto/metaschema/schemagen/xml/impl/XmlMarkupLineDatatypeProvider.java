/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

public class XmlMarkupLineDatatypeProvider
    extends AbstractXmlMarkupDatatypeProvider {
  private static final String DATATYPE_NAME = "MarkupLineDatatype";
  private static final String SCHEMA_RESOURCE_PATH = "/schema/xml/metaschema-markup-line.xsd";

  @Override
  protected String getSchemaResourcePath() {
    return SCHEMA_RESOURCE_PATH;
  }

  @Override
  protected String getDataTypeName() {
    return DATATYPE_NAME;
  }
}
