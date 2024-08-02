/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;

public final class ConstraintExtensionEnum {
  private ConstraintExtensionEnum() {
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
  public static void encodeExtensibleEnumType(IAllowedValuesConstraint.Extensible obj,
      org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      switch (obj) {
      case NONE:
        target.setStringValue("none");
        break;
      case MODEL:
        target.setStringValue("model");
        break;
      case EXTERNAL:
        target.setStringValue("external");
        break;
      default:
        throw new UnsupportedOperationException(obj.name());
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
  public static IAllowedValuesConstraint.Extensible decodeExtensibleEnumType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    IAllowedValuesConstraint.Extensible retval;
    switch (value) {
    case "none":
      retval = IAllowedValuesConstraint.Extensible.NONE;
      break;
    case "model":
      retval = IAllowedValuesConstraint.Extensible.MODEL;
      break;
    case "external":
      retval = IAllowedValuesConstraint.Extensible.EXTERNAL;
      break;
    default:
      throw new UnsupportedOperationException(value);
    }
    return retval;
  }
}
