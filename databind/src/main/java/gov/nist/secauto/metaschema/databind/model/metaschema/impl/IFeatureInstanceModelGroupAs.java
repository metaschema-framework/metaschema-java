/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema.impl;

import gov.nist.secauto.metaschema.core.model.IModelInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.databind.model.IGroupAs;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureInstanceModelGroupAs extends IModelInstanceAbsolute {
  @NonNull
  IGroupAs getGroupAs();

  @Override
  default String getGroupAsName() {
    return getGroupAs().getGroupAsName();
  }

  @Override
  default String getGroupAsXmlNamespace() {
    return getGroupAs().getGroupAsXmlNamespace();
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
