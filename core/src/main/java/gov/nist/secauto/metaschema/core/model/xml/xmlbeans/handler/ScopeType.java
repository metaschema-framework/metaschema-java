/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

import gov.nist.secauto.metaschema.core.model.IDefinition;

/**
 * Supports reading and writing Metaschema constraint scope behavior strings
 * based on the
 * {@link gov.nist.secauto.metaschema.core.model.IDefinition.ModuleScope}
 * enumeration.
 */
public final class ScopeType {
  private ScopeType() {
    // disable construction
  }

  /**
   * Sets the value of obj onto the given simple value target.
   *
   * @param value
   *          the boolean value to set
   * @param target
   *          the XML value to cast to a scope
   */
  public static void encodeScopeType(IDefinition.ModuleScope value, org.apache.xmlbeans.SimpleValue target) {
    if (value != null) {
      switch (value) {
      case PUBLIC:
        target.setStringValue("global");
        break;
      case PRIVATE:
        target.setStringValue("local");
        break;
      default:
        throw new UnsupportedOperationException(value.toString());
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
  public static IDefinition.ModuleScope decodeScopeType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    IDefinition.ModuleScope retval;
    switch (value) {
    case "global":
      retval = IDefinition.ModuleScope.PUBLIC;
      break;
    case "local":
      retval = IDefinition.ModuleScope.PRIVATE;
      break;
    default:
      throw new UnsupportedOperationException(value);
    }
    return retval;
  }
}
