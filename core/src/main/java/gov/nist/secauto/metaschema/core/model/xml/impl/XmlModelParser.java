/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.xml.impl;

import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.constraint.ConstraintInitializationException;
import gov.nist.secauto.metaschema.core.model.xml.xmlbeans.GroupAsType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigInteger;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

//@SuppressWarnings("PMD.CouplingBetweenObjects")
public final class XmlModelParser {
  private XmlModelParser() {
    // disable construction
  }

  /**
   * Get the group-as/@in-json value based on the XMLBeans representation.
   *
   * @param groupAs
   *          the XMLBeans value
   * @return the in-json value
   */
  @NonNull
  public static JsonGroupAsBehavior getJsonGroupAsBehavior(@Nullable GroupAsType groupAs) {
    JsonGroupAsBehavior retval = IGroupable.DEFAULT_JSON_GROUP_AS_BEHAVIOR;
    if (groupAs != null && groupAs.isSetInJson()) {
      retval = ObjectUtils.notNull(groupAs.getInJson());
    }
    return retval;
  }

  /**
   * Get the group-as/@in-xml value based on the XMLBeans representation.
   *
   * @param groupAs
   *          the XMLBeans value
   * @return the in-xml value
   */
  @NonNull
  public static XmlGroupAsBehavior getXmlGroupAsBehavior(@Nullable GroupAsType groupAs) {
    XmlGroupAsBehavior retval = IGroupable.DEFAULT_XML_GROUP_AS_BEHAVIOR;
    if (groupAs != null && groupAs.isSetInXml()) {
      retval = ObjectUtils.notNull(groupAs.getInXml());
    }
    return retval;
  }

  /**
   * Convert the XMLBeans max occurrence to an integer value.
   *
   * @param value
   *          the XMLBeans value
   * @return the integer value
   */
  public static int getMinOccurs(@Nullable BigInteger value) {
    int retval = IGroupable.DEFAULT_GROUP_AS_MIN_OCCURS;
    if (value != null) {
      retval = value.intValueExact();
    }
    return retval;
  }

  /**
   * Convert the XMLBeans max occurrence to an integer value.
   * <p>
   * If the source value is "unbounded", the the value {@code -1} is used.
   *
   * @param value
   *          the XMLBeans value
   * @return the integer value
   */
  public static int getMaxOccurs(@Nullable Object value) {
    int retval = IGroupable.DEFAULT_GROUP_AS_MAX_OCCURS;
    if (value != null) {
      if (value instanceof String) {
        // must be "unbounded"
        retval = -1;
      } else if (value instanceof BigInteger) {
        retval = ((BigInteger) value).intValueExact();
      } else {
        throw new ConstraintInitializationException("Invalid type: " + value.getClass().getName());
      }
    }
    return retval;
  }
}
