/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl.schematype;

import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import dev.metaschema.core.datatype.markup.MarkupLine;
import dev.metaschema.core.model.IValuedDefinition;
import dev.metaschema.core.model.constraint.IAllowedValue;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.schemagen.AbstractGenerationState.AllowedValueCollection;
import dev.metaschema.schemagen.SchemaGenerationException;
import dev.metaschema.schemagen.xml.impl.IXmlGenerationState;
import dev.metaschema.schemagen.xml.impl.XmlDatatypeManager;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XML Schema simple type that restricts a base data type with enumerated
 * allowed values.
 * <p>
 * This class generates an xs:simpleType with xs:restriction containing
 * xs:enumeration elements for each allowed value defined in the Metaschema
 * constraints.
 */
public class XmlSimpleTypeDataTypeRestriction
    extends AbstractXmlSimpleType {
  @NonNull
  private final AllowedValueCollection allowedValuesCollection;

  /**
   * Construct a new data type restriction.
   *
   * @param qname
   *          the qualified name for the XML Schema type
   * @param definition
   *          the Metaschema definition that this restriction applies to
   * @param allowedValuesCollection
   *          the collection of allowed values to use as enumeration constraints
   */
  public XmlSimpleTypeDataTypeRestriction(
      @NonNull QName qname,
      @NonNull IValuedDefinition definition,
      @NonNull AllowedValueCollection allowedValuesCollection) {
    super(qname, definition);
    this.allowedValuesCollection = allowedValuesCollection;
  }

  /**
   * Get the collection of allowed values for this restriction.
   *
   * @return the allowed values collection
   */
  @NonNull
  protected AllowedValueCollection getAllowedValuesCollection() {
    return allowedValuesCollection;
  }

  @Override
  public boolean isInline(IXmlGenerationState state) {
    return true;
  }

  @Override
  public boolean isGeneratedType(IXmlGenerationState state) {
    return true;
  }

  @Override
  public void generate(IXmlGenerationState state) {
    try {
      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "simpleType", XmlDatatypeManager.NS_XML_SCHEMA);

      if (!isInline(state)) {
        state.writeAttribute("name", ObjectUtils.notNull(getQName().getLocalPart()));
      }

      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "restriction", XmlDatatypeManager.NS_XML_SCHEMA);
      state.writeAttribute("base", state.getSimpleType(getDataTypeAdapter()).getTypeReference());

      for (IAllowedValue allowedValue : getAllowedValuesCollection().getValues()) {
        state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "enumeration", XmlDatatypeManager.NS_XML_SCHEMA);
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

  /**
   * Generate an XML Schema annotation containing documentation for an allowed
   * value.
   *
   * @param description
   *          the markup description to include in the documentation
   * @param xmlNS
   *          the XML namespace for documentation elements
   * @param state
   *          the schema generation state for writing output
   * @throws XMLStreamException
   *           if an error occurs while writing XML
   */
  public static void generateDescriptionAnnotation(
      @NonNull MarkupLine description,
      @NonNull String xmlNS,
      @NonNull IXmlGenerationState state) throws XMLStreamException {
    XMLStreamWriter2 writer = state.getXMLStreamWriter();
    writer.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "annotation", XmlDatatypeManager.NS_XML_SCHEMA);
    writer.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "documentation", XmlDatatypeManager.NS_XML_SCHEMA);

    // write description
    writer.writeStartElement(xmlNS, "p");

    description.writeXHtml(xmlNS, writer);

    writer.writeEndElement(); // p

    writer.writeEndElement(); // xs:documentation
    writer.writeEndElement(); // xs:annotation
  }
}
