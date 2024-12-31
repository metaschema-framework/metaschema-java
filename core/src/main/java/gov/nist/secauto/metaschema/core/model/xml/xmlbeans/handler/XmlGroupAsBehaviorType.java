/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.xmlbeans.handler;

import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;

/**
 * Supports reading and writing Metaschema XML grouping behavior strings based
 * on the {@link XmlGroupAsBehavior} enumeration.
 */
public final class XmlGroupAsBehaviorType {
  private XmlGroupAsBehaviorType() {
    // disable construction
  }

  /**
   * Sets the value of obj onto the given simple value target.
   *
   * @param obj
   *          the XML grouping behavior to encode
   * @param target
   *          the target SimpleValue to store the encoded string
   */
  public static void encodeXmlGroupAsBehaviorType(XmlGroupAsBehavior obj, org.apache.xmlbeans.SimpleValue target) {
    if (obj != null) {
      switch (obj) {
      case GROUPED:
        target.setStringValue("GROUPED");
        break;
      case UNGROUPED:
        target.setStringValue("UNGROUPED");
        break;
      default:
        target.setStringValue(IGroupable.DEFAULT_XML_GROUP_AS_BEHAVIOR.name());
        break;
      }
    }
  }

  /**
   * Returns an appropriate Java object from the given simple value.
   *
   * @param obj
   *          the SimpleValue containing the encoded behavior string
   * @return the decoded XmlGroupAsBehavior value
   * @throws UnsupportedOperationException
   *           if the string value is not recognized
   */
  public static XmlGroupAsBehavior decodeXmlGroupAsBehaviorType(org.apache.xmlbeans.SimpleValue obj) {
    String value = obj.getStringValue();
    XmlGroupAsBehavior retval;
    switch (value) {
    case "GROUPED":
      retval = XmlGroupAsBehavior.GROUPED;
      break;
    case "UNGROUPED":
      retval = XmlGroupAsBehavior.UNGROUPED;
      break;
    default:
      throw new UnsupportedOperationException(value);
    }
    return retval;
  }
}
