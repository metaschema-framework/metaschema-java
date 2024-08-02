/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class JsonGroupAsBehaviorType {
  private JsonGroupAsBehaviorType() {
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
  @SuppressFBWarnings("SF_SWITCH_NO_DEFAULT")
  public static void encodeJsonGroupAsBehaviorType(JsonGroupAsBehavior obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      switch (obj) {
      case LIST:
        target.setStringValue("ARRAY");
        break;
      case SINGLETON_OR_LIST:
        target.setStringValue("SINGLETON_OR_ARRAY");
        break;
      case KEYED:
        target.setStringValue("BY_KEY");
        break;
      case NONE:
      default:
        // do nothing
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
  public static JsonGroupAsBehavior decodeJsonGroupAsBehaviorType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    JsonGroupAsBehavior retval;
    switch (value) {
    case "ARRAY":
      retval = JsonGroupAsBehavior.LIST;
      break;
    case "SINGLETON_OR_ARRAY":
      retval = JsonGroupAsBehavior.SINGLETON_OR_LIST;
      break;
    case "BY_KEY":
      retval = JsonGroupAsBehavior.KEYED;
      break;
    default:
      throw new UnsupportedOperationException(value);
    }
    return retval;
  }
}
