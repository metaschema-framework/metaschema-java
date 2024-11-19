/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.databind.model.IGroupAs;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureInstanceModelGroupAs extends IGroupable {
  /**
   * Get the underlying group-as provider.
   *
   * @return the group-as provider
   */
  @NonNull
  IGroupAs getGroupAs();

  @Override
  default String getGroupAsName() {
    IEnhancedQName qname = getGroupAs().getGroupAsQName();
    return qname == null ? null : qname.getLocalName();
  }

  @Override
  default IEnhancedQName getEffectiveXmlGroupAsQName() {
    IEnhancedQName retval = null;
    if (XmlGroupAsBehavior.GROUPED.equals(getXmlGroupAsBehavior())) {
      IEnhancedQName qname = getGroupAs().getGroupAsQName();
      if (qname == null) {
        throw new IllegalStateException("Instance is grouped, but no group-as QName was provided.");
      }
      retval = qname;
    }
    return retval;
  }

  @Override
  default JsonGroupAsBehavior getJsonGroupAsBehavior() {
    return getGroupAs().getJsonGroupAsBehavior();
  }

  @Override
  default XmlGroupAsBehavior getXmlGroupAsBehavior() {
    return getGroupAs().getXmlGroupAsBehavior();
  }
}
