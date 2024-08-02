/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

public final class InXmlWrappedType {
  private InXmlWrappedType() {
    // disable construction
  }

  /**
   * Sets the value of obj onto the given simple value target.
   *
   * @param obj
   *          the boolean value to set
   * @param target
   *          the XML value to cast to a boolean
   */
  public static void encodeInXmlWrappedType(Boolean obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      if (obj) {
        target.setStringValue("WRAPPED");
      } else {
        target.setStringValue("UNWRAPPED");
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
  public static Boolean decodeInXmlWrappedType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    Boolean retval;
    switch (value) {
    case "WRAPPED":
    case "WITH_WRAPPER": // deprecated alias
      retval = Boolean.TRUE;
      break;
    case "UNWRAPPED":
      retval = Boolean.FALSE;
      break;
    default:
      throw new UnsupportedOperationException(String.format("Unsupported InXML type '%s'", value));
    }
    return retval;
  }
}
