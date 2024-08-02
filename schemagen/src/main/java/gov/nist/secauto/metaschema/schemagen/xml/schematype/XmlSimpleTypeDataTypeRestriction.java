/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.schematype;

import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.AbstractGenerationState.AllowedValueCollection;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class XmlSimpleTypeDataTypeRestriction
    extends AbstractXmlSimpleType {
  @NonNull
  private final AllowedValueCollection allowedValuesCollection;

  public XmlSimpleTypeDataTypeRestriction(
      @NonNull QName qname,
      @NonNull IValuedDefinition definition,
      @NonNull AllowedValueCollection allowedValuesCollection) {
    super(qname, definition);
    this.allowedValuesCollection = allowedValuesCollection;
  }

  protected AllowedValueCollection getAllowedValuesCollection() {
    return allowedValuesCollection;
  }

  @Override
  public boolean isInline(XmlGenerationState state) {
    return true;
  }

  @Override
  public boolean isGeneratedType(XmlGenerationState state) {
    return true;
  }

  @Override
  public void generate(XmlGenerationState state) {
    try {
      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "simpleType", XmlSchemaGenerator.NS_XML_SCHEMA);

      if (!isInline(state)) {
        state.writeAttribute("name", ObjectUtils.notNull(getQName().getLocalPart()));
      }

      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "restriction", XmlSchemaGenerator.NS_XML_SCHEMA);
      state.writeAttribute("base", state.getSimpleType(getDataTypeAdapter()).getTypeReference());

      for (IAllowedValue allowedValue : getAllowedValuesCollection().getValues()) {
        state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "enumeration", XmlSchemaGenerator.NS_XML_SCHEMA);
        state.writeAttribute("value", allowedValue.getValue());

        MarkupLine description = allowedValue.getDescription();
        if (!description.isEmpty()) {
          generateDescriptionAnnotation(
              description,
              ObjectUtils.notNull(getQName().getNamespaceURI()),
              state);
          // LOGGER.info(String.format("Field:%s:%s: %s",
          // definition.getContainingMetaschema().getLocation(),
          // definition.getName(), allowedValue.getValue()));
        }
        state.writeEndElement(); // xs:enumeration
      }

      state.writeEndElement(); // xs:restriction
      state.writeEndElement(); // xs:simpleType
    } catch (XMLStreamException ex) {
      throw new SchemaGenerationException(ex);
    }
  }

  public static void generateDescriptionAnnotation(
      @NonNull MarkupLine description,
      @NonNull String xmlNS,
      @NonNull XmlGenerationState state) throws XMLStreamException {
    XMLStreamWriter2 writer = state.getXMLStreamWriter();
    writer.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "annotation", XmlSchemaGenerator.NS_XML_SCHEMA);
    writer.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "documentation", XmlSchemaGenerator.NS_XML_SCHEMA);

    // write description
    writer.writeStartElement(xmlNS, "p");

    description.writeXHtml(xmlNS, writer);

    writer.writeEndElement(); // p

    writer.writeEndElement(); // xs:documentation
    writer.writeEndElement(); // xs:annotation
  }
}
