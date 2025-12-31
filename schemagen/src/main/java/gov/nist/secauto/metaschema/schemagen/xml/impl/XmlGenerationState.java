/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IFieldDefinition;
import gov.nist.secauto.metaschema.core.model.IFlagDefinition;
import gov.nist.secauto.metaschema.core.model.IModelElement;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValue;
import gov.nist.secauto.metaschema.core.util.AutoCloser;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.schemagen.AbstractGenerationState;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationException;
import gov.nist.secauto.metaschema.schemagen.SchemaGenerationFeature;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.IXmlComplexType;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.IXmlSimpleType;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.IXmlType;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.XmlComplexTypeAssemblyDefinition;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.XmlComplexTypeFieldDefinition;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.XmlSimpleTypeDataTypeReference;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.XmlSimpleTypeDataTypeRestriction;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.XmlSimpleTypeUnion;

import org.codehaus.stax2.XMLStreamWriter2;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Manages state and provides utility methods during XML Schema generation.
 * <p>
 * This class tracks types, namespaces, and provides methods for writing XML
 * Schema elements during the schema generation process.
 */
public class XmlGenerationState
    extends AbstractGenerationState<AutoCloser<XMLStreamWriter2, SchemaGenerationException>, XmlDatatypeManager>
    implements IXmlGenerationState {
  @NonNull
  private final String defaultNS;
  @NonNull
  private final Map<String, String> namespaceToPrefixMap = new ConcurrentHashMap<>();
  @NonNull
  private final Map<IDataTypeAdapter<?>, IXmlSimpleType> dataTypeToSimpleTypeMap = new ConcurrentHashMap<>();
  @NonNull
  private final Map<IValuedDefinition, IXmlSimpleType> definitionToSimpleTypeMap = new ConcurrentHashMap<>();
  @NonNull
  private final Map<IDefinition, IXmlType> definitionToTypeMap = new ConcurrentHashMap<>();

  private final AtomicInteger prefixNum = new AtomicInteger(); // 0

  /**
   * Constructs a new XML generation state for the given module.
   *
   * @param module
   *          the Metaschema module being generated
   * @param writer
   *          the auto-closing XML stream writer wrapper
   * @param configuration
   *          the schema generation configuration options
   */
  public XmlGenerationState(
      @NonNull IModule module,
      @NonNull AutoCloser<XMLStreamWriter2, SchemaGenerationException> writer,
      @NonNull IConfiguration<SchemaGenerationFeature<?>> configuration) {
    super(module, writer, configuration, new XmlDatatypeManager());
    this.defaultNS = ObjectUtils.notNull(module.getXmlNamespace().toASCIIString());
  }

  /**
   * Retrieves the underlying XML stream writer.
   *
   * @return the XML stream writer
   */
  @Override
  @NonNull
  public XMLStreamWriter2 getXMLStreamWriter() {
    return getWriter().getResource();
  }

  /**
   * Retrieves the default XML namespace for this schema.
   *
   * @return the default namespace URI
   */
  @Override
  @NonNull
  public String getDefaultNS() {
    return defaultNS;
  }

  /**
   * Retrieves the namespace used for datatype definitions.
   *
   * @return the datatype namespace URI
   */
  @NonNull
  public String getDatatypeNS() {
    return getDefaultNS();
  }

  /**
   * Retrieves the XML namespace for the given model element.
   *
   * @param modelElement
   *          the model element to get the namespace for
   * @return the XML namespace URI
   */
  @SuppressWarnings("null")
  @Override
  @NonNull
  public String getNS(@NonNull IModelElement modelElement) {
    return modelElement.getContainingModule().getXmlNamespace().toASCIIString();
  }

  /**
   * Retrieves or generates a namespace prefix for the given namespace.
   * <p>
   * Returns {@code null} for the default namespace. For other namespaces,
   * generates a unique prefix if one does not already exist.
   *
   * @param namespace
   *          the namespace URI to get a prefix for
   * @return the namespace prefix, or {@code null} if it is the default namespace
   */
  public String getNSPrefix(String namespace) {
    String retval = null;
    if (!getDefaultNS().equals(namespace)) {
      retval = namespaceToPrefixMap.computeIfAbsent(
          namespace,
          key -> String.format("ns%d", prefixNum.incrementAndGet()));
    }
    return retval;
  }

  /**
   * Creates a new qualified name with the given local name and namespace.
   *
   * @param localName
   *          the local name for the QName
   * @param namespace
   *          the namespace URI for the QName
   * @return a new QName with an appropriate prefix
   */
  @NonNull
  protected QName newQName(
      @NonNull String localName,
      @NonNull String namespace) {
    String prefix = null;
    if (!getDefaultNS().equals(namespace)) {
      prefix = getNSPrefix(namespace);
    }

    return ObjectUtils.notNull(
        prefix == null ? new QName(namespace, localName) : new QName(namespace, localName, prefix));
  }

  /**
   * Creates a new qualified name for a definition type.
   *
   * @param definition
   *          the definition to create a type name for
   * @param suffix
   *          an optional suffix to append to the type name, or {@code null}
   * @return a new QName for the definition type
   */
  @NonNull
  protected QName newQName(
      @NonNull IDefinition definition,
      @Nullable String suffix) {
    return newQName(
        getTypeNameForDefinition(definition, suffix),
        getNS(definition));
  }

  /**
   * Retrieves or creates the XML type representation for a definition.
   * <p>
   * Creates and caches the appropriate XML type based on the definition's model
   * type (flag, field, or assembly).
   *
   * @param definition
   *          the definition to get the XML type for
   * @return the XML type representing the definition
   * @throws UnsupportedOperationException
   *           if the definition is a choice or choice group
   */
  @Override
  public IXmlType getXmlForDefinition(@NonNull IDefinition definition) {
    IXmlType retval = definitionToTypeMap.get(definition);
    if (retval == null) {
      switch (definition.getModelType()) {
      case FIELD: {
        IFieldDefinition field = (IFieldDefinition) definition;
        if (field.getFlagInstances().isEmpty()) {
          retval = getSimpleType(field);
        } else {
          retval = newComplexType(field);
        }
        break;
      }
      case ASSEMBLY: {
        retval = newComplexType((IAssemblyDefinition) definition);
        break;
      }
      case FLAG:
        retval = getSimpleType((IFlagDefinition) definition);
        break;
      case CHOICE_GROUP:
      case CHOICE:
        throw new UnsupportedOperationException(definition.getModelType().toString());
      }
      assert retval != null : definition.getModelType();
      definitionToTypeMap.put(definition, retval);
    }
    return retval;
  }

  /**
   * Retrieves or creates a simple type for a data type adapter.
   *
   * @param dataType
   *          the data type adapter
   * @return the XML simple type representation
   */
  @Override
  @NonNull
  public IXmlSimpleType getSimpleType(@NonNull IDataTypeAdapter<?> dataType) {
    IXmlSimpleType type = dataTypeToSimpleTypeMap.get(dataType);
    if (type == null) {
      // lazy initialize and cache the type
      QName qname = newQName(
          getDatatypeManager().getTypeNameForDatatype(dataType),
          getDatatypeNS());
      type = new XmlSimpleTypeDataTypeReference(qname, dataType);
      dataTypeToSimpleTypeMap.put(dataType, type);
    }
    return type;
  }

  /**
   * Retrieves or creates a simple type for a valued definition.
   * <p>
   * If the definition has allowed value constraints, creates an appropriate
   * restriction or union type. Otherwise, returns the simple type for the
   * underlying data type.
   *
   * @param definition
   *          the valued definition
   * @return the XML simple type representation
   */
  @Override
  @NonNull
  public IXmlSimpleType getSimpleType(@NonNull IValuedDefinition definition) {
    IXmlSimpleType simpleType = definitionToSimpleTypeMap.get(definition);
    if (simpleType == null) {
      AllowedValueCollection allowedValuesCollection = getContextIndependentEnumeratedValues(definition);
      List<IAllowedValue> allowedValues = allowedValuesCollection.getValues();

      IDataTypeAdapter<?> dataType = definition.getJavaTypeAdapter();
      if (allowedValues.isEmpty()) {
        // just use the built-in type
        simpleType = getSimpleType(dataType);
      } else {

        // generate a restriction on the built-in type for the enumerated values
        simpleType = new XmlSimpleTypeDataTypeRestriction(
            newQName(definition, null),
            definition,
            allowedValuesCollection);

        if (!allowedValuesCollection.isClosed()) {
          // if other values are allowed, we need to make a union of the restriction type
          // and the base
          // built-in type
          simpleType = new XmlSimpleTypeUnion(
              newQName(definition, "Union"),
              definition,
              getSimpleType(dataType),
              simpleType);
        }
      }

      definitionToSimpleTypeMap.put(definition, simpleType);
    }
    return simpleType;
  }

  /**
   * Creates a new complex type for a field definition.
   *
   * @param definition
   *          the field definition
   * @return a new complex type representation
   */
  @NonNull
  protected IXmlComplexType newComplexType(@NonNull IFieldDefinition definition) {
    QName qname = newQName(definition, null);
    return new XmlComplexTypeFieldDefinition(qname, definition);
  }

  /**
   * Creates a new complex type for an assembly definition.
   *
   * @param definition
   *          the assembly definition
   * @return a new complex type representation
   */
  @NonNull
  protected IXmlComplexType newComplexType(@NonNull IAssemblyDefinition definition) {
    QName qname = newQName(definition, null);
    return new XmlComplexTypeAssemblyDefinition(qname, definition);
  }

  /**
   * Generates all XML types that are not inline and are referenced.
   * <p>
   * Iterates through all definitions and generates types that need to be written
   * as separate type definitions in the schema.
   *
   * @throws XMLStreamException
   *           if an error occurs while writing XML content
   */
  public void generateXmlTypes() throws XMLStreamException {

    for (IXmlType type : definitionToTypeMap.values()) {
      if (!type.isInline(this) && type.isGeneratedType(this) && type.isReferenced(this)) {
        type.generate(this);
      } else {
        assert !type.isGeneratedType(this) || type.isInline(this) || !type.isReferenced(this);
      }
    }
    getDatatypeManager().generateDatatypes(getXMLStreamWriter());
  }

  /**
   * Writes an attribute to the current element.
   *
   * @param localName
   *          the local name of the attribute
   * @param value
   *          the value of the attribute
   * @throws XMLStreamException
   *           if an error occurs while writing
   */
  @Override
  public void writeAttribute(@NonNull String localName, @NonNull String value) throws XMLStreamException {
    getXMLStreamWriter().writeAttribute(localName, value);
  }

  /**
   * Writes a start element with the given namespace and local name.
   *
   * @param namespaceUri
   *          the namespace URI for the element
   * @param localName
   *          the local name of the element
   * @throws XMLStreamException
   *           if an error occurs while writing
   */
  @Override
  public void writeStartElement(@NonNull String namespaceUri, @NonNull String localName) throws XMLStreamException {
    getXMLStreamWriter().writeStartElement(namespaceUri, localName);
  }

  /**
   * Writes a start element with the given prefix, local name, and namespace.
   *
   * @param prefix
   *          the namespace prefix for the element
   * @param localName
   *          the local name of the element
   * @param namespaceUri
   *          the namespace URI for the element
   * @throws XMLStreamException
   *           if an error occurs while writing
   */
  @Override
  public void writeStartElement(
      @NonNull String prefix,
      @NonNull String localName,
      @NonNull String namespaceUri) throws XMLStreamException {
    getXMLStreamWriter().writeStartElement(prefix, localName, namespaceUri);
  }

  /**
   * Writes an end element for the current element.
   *
   * @throws XMLStreamException
   *           if an error occurs while writing
   */
  @Override
  public void writeEndElement() throws XMLStreamException {
    getXMLStreamWriter().writeEndElement();
  }

  /**
   * Writes character content to the current element.
   *
   * @param text
   *          the text content to write
   * @throws XMLStreamException
   *           if an error occurs while writing
   */
  @Override
  public void writeCharacters(@NonNull String text) throws XMLStreamException {
    getXMLStreamWriter().writeCharacters(text);
  }

  /**
   * Writes a namespace declaration.
   *
   * @param prefix
   *          the namespace prefix
   * @param namespaceUri
   *          the namespace URI
   * @throws XMLStreamException
   *           if an error occurs while writing
   */
  @Override
  public void writeNamespace(String prefix, String namespaceUri) throws XMLStreamException {
    getXMLStreamWriter().writeNamespace(prefix, namespaceUri);
  }

  @Override
  public void flushWriter() throws IOException {
    try {
      getWriter().getResource().flush();
    } catch (XMLStreamException ex) {
      throw new IOException(ex);
    }
  }
}
