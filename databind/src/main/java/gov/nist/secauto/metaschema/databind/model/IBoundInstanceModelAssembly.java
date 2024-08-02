/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IAssemblyInstanceAbsolute;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.impl.InstanceModelAssemblyComplex;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureComplexItemValueHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemReadHandler;
import gov.nist.secauto.metaschema.databind.model.info.IItemWriteHandler;

import java.io.IOException;
import java.lang.reflect.Field;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents an assembly instance bound to Java field.
 */
public interface IBoundInstanceModelAssembly
    extends IBoundInstanceModelNamed<IBoundObject>, IAssemblyInstanceAbsolute, IFeatureComplexItemValueHandler {
  /**
   * Create a new bound assembly instance.
   *
   * @param field
   *          the Java field the instance is bound to
   * @param containingDefinition
   *          the definition containing the instance
   * @return the new instance
   */
  @NonNull
  static IBoundInstanceModelAssembly newInstance(
      @NonNull Field field,
      @NonNull IBoundDefinitionModelAssembly containingDefinition) {
    Class<? extends IBoundObject> itemType = IBoundInstanceModel.getItemType(field, IBoundObject.class);
    IBindingContext bindingContext = containingDefinition.getBindingContext();
    IBoundDefinitionModel<?> definition = bindingContext.getBoundDefinitionForClass(itemType);
    if (definition instanceof IBoundDefinitionModelAssembly) {
      return InstanceModelAssemblyComplex.newInstance(
          field,
          (IBoundDefinitionModelAssembly) definition,
          containingDefinition);
    }

    throw new IllegalStateException(String.format(
        "The field '%s' on class '%s' is not bound to a Metaschema assembly",
        field.toString(),
        field.getDeclaringClass().getName()));
  }

  @Override
  @NonNull
  IBoundDefinitionModelAssembly getDefinition();
  // @Override
  // default Object getValue(Object parent) {
  // return IBoundInstanceModelNamed.super.getValue(parent);
  // }

  // @Override
  // default void setValue(Object parentObject, Object value) {
  // IBoundInstanceModelNamed.super.setValue(parentObject, value);
  // }

  @Override
  default IBoundObject readItem(IBoundObject parent, IItemReadHandler handler) throws IOException {
    return handler.readItemAssembly(ObjectUtils.requireNonNull(parent, "parent"), this);
  }

  @Override
  default void writeItem(IBoundObject item, IItemWriteHandler handler) throws IOException {
    handler.writeItemAssembly(item, this);
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
