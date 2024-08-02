/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

public final class YesNoType {
  private YesNoType() {
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
  public static void encodeYesNoType(Boolean obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      if (obj) {
        target.setStringValue("yes");
      } else {
        target.setStringValue("no");
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
  public static Boolean decodeYesNoType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    Boolean retval;
    if ("yes".equals(value)) {
      retval = Boolean.TRUE;
    } else {
      retval = Boolean.FALSE;
    }
    return retval;
  }
}
