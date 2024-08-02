/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.info;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IMetaschemaData;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.io.BindingException;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Supplier;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IFeatureComplexItemValueHandler extends IItemValueHandler<IBoundObject> {
  /**
   * Get the Metaschema definition representing the bound complex data.
   *
   * @return the definition
   */
  @NonNull
  IBoundDefinitionModelComplex getDefinition();

  // /**
  // * Get the name of the JSON key, if a JSON key is configured.
  // *
  // * @return the name of the JSON key flag if configured, or {@code null}
  // * otherwise
  // */
  // @Nullable
  // String getJsonKeyFlagName();

  /**
   * Get the mapping of JSON property names to property bindings.
   *
   * @return the mapping
   */
  // REFACTOR: move JSON-specific methods to a binding cache implementation
  @NonNull
  Map<String, IBoundProperty<?>> getJsonProperties();

  // REFACTOR: flatten implementations?
  @Override
  @NonNull
  IBoundObject deepCopyItem(
      @NonNull IBoundObject item,
      @Nullable IBoundObject parentInstance) throws BindingException;

  /**
   * The class this binding is to.
   *
   * @return the bound class
   */
  @NonNull
  Class<? extends IBoundObject> getBoundClass();

  /**
   * Gets a new instance of the bound class.
   *
   * @param <CLASS>
   *          the type of the bound class
   * @param supplier
   *          the metaschema data generator used to capture parse information
   *          (i.e., location)
   * @return a Java object for the class
   * @throws RuntimeException
   *           if the instance cannot be created due to a binding error
   */
  @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
  @NonNull
  default <CLASS extends IBoundObject> CLASS newInstance(@Nullable Supplier<IMetaschemaData> supplier) {
    Class<?> clazz = getBoundClass();
    try {
      CLASS retval;
      if (supplier != null) {
        @SuppressWarnings("unchecked") Constructor<CLASS> constructor
            = (Constructor<CLASS>) clazz.getDeclaredConstructor(IMetaschemaData.class);
        retval = constructor.newInstance(supplier.get());
      } else {
        @SuppressWarnings("unchecked") Constructor<CLASS> constructor
            = (Constructor<CLASS>) clazz.getDeclaredConstructor();
        retval = constructor.newInstance();
      }
      return ObjectUtils.notNull(retval);
    } catch (NoSuchMethodException ex) {
      String msg = String.format("Class '%s' does not have a required no-arg constructor.", clazz.getName());
      throw new RuntimeException(msg, ex);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  void callBeforeDeserialize(
      @NonNull IBoundObject targetObject,
      @Nullable IBoundObject parentObject) throws BindingException;

  void callAfterDeserialize(
      @NonNull IBoundObject targetObject,
      @Nullable IBoundObject parentObject) throws BindingException;
}
