/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

/**
 * Represents a field instance within an assembly definition.
 * <p>
 * A field instance references a field definition and specifies how that field
 * is used within its containing assembly, including XML wrapping behavior.
 */
public interface IFieldInstance extends IField, INamedModelInstance, IValuedInstance {
  /**
   * The default value for whether a field is wrapped in XML.
   */
  boolean DEFAULT_FIELD_IN_XML_WRAPPED = true;

  /**
   * Retrieves the field definition referenced by this instance.
   *
   * @return the field definition
   */
  @Override
  IFieldDefinition getDefinition();

  /**
   * Determines if the field is configured to have a wrapper in XML.
   *
   * @return {@code true} if an XML wrapper is required, or {@code false}
   *         otherwise
   * @see #DEFAULT_FIELD_IN_XML_WRAPPED
   */
  default boolean isInXmlWrapped() {
    return DEFAULT_FIELD_IN_XML_WRAPPED;
  }
}
