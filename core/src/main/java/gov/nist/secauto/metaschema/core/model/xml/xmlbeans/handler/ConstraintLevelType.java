/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;

public final class ConstraintLevelType {
  private ConstraintLevelType() {
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
  public static void encodeConstraintLevelType(IConstraint.Level obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      switch (obj) {
      case CRITICAL:
        target.setStringValue("CRITICAL");
        break;
      case ERROR:
        target.setStringValue("ERROR");
        break;
      case WARNING:
        target.setStringValue("WARNING");
        break;
      case INFORMATIONAL:
        target.setStringValue("INFORMATIONAL");
        break;
      case DEBUG:
        target.setStringValue("DEBUG");
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
  public static IConstraint.Level decodeConstraintLevelType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    IConstraint.Level retval;
    switch (value) {
    case "CRITICAL":
      retval = IConstraint.Level.CRITICAL;
      break;
    case "ERROR":
      retval = IConstraint.Level.ERROR;
      break;
    case "WARNING":
      retval = IConstraint.Level.WARNING;
      break;
    case "INFORMATIONAL":
      retval = IConstraint.Level.INFORMATIONAL;
      break;
    case "DEBUG":
      retval = IConstraint.Level.DEBUG;
      break;
    default:
      throw new UnsupportedOperationException(value);
    }
    return retval;
  }
}
