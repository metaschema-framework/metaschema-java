/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureComplexItemValueHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;

public interface IBoundInstanceModelFieldComplex
    extends IBoundInstanceModelField<IBoundObject>, IFeatureComplexItemValueHandler {

  @Override
  IBoundDefinitionModelFieldComplex getDefinition();

  @Override
  default boolean isEffectiveValueWrappedInXml() {
    // always wrapped
    return true;
  }

  @Override
  default IBoundObject readItem(IBoundObject parent, IItemReadHandler handler) throws IOException {
    return handler.readItemField(ObjectUtils.requireNonNull(parent, "parent"), this);
  }

  @Override
  default void writeItem(IBoundObject item, IItemWriteHandler handler) throws IOException {
    handler.writeItemField(item, this);
  }

  @Override
  default IBoundObject deepCopyItem(IBoundObject item, IBoundObject parentInstance) throws BindingException {
    return getDefinition().deepCopyItem(item, parentInstance);
  }

  @Override
  default Class<? extends IBoundObject> getBoundClass() {
    return getDefinition().getBoundClass();
  }

  @Override
  default void callBeforeDeserialize(IBoundObject targetObject, IBoundObject parentObject) throws BindingException {
    getDefinition().callBeforeDeserialize(targetObject, parentObject);
  }

  @Override
  default void callAfterDeserialize(IBoundObject targetObject, IBoundObject parentObject) throws BindingException {
    getDefinition().callAfterDeserialize(targetObject, parentObject);
  }
}
