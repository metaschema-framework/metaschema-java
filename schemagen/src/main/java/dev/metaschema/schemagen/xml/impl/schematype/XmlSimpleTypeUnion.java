/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl.schematype;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import dev.metaschema.core.model.IValuedDefinition;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.schemagen.SchemaGenerationException;
import dev.metaschema.schemagen.xml.impl.IXmlGenerationState;
import dev.metaschema.schemagen.xml.impl.XmlDatatypeManager;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XML Schema simple type that is a union of multiple member types.
 * <p>
 * This class generates an xs:simpleType with xs:union that combines multiple
 * simple types. Member types may be referenced by name or inlined depending on
 * their generation requirements.
 */
public class XmlSimpleTypeUnion
    extends AbstractXmlSimpleType {
  @NonNull
  private final List<IXmlSimpleType> simpleTypes;

  /**
   * Construct a new union type.
   *
   * @param qname
   *          the qualified name for the XML Schema type
   * @param definition
   *          the Metaschema definition that this union applies to
   * @param simpleTypes
   *          the member types to include in the union
   */
  public XmlSimpleTypeUnion(
      @NonNull QName qname,
      @NonNull IValuedDefinition definition,
      @NonNull IXmlSimpleType... simpleTypes) {
    super(qname, definition);
    this.simpleTypes = CollectionUtil.requireNonEmpty(CollectionUtil.listOrEmpty(simpleTypes));
  }

  /**
   * Get the member types that make up this union.
   *
   * @return an unmodifiable list of member simple types
   */
  @NonNull
  public List<IXmlSimpleType> getSimpleTypes() {
    return simpleTypes;
  }

  @Override
  public boolean isInline(IXmlGenerationState state) {
    return true;
  }

  @Override
  public void generate(IXmlGenerationState state) { // NOPMD unavoidable complexity
    try {
      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "simpleType", XmlDatatypeManager.NS_XML_SCHEMA);

      if (!isInline(state)) {
        state.writeAttribute("name", ObjectUtils.notNull(getQName().getLocalPart()));
      }

      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "union", XmlDatatypeManager.NS_XML_SCHEMA);

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
