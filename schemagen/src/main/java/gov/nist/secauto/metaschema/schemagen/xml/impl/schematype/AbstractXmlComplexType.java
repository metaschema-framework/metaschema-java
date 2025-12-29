/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl.schematype;

import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.xml.XmlSchemaGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.DocumentationGenerator;
import gov.nist.secauto.metaschema.schemagen.xml.impl.XmlGenerationState;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a common base implementation for XML complex type schema elements.
 * <p>
 * This class represents a complex type in an XML schema that corresponds to a
 * Metaschema model definition (assembly or field with flags).
 *
 * @param <D>
 *          the type of model definition this complex type represents
 */
public abstract class AbstractXmlComplexType<D extends IModelDefinition>
    extends AbstractXmlType
    implements IXmlComplexType {
  @NonNull
  private final D definition;

  /**
   * Construct a new complex type.
   *
   * @param qname
   *          the qualified name for the type
   * @param definition
   *          the model definition this type represents
   */
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

  /**
   * Generate the body content of the complex type.
   *
   * @param state
   *          the generation state for context and writing
   * @throws XMLStreamException
   *           if an error occurs while writing the XML
   */
  protected abstract void generateTypeBody(@NonNull XmlGenerationState state) throws XMLStreamException;

  /**
   * Generate an XML schema attribute declaration for a flag instance.
   *
   * @param instance
   *          the flag instance to generate an attribute for
   * @param state
   *          the generation state for context and writing
   * @throws XMLStreamException
   *           if an error occurs while writing the XML
   */
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
