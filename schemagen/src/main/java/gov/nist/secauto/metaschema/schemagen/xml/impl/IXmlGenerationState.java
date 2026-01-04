/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.xml.impl;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.IDefinition;
import gov.nist.secauto.metaschema.core.model.IModelElement;
import gov.nist.secauto.metaschema.core.model.IValuedDefinition;
import gov.nist.secauto.metaschema.schemagen.ModuleIndex;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.IXmlSimpleType;
import gov.nist.secauto.metaschema.schemagen.xml.impl.schematype.IXmlType;

import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLStreamException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides XML generation state operations needed by schema type classes.
 * <p>
 * This interface defines the contract for XML generation state that schema type
 * implementations depend on. The {@code xml.impl.schematype} classes import
 * this interface from the parent {@code xml.impl} package.
 */
public interface IXmlGenerationState {

  /**
   * Retrieves the underlying XML stream writer.
   *
   * @return the XML stream writer
   */
  @NonNull
  XMLStreamWriter2 getXMLStreamWriter();

  /**
   * Retrieves the default XML namespace for this schema.
   *
   * @return the default namespace URI
   */
  @NonNull
  String getDefaultNS();

  /**
   * Retrieves the module index for this generation state.
   *
   * @return the module index
   */
  @NonNull
  ModuleIndex getMetaschemaIndex();

  /**
   * Determines if a definition should be generated inline.
   *
   * @param definition
   *          the definition to check
   * @return {@code true} if the definition should be inlined, {@code false}
   *         otherwise
   */
  boolean isInline(@NonNull IDefinition definition);

  /**
   * Retrieves or creates the XML type representation for a definition.
   *
   * @param definition
   *          the definition to get the XML type for
   * @return the XML type representing the definition
   */
  @NonNull
  IXmlType getXmlForDefinition(@NonNull IDefinition definition);

  /**
   * Retrieves or creates a simple type for a data type adapter.
   *
   * @param dataType
   *          the data type adapter
   * @return the XML simple type representation
   */
  @NonNull
  IXmlSimpleType getSimpleType(@NonNull IDataTypeAdapter<?> dataType);

  /**
   * Retrieves or creates a simple type for a valued definition.
   *
   * @param definition
   *          the valued definition
   * @return the XML simple type representation
   */
  @NonNull
  IXmlSimpleType getSimpleType(@NonNull IValuedDefinition definition);

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
  void writeAttribute(@NonNull String localName, @NonNull String value) throws XMLStreamException;

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
  void writeStartElement(
      @NonNull String prefix,
      @NonNull String localName,
      @NonNull String namespaceUri) throws XMLStreamException;

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
  void writeStartElement(@NonNull String namespaceUri, @NonNull String localName) throws XMLStreamException;

  /**
   * Writes an end element for the current element.
   *
   * @throws XMLStreamException
   *           if an error occurs while writing
   */
  void writeEndElement() throws XMLStreamException;

  /**
   * Retrieves the XML namespace for the given model element.
   *
   * @param modelElement
   *          the model element to get the namespace for
   * @return the XML namespace URI
   */
  @NonNull
  String getNS(@NonNull IModelElement modelElement);

  /**
   * Writes character content to the current element.
   *
   * @param text
   *          the text content to write
   * @throws XMLStreamException
   *           if an error occurs while writing
   */
  void writeCharacters(@NonNull String text) throws XMLStreamException;

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
  void writeNamespace(@NonNull String prefix, @NonNull String namespaceUri) throws XMLStreamException;
}
