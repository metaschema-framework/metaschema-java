/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import java.util.Collection;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides information about a generated Java class that represents a Module
 * module.
 */
public interface IGeneratedModuleClass extends IGeneratedClass {

  /**
   * Get the associated Module module data.
   *
   * @return the module data
   */
  @NonNull
  IModule getModule();

  /**
   * Get the Java package name associated with the Module module.
   *
   * @return the package name
   */
  @NonNull
  String getPackageName();

  /**
   * Get the collection of generated classes representing definitions associated
   * with the Module module.
   *
   * @return the collection of definition classes
   */
  @NonNull
  Collection<IGeneratedDefinitionClass> getGeneratedDefinitionClasses();

  /**
   * Dynamicly load this class.
   *
   * @param classLoader
   *          the class loader to use to load this class
   * @return the module class
   * @throws ClassNotFoundException
   *           if this classwas not found
   * @since 2.0.0
   */
  @SuppressWarnings("unchecked")
  @NonNull
  default Class<? extends IBoundModule> load(@NonNull ClassLoader classLoader) throws ClassNotFoundException {
    return ObjectUtils.notNull((Class<? extends IBoundModule>) classLoader.loadClass(getClassName().reflectionName()));
  }
}
