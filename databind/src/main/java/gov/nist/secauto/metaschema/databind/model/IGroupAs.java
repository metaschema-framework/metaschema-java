/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A data object to record the group as selections.
 */
public interface IGroupAs {
  @NonNull
  IGroupAs SINGLETON_GROUP_AS = new IGroupAs() {
    @Override
    public IEnhancedQName getGroupAsQName() {
      return null;
    }

    @Override
    public JsonGroupAsBehavior getJsonGroupAsBehavior() {
      return JsonGroupAsBehavior.NONE;
    }

    @Override
    public XmlGroupAsBehavior getXmlGroupAsBehavior() {
      return XmlGroupAsBehavior.UNGROUPED;
    }
  };

  @Nullable
  IEnhancedQName getGroupAsQName();

  @NonNull
  JsonGroupAsBehavior getJsonGroupAsBehavior();

  @NonNull
  XmlGroupAsBehavior getXmlGroupAsBehavior();
}
