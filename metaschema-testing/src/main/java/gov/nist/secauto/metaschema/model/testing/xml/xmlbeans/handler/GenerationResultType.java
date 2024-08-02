/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.model.testing.xml.xmlbeans.handler;

public final class GenerationResultType {
  private GenerationResultType() {
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
  public static void encodeGenerationResultType(Boolean obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      if (obj.booleanValue()) {
        target.setStringValue("SUCCESS");
      } else {
        target.setStringValue("FAILURE");
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
  public static Boolean decodeGenerationResultType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    Boolean retval;
    if ("SUCCESS".equals(value)) {
      retval = Boolean.TRUE;
    } else {
      retval = Boolean.FALSE;
    }
    return retval;
  }
}
