/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.handler;

/**
 * An XMLBeans value handler for parsing and writing boolean validation result
 * type values.
 */

public final class ValidationResultType {
  private ValidationResultType() {
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
  public static void encodeValidationResultType(Boolean obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      if (obj) {
        target.setStringValue("VALID");
      } else {
        target.setStringValue("INVALID");
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
  public static Boolean decodeValidationResultType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    Boolean retval;
    if ("VALID".equals(value)) {
      retval = Boolean.TRUE;
    } else {
      retval = Boolean.FALSE;
    }
    return retval;
  }
}
