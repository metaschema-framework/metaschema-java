/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.impl;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IBoundInstanceModel;
import gov.nist.secauto.metaschema.databind.model.IGroupAs;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureInstanceModelGroupAs<ITEM> extends IBoundInstanceModel<ITEM> {
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

  @Override
  default void deepCopy(@NonNull IBoundObject fromInstance, @NonNull IBoundObject toInstance) throws BindingException {
    Object value = getValue(fromInstance);
    if (value != null) {
      value = getCollectionInfo().deepCopyItems(fromInstance, toInstance);
    }
    setValue(toInstance, value);
  }
}
