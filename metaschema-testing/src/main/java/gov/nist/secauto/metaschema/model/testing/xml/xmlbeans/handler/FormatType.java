/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.handler;

import gov.nist.secauto.metaschema.databind.io.Format;

/**
 * An XMLBeans value handler for parsing and writing {@link Format} values.
 */
public final class FormatType {
  private FormatType() {
    // disable
  }

  /**
   * Sets the value of obj onto the given simple value target.
   *
   * @param obj
   *          the boolean value to set
   * @param target
   *          the XML value to cast to a boolean
   */
  public static void encodeFormatType(Format obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      switch (obj) {
      case JSON:
      case XML:
      case YAML:
        target.setStringValue(obj.name());
        break;
      default:
        throw new UnsupportedOperationException(String.format("Unsupported format type '%s'", obj.toString()));
      }
    }
  }

  /**
   * Returns an appropriate Java object from the given simple value.
   *
   * @param obj
   *          the XML value to cast to a boolean
   * @return the associated boolean value
   */
  public static Format decodeFormatType(org.apache.xmlbeans.SimpleValue obj) {
    return Format.valueOf(obj.getStringValue());
  }
}
