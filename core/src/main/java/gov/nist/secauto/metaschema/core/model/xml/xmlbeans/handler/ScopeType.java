/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

import gov.nist.secauto.metaschema.core.model.ModuleScopeEnum;

public final class ScopeType {
  private ScopeType() {
    // disable construction
  }

  /**
   * Sets the value of obj onto the given simple value target.
   *
   * @param obj
   *          the boolean value to set
   * @param target
   *          the XML value to cast to a scope
   */
  public static void encodeScopeType(ModuleScopeEnum obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      switch (obj) {
      case INHERITED:
        target.setStringValue("global");
        break;
      case LOCAL:
        target.setStringValue("local");
        break;
      default:
        throw new UnsupportedOperationException(obj.toString());
      }
    }
  }

  /**
   * Returns an appropriate Java object from the given simple value.
   *
   * @param obj
   *          the XML value to cast to a scope
   * @return the associated scope value
   */
  public static ModuleScopeEnum decodeScopeType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    ModuleScopeEnum retval;
    switch (value) {
    case "global":
      retval = ModuleScopeEnum.INHERITED;
      break;
    case "local":
      retval = ModuleScopeEnum.LOCAL;
      break;
    default:
      throw new UnsupportedOperationException(value);
    }
    return retval;
  }
}
