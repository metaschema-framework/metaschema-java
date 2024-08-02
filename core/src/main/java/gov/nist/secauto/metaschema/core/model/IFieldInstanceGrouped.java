/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public interface IFieldInstanceGrouped extends INamedModelInstanceGrouped, IFieldInstance {

  /**
   * Determines if the field is configured to have a wrapper in XML.
   *
   * @return {@code true} if an XML wrapper is required, or {@code false}
   *         otherwise
   */
  @Override
  default boolean isInXmlWrapped() {
    // must always be wrapped
    return true;
  }

  @Override
  default boolean isEffectiveValueWrappedInXml() {
    // must always be wrapped
    return true;
  }
}
