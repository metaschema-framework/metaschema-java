/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;
import gov.nist.secauto.metaschema.core.model.IModule;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
}
