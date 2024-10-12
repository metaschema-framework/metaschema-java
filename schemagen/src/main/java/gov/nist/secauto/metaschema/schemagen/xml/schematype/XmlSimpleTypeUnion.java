/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.schematype;

import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class XmlSimpleTypeUnion
    extends AbstractXmlSimpleType {
  @NonNull
  private final List<IXmlSimpleType> simpleTypes;

  public XmlSimpleTypeUnion(
      @NonNull QName qname,
      @NonNull IValuedDefinition definition,
      @NonNull IXmlSimpleType... simpleTypes) {
    super(qname, definition);
    this.simpleTypes = CollectionUtil.requireNonEmpty(CollectionUtil.listOrEmpty(simpleTypes));
  }

  @NonNull
  public List<IXmlSimpleType> getSimpleTypes() {
    return simpleTypes;
  }

  @Override
  public boolean isInline(XmlGenerationState state) {
    return true;
  }

  @Override
  public void generate(XmlGenerationState state) { // NOPMD unavoidable complexity
    try {
      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "simpleType", XmlSchemaGenerator.NS_XML_SCHEMA);

      if (!isInline(state)) {
        state.writeAttribute("name", ObjectUtils.notNull(getQName().getLocalPart()));
      }

      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "union", XmlSchemaGenerator.NS_XML_SCHEMA);

      List<IXmlSimpleType> memberTypes = new LinkedList<>();
      List<IXmlSimpleType> inlineTypes = new LinkedList<>();
      for (IXmlSimpleType unionType : simpleTypes) {
        if (unionType.isGeneratedType(state) && unionType.isInline(state)) {
          inlineTypes.add(unionType);
        } else {
          memberTypes.add(unionType);
        }
      }

      if (!memberTypes.isEmpty()) {
        state.writeAttribute(
            "memberTypes",
            ObjectUtils.notNull(memberTypes.stream()
                .map(IXmlSimpleType::getTypeReference)
                .collect(Collectors.joining(" "))));
      }

      for (IXmlSimpleType inlineType : inlineTypes) {
        inlineType.generate(state);
      }

      state.writeEndElement(); // xs:union
      state.writeEndElement(); // xs:simpleType

      for (IXmlSimpleType memberType : memberTypes) {
        memberType.generate(state);
      }
    } catch (XMLStreamException ex) {
      throw new SchemaGenerationException(ex);
    }
  }
}
