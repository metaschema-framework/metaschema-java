/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen.json.impl;

import gov.nist.secauto.metaschema.core.model.IGroupable;
import gov.nist.secauto.metaschema.core.model.IModelInstance;

public interface IGroupableModelInstanceJsonProperty<I extends IModelInstance & IGroupable>
    extends IJsonProperty<I> {
  default int getMinOccurs() {
    return getInstance().getMinOccurs();
  }

  default int getMaxOccurs() {
    return getInstance().getMaxOccurs();
  }

  @Override
  default boolean isRequired() {
    return getMinOccurs() > 0;
  }
}
