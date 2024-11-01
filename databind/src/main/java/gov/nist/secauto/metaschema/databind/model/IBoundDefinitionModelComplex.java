/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.info.IFeatureComplexItemValueHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Predicate;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents a field or assembly instance bound to Java class.
 */
public interface IBoundDefinitionModelComplex
    extends IBoundDefinitionModel<IBoundObject>, IFeatureComplexItemValueHandler {

  @NonNull
  Map<String, IBoundProperty<?>> getJsonProperties(@Nullable Predicate<IBoundInstanceFlag> flagFilter);

  @Nullable
  Method getBeforeDeserializeMethod();

  /**
   * Calls the method named "beforeDeserialize" on each class in the object's
   * hierarchy if the method exists on the class.
   * <p>
   * These methods can be used to set the initial state of the target bound object
   * before data is read and applied during deserialization.
   *
   * @param targetObject
   *          the data object target to call the method(s) on
   * @param parentObject
   *          the object target's parent object, which is used as the method
   *          argument
   * @throws BindingException
   *           if an error occurs while calling the method
   */
  @Override
  default void callBeforeDeserialize(IBoundObject targetObject, IBoundObject parentObject) throws BindingException {
    Method beforeDeserializeMethod = getBeforeDeserializeMethod();
    if (beforeDeserializeMethod != null) {
      try {
        beforeDeserializeMethod.invoke(targetObject, parentObject);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        throw new BindingException(ex);
      }
    }
  }

  @Nullable
  Method getAfterDeserializeMethod();

  /**
   * Calls the method named "afterDeserialize" on each class in the object's
   * hierarchy if the method exists.
   * <p>
   * These methods can be used to modify the state of the target bound object
   * after data is read and applied during deserialization.
   *
   * @param targetObject
   *          the data object target to call the method(s) on
   * @param parentObject
   *          the object target's parent object, which is used as the method
   *          argument
   * @throws BindingException
   *           if an error occurs while calling the method
   */
  @Override
  default void callAfterDeserialize(IBoundObject targetObject, IBoundObject parentObject) throws BindingException {
    Method afterDeserializeMethod = getAfterDeserializeMethod();
    if (afterDeserializeMethod != null) {
      try {
        afterDeserializeMethod.invoke(targetObject, parentObject);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        throw new BindingException(ex);
      }
    }
  }

  // @Override
  // public String getJsonKeyFlagName() {
  // // definition items never have a JSON key
  // return null;
  // }

}
