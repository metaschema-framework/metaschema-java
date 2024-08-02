/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.schematype;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.DocumentationGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractXmlComplexType<D extends IModelDefinition>
    extends AbstractXmlType
    implements IXmlComplexType {
  @NonNull
  private final D definition;

  public AbstractXmlComplexType(
      @NonNull QName qname,
      @NonNull D definition) {
    super(qname);
    this.definition = definition;
  }

  @Override
  @NonNull
  public D getDefinition() {
    return definition;
  }

  @Override
  public void generate(@NonNull XmlGenerationState state) {
    try {
      state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "complexType", XmlSchemaGenerator.NS_XML_SCHEMA);

      if (!isInline(state)) {
        state.writeAttribute("name", getTypeName());
      }

      DocumentationGenerator.generateDocumentation(getDefinition(), state);

      generateTypeBody(state);

      state.writeEndElement(); // complexType
    } catch (XMLStreamException ex) {
      throw new SchemaGenerationException(ex);
    }
  }

  protected abstract void generateTypeBody(@NonNull XmlGenerationState state) throws XMLStreamException;

  protected static void generateFlagInstance(@NonNull IFlagInstance instance, @NonNull XmlGenerationState state)
      throws XMLStreamException {
    state.writeStartElement(XmlSchemaGenerator.PREFIX_XML_SCHEMA, "attribute", XmlSchemaGenerator.NS_XML_SCHEMA);

    state.writeAttribute("name", instance.getEffectiveName());

    if (instance.isRequired()) {
      state.writeAttribute("use", "required");
    }

    IXmlType type = state.getXmlForDefinition(instance.getDefinition());
    if (type.isGeneratedType(state) && type.isInline(state)) {
      DocumentationGenerator.generateDocumentation(instance, state);

      type.generate(state);
    } else {
      state.writeAttribute("type", type.getTypeReference());

      DocumentationGenerator.generateDocumentation(instance, state);
    }

    state.writeEndElement(); // xs:attribute
  }

  @Override
  public boolean isInline(XmlGenerationState state) {
    return state.isInline(getDefinition());
  }
}
