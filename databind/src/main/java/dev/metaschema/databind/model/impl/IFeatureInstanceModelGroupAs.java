/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.impl;

import dev.metaschema.core.model.IGroupable;
import dev.metaschema.core.model.JsonGroupAsBehavior;
import dev.metaschema.core.model.XmlGroupAsBehavior;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.databind.model.IGroupAs;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A feature interface for model instances that support group-as configuration.
 * <p>
 * This interface provides access to the group-as settings that control how
 * collections of model instances are serialized in XML and JSON.
 */
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
