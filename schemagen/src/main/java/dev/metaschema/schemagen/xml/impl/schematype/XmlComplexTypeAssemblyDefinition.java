/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.schemagen.xml.impl.schematype;

import dev.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import dev.metaschema.core.model.IAssemblyDefinition;
import dev.metaschema.core.model.IChoiceGroupInstance;
import dev.metaschema.core.model.IChoiceInstance;
import dev.metaschema.core.model.IFieldInstanceAbsolute;
import dev.metaschema.core.model.IFlagInstance;
import dev.metaschema.core.model.IModelInstanceAbsolute;
import dev.metaschema.core.model.INamedModelInstanceAbsolute;
import dev.metaschema.core.model.INamedModelInstanceGrouped;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.schemagen.SchemaGenerationException;
import dev.metaschema.schemagen.xml.impl.DocumentationGenerator;
import dev.metaschema.schemagen.xml.impl.IXmlGenerationState;
import dev.metaschema.schemagen.xml.impl.XmlDatatypeManager;

import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XML Schema complex type implementation for Metaschema assembly
 * definitions.
 * <p>
 * This class generates XML Schema complexType elements that represent
 * Metaschema assemblies, including their child model instances (fields,
 * assemblies, choices) and flag instances (attributes).
 */
public class XmlComplexTypeAssemblyDefinition
    extends AbstractXmlComplexType<IAssemblyDefinition> {

  /**
   * Construct a new complex type for an assembly definition.
   *
   * @param qname
   *          the qualified name for the XML Schema type
   * @param definition
   *          the Metaschema assembly definition to generate the type for
   */
  public XmlComplexTypeAssemblyDefinition(
      @NonNull QName qname,
      @NonNull IAssemblyDefinition definition) {
    super(qname, definition);
  }

  @Override
  protected void generateTypeBody(IXmlGenerationState state) throws XMLStreamException {
    IAssemblyDefinition definition = getDefinition();

    Collection<? extends IModelInstanceAbsolute> modelInstances = definition.getModelInstances();
    if (!modelInstances.isEmpty()) {
      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "sequence", XmlDatatypeManager.NS_XML_SCHEMA);
      for (IModelInstanceAbsolute modelInstance : modelInstances) {
        assert modelInstance != null;
        generateModelInstance(modelInstance, state);
      }
      state.writeEndElement();
    }

    Collection<? extends IFlagInstance> flagInstances = definition.getFlagInstances();
    if (!flagInstances.isEmpty()) {
      for (IFlagInstance flagInstance : flagInstances) {
        assert flagInstance != null;
        generateFlagInstance(flagInstance, state);
      }
    }
  }

  /**
   * Generate XML Schema elements for a model instance.
   * <p>
   * Handles grouped elements, assemblies, fields (wrapped and unwrapped),
   * choices, and choice groups.
   *
   * @param modelInstance
   *          the model instance to generate schema elements for
   * @param state
   *          the schema generation state for writing output
   * @throws XMLStreamException
   *           if an error occurs while writing XML
   */
  protected void generateModelInstance( // NOPMD acceptable complexity
      @NonNull IModelInstanceAbsolute modelInstance,
      @NonNull IXmlGenerationState state)
      throws XMLStreamException {

    boolean grouped = false;
    if (XmlGroupAsBehavior.GROUPED.equals(modelInstance.getXmlGroupAsBehavior())) {
      // handle grouping
      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "element", XmlDatatypeManager.NS_XML_SCHEMA);

      IEnhancedQName groupAsQName = ObjectUtils.requireNonNull(modelInstance.getEffectiveXmlGroupAsQName());

      if (!state.getDefaultNS().equals(groupAsQName.getNamespace())) {
        throw new SchemaGenerationException(
            String.format("Attempt to create element '%s' on definition '%s' with different namespace", groupAsQName,
                getDefinition().toCoordinates()));
      }
      state.writeAttribute("name", ObjectUtils.requireNonNull(groupAsQName.getLocalName()));

      if (modelInstance.getMinOccurs() == 0) {
        // this is an optional instance group
        state.writeAttribute("minOccurs", "0");
      }

      // now generate the child elements of the group
      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "complexType", XmlDatatypeManager.NS_XML_SCHEMA);
      state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "sequence", XmlDatatypeManager.NS_XML_SCHEMA);

      // mark that we need to close these elements
      grouped = true;
    }

    switch (modelInstance.getModelType()) {
    case ASSEMBLY:
      generateNamedModelInstance((INamedModelInstanceAbsolute) modelInstance, grouped, state);
      break;
    case FIELD: {
      IFieldInstanceAbsolute fieldInstance = (IFieldInstanceAbsolute) modelInstance;
      if (fieldInstance.isEffectiveValueWrappedInXml()) {
        generateNamedModelInstance(fieldInstance, grouped, state);
      } else {
        generateUnwrappedFieldInstance(fieldInstance, grouped, state);
      }
      break;
    }
    case CHOICE:
      generateChoiceModelInstance((IChoiceInstance) modelInstance, state);
      break;
    case CHOICE_GROUP:
      generateChoiceGroupInstance((IChoiceGroupInstance) modelInstance, state);
      break;
    case FLAG:
      throw new UnsupportedOperationException(modelInstance.getModelType().toString());
    }

    if (grouped) {
      state.writeEndElement(); // xs:sequence
      state.writeEndElement(); // xs:complexType
      state.writeEndElement(); // xs:element
    }
  }

  /**
   * Generate an XML Schema element declaration for a named model instance.
   *
   * @param modelInstance
   *          the named model instance to generate a declaration for
   * @param grouped
   *          {@code true} if the instance is within a grouping element
   * @param state
   *          the schema generation state for writing output
   * @throws XMLStreamException
   *           if an error occurs while writing XML
   */
  protected void generateNamedModelInstance(
      @NonNull INamedModelInstanceAbsolute modelInstance,
      boolean grouped,
      @NonNull IXmlGenerationState state) throws XMLStreamException {
    state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "element", XmlDatatypeManager.NS_XML_SCHEMA);

    state.writeAttribute("name", modelInstance.getEffectiveName());

    // state.generateElementNameOrRef(modelInstance);

    if (!grouped && modelInstance.getMinOccurs() != 1) {
      state.writeAttribute("minOccurs", ObjectUtils.notNull(Integer.toString(modelInstance.getMinOccurs())));
    }

    if (modelInstance.getMaxOccurs() != 1) {
      state.writeAttribute("maxOccurs",
          modelInstance.getMaxOccurs() == -1 ? "unbounded"
              : ObjectUtils.notNull(Integer.toString(modelInstance.getMaxOccurs())));
    }

    IXmlType type = state.getXmlForDefinition(modelInstance.getDefinition());
    if (type.isGeneratedType(state) && type.isInline(state)) {
      DocumentationGenerator.generateDocumentation(modelInstance, state);
      type.generate(state);
    } else {
      state.writeAttribute("type", type.getTypeReference());
      DocumentationGenerator.generateDocumentation(modelInstance, state);
    }
    state.writeEndElement(); // xs:element
  }

  /**
   * Generate an XML Schema group reference for an unwrapped field instance.
   * <p>
   * Unwrapped fields are used for multiline markup content that appears directly
   * within the parent element without a wrapper element.
   *
   * @param fieldInstance
   *          the unwrapped field instance to generate a reference for
   * @param grouped
   *          {@code true} if the instance is within a grouping element
   * @param state
   *          the schema generation state for writing output
   * @throws XMLStreamException
   *           if an error occurs while writing XML
   */
  protected static void generateUnwrappedFieldInstance(
      @NonNull IFieldInstanceAbsolute fieldInstance,
      boolean grouped,
      @NonNull IXmlGenerationState state) throws XMLStreamException {

    if (!MarkupDataTypeProvider.MARKUP_MULTILINE.equals(fieldInstance.getDefinition().getJavaTypeAdapter())) {
      throw new IllegalStateException();
    }

    state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "group", XmlDatatypeManager.NS_XML_SCHEMA);

    state.writeAttribute("ref", "blockElementGroup");

    // minOccurs=1 is the schema default
    if (!grouped && fieldInstance.getMinOccurs() != 1) {
      state.writeAttribute("minOccurs", ObjectUtils.notNull(Integer.toString(fieldInstance.getMinOccurs())));
    }

    // if (fieldInstance.getMaxOccurs() != 1) {
    // state.writeAttribute("maxOccurs",
    // fieldInstance.getMaxOccurs() == -1 ? "unbounded"
    // : ObjectUtils.notNull(Integer.toString(fieldInstance.getMaxOccurs())));
    // }

    // unwrapped fields always have a max-occurance of 1. Since the markup multiline
    // is unbounded, this
    // value is unbounded.
    state.writeAttribute("maxOccurs", "unbounded");

    DocumentationGenerator.generateDocumentation(fieldInstance, state);

    state.writeEndElement(); // xs:group
  }

  /**
   * Generate an XML Schema choice element for a choice model instance.
   *
   * @param choice
   *          the choice instance to generate schema elements for
   * @param state
   *          the schema generation state for writing output
   * @throws XMLStreamException
   *           if an error occurs while writing XML
   */
  protected void generateChoiceModelInstance(
      @NonNull IChoiceInstance choice,
      @NonNull IXmlGenerationState state) throws XMLStreamException {
    state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "choice", XmlDatatypeManager.NS_XML_SCHEMA);

    for (IModelInstanceAbsolute instance : choice.getModelInstances()) {
      assert instance != null;

      if (instance instanceof IChoiceInstance) {
        generateChoiceModelInstance((IChoiceInstance) instance, state);
      } else {
        generateModelInstance(instance, state);
      }
    }

    state.writeEndElement(); // xs:choice
  }

  private void generateChoiceGroupInstance(IChoiceGroupInstance choiceGroup, IXmlGenerationState state)
      throws XMLStreamException {
    state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "choice", XmlDatatypeManager.NS_XML_SCHEMA);

    int min = choiceGroup.getMinOccurs();
    if (min != 1) {
      state.writeAttribute("minOccurs", ObjectUtils.notNull(Integer.toString(min)));
    }

    int max = choiceGroup.getMaxOccurs();
    if (max < 0) {
      state.writeAttribute("maxOccurs", "unbounded");
    } else if (max > 1) {
      state.writeAttribute("maxOccurs", ObjectUtils.notNull(Integer.toString(max)));
    }

    for (INamedModelInstanceGrouped instance : choiceGroup.getNamedModelInstances()) {
      assert instance != null;

      generateGroupedNamedModelInstance(instance, state);
    }

    state.writeEndElement(); // xs:choice
  }

  /**
   * Generate an XML Schema element declaration for a grouped named model
   * instance.
   * <p>
   * Grouped instances appear within choice groups and do not have occurrence
   * constraints at the element level since these are handled by the parent
   * choice.
   *
   * @param instance
   *          the grouped named model instance to generate a declaration for
   * @param state
   *          the schema generation state for writing output
   * @throws XMLStreamException
   *           if an error occurs while writing XML
   */
  protected void generateGroupedNamedModelInstance(
      @NonNull INamedModelInstanceGrouped instance,
      @NonNull IXmlGenerationState state) throws XMLStreamException {
    state.writeStartElement(XmlDatatypeManager.PREFIX_XML_SCHEMA, "element", XmlDatatypeManager.NS_XML_SCHEMA);

    state.writeAttribute("name", instance.getEffectiveName());

    // state.generateElementNameOrRef(modelInstance);

    IXmlType type = state.getXmlForDefinition(instance.getDefinition());
    if (type.isGeneratedType(state) && type.isInline(state)) {
      DocumentationGenerator.generateDocumentation(instance, state);
      type.generate(state);
    } else {
      state.writeAttribute("type", type.getTypeReference());
      DocumentationGenerator.generateDocumentation(instance, state);
    }
    state.writeEndElement(); // xs:element
  }
}
