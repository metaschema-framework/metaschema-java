/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.config;

import java.util.List;

import dev.metaschema.core.model.IModelDefinition;
import dev.metaschema.core.model.IModule;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides configuration for Java class binding generation from Metaschema
 * modules.
 * <p>
 * This interface defines how Metaschema module elements are mapped to Java
 * classes, including package names, class names, base classes, and
 * superinterfaces.
 */
public interface IBindingConfiguration {

  /**
   * Generates a Java package name for the provided Module module.
   *
   * @param module
   *          the Module module to generate a package name for
   * @return a Java package name
   */
  @NonNull
  String getPackageNameForModule(@NonNull IModule module);

  /**
   * Get the Java class name for the provided field or assembly definition.
   *
   * @param definition
   *          the definition to generate the Java class name for
   * @return a Java class name
   */
  @NonNull
  String getClassName(@NonNull IModelDefinition definition);

  /**
   * Get the Java class name for the provided Module module.
   *
   * @param module
   *          the Module module to generate the Java class name for
   * @return a Java class name
   */
  @NonNull
  String getClassName(@NonNull IModule module);

  /**
   * Get the Java class name of the base class to use for the class associated
   * with the provided definition.
   *
   * @param definition
   *          a definition that may be built as a class
   * @return the name of the base class or {@code null} if no base class is to be
   *         used
   */
  @Nullable
  String getQualifiedBaseClassName(@NonNull IModelDefinition definition);

  /**
   * Get the Java class names of the superinterfaces to use for the class
   * associated with the provided definition.
   *
   * @param definition
   *          a definition that may be built as a class
   * @return a list of superinterface class names
   */
  @NonNull
  List<String> getQualifiedSuperinterfaceClassNames(@NonNull IModelDefinition definition);

  /**
   * Retrieve the binding configuration for the provided definition.
   *
   * @param definition
   *          the definition to get the configuration for
   * @return the binding configuration, or {@code null} if there is no
   *         configuration for this definition
   */
  @Nullable
  IDefinitionBindingConfiguration getBindingConfigurationForDefinition(@NonNull IModelDefinition definition);
}
