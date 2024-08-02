/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

import java.util.regex.Pattern;

public final class RegexTypeHandler {
  private RegexTypeHandler() {
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
  public static void encodeRegexType(Pattern obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      target.setStringValue(obj.pattern());
    }
  }

  /**
   * Returns an appropriate Java object from the given simple value.
   *
   * @param obj
   *          the XML value to cast to a boolean
   * @return the associated boolean value
   */
  public static Pattern decodeRegexType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    return Pattern.compile(value);
  }
}
