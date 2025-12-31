/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.schemagen.xml.impl.IXmlGenerationState;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlDatatypeManager;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XML Schema complex type implementation for Metaschema field definitions
 * that have flags.
 * <p>
 * Fields with flags require a complex type in XML Schema because they need both
 * content (the field value) and attributes (the flags). This class generates
 * either simpleContent or complexContent extensions depending on whether the
 * field's data type produces mixed XML content.
 */
public class XmlComplexTypeFieldDefinition
    extends AbstractXmlComplexType<IFieldDefinition> {
  /**
   * Construct a new complex type for a field definition.
   *
   * @param qname
   *          the qualified name for the XML Schema type
   * @param definition
   *          the Metaschema field definition to generate the type for
   */
  public XmlComplexTypeFieldDefinition(
      @NonNull QName qname,
      @NonNull IFieldDefinition definition) {
    super(qname, definition);
  }

  @Override
  protected void generateTypeBody(IXmlGenerationState state) throws XMLStreamException {
    IFieldDefinition definition = getDefinition();
    IXmlSimpleType valueType = state.getSimpleType(definition);
    IDataTypeAdapter<?> datatype = valueType.getDataTypeAdapter();

    String xmlContentType;
    if (datatype.isXmlMixed()) {
      xmlContentType = "complexContent"; // with attributes
    } else {
      xmlContentType = "simpleContent"; // without attributes
    }
    state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, xmlContentType, XmlDatatypeManager.NS_XML_SCHEMA);
    state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "extension", XmlDatatypeManager.NS_XML_SCHEMA);
    state.writeAttribute("base", valueType.getTypeReference());

    for (IFlagInstance flagInstance : definition.getFlagInstances()) {
      assert flagInstance != null;
      generateFlagInstance(flagInstance, state);
    }
    state.writeEndElement(); // xs:extension

    state.writeEndElement(); // xs:simpleContent or xs:complexContent
  }
}
