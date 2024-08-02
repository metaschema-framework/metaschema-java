/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public interface IFieldInstance extends IField, INamedModelInstance, IValuedInstance {
  boolean DEFAULT_FIELD_IN_XML_WRAPPED = true;

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
