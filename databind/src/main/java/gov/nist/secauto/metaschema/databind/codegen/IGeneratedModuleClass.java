/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.model.IModule;

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
}
