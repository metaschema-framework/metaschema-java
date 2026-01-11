/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model;

import dev.metaschema.core.model.IModuleExtended;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a bound Metaschema module that provides access to its field and
 * assembly definitions through Java class bindings.
 */
public interface IBoundModule
    extends IModuleExtended<
        IBoundModule,
        IBoundDefinitionModelComplex,
        IBoundDefinitionFlag,
        IBoundDefinitionModelField<?>,
        IBoundDefinitionModelAssembly> {

  /**
   * Create a new instance of a bound module using reflection.
   *
   * @param clazz
   *          the bound module class to instantiate
   * @param bindingContext
   *          the binding context for the module
   * @param importedModules
   *          the list of modules imported by this module
   * @return the new module instance
   * @throws IllegalArgumentException
   *           if the module cannot be instantiated
   */
  @NonNull
  static IBoundModule newInstance(
      @NonNull Class<? extends IBoundModule> clazz,
      @NonNull IBindingContext bindingContext,
      @NonNull List<? extends IBoundModule> importedModules) {

    Constructor<? extends IBoundModule> constructor;
    try {
      constructor = clazz.getDeclaredConstructor(List.class, IBindingContext.class);
    } catch (NoSuchMethodException ex) {
      throw new IllegalArgumentException(ex);
    }

    try {
      return ObjectUtils.notNull(constructor.newInstance(importedModules, bindingContext));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * Get the Module binding context.
   *
   * @return the context
   */
  @NonNull
  IBindingContext getBindingContext();

  @Override
  default URI getLocation() {
    // not known
    return null;
  }

  @Override
  Collection<IBoundDefinitionModelAssembly> getAssemblyDefinitions();

  @Override
  IBoundDefinitionModelAssembly getAssemblyDefinitionByName(@NonNull Integer name);

  @Override
  Collection<IBoundDefinitionModelField<?>> getFieldDefinitions();

  @Override
  IBoundDefinitionModelField<?> getFieldDefinitionByName(@NonNull Integer name);
}
