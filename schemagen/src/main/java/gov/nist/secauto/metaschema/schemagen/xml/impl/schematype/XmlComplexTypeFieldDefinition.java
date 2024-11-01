/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class XmlComplexTypeFieldDefinition
    extends AbstractXmlComplexType<IFieldDefinition> {
  public XmlComplexTypeFieldDefinition(
      @NonNull QName qname,
      @NonNull IFieldDefinition definition) {
    super(qname, definition);
  }

  @Override
  protected void generateTypeBody(XmlGenerationState state) throws XMLStreamException {
    IFieldDefinition definition = getDefinition();
    IXmlSimpleType valueType = state.getSimpleType(definition);
    IDataTypeAdapter<?> datatype = valueType.getDataTypeAdapter();

    String xmlContentType;
    if (datatype.isXmlMixed()) {
      xmlContentType = "complexContent"; // with attributes
    } else {
      xmlContentType = "simpleContent"; // without attributes
    }
    state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, xmlContentType, XmlSchemaGenerator.NS_XML_SCHEMA);
    state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "extension", XmlSchemaGenerator.NS_XML_SCHEMA);
    state.writeAttribute("base", valueType.getTypeReference());

    for (IFlagInstance flagInstance : definition.getFlagInstances()) {
      assert flagInstance != null;
      generateFlagInstance(flagInstance, state);
    }
    state.writeEndElement(); // xs:extension

    state.writeEndElement(); // xs:simpleContent or xs:complexContent
  }
}
