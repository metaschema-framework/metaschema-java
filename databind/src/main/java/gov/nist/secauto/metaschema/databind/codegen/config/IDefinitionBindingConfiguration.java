/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public interface IDefinitionBindingConfiguration {
  /**
   * Get the class name to use for the generated class associated with this
   * binding.
   *
   * @return a class name
   */
  @Nullable
  String getClassName();

  /**
   * Get the class that the associated generated class will extend.
   *
   * @return a full type, including the package
   */
  @Nullable
  String getQualifiedBaseClassName();

  /**
   * A collection of interfaces that the associated generated class will
   * implement.
   *
   * @return a list of fully qualified type names for interfaces
   */
  @NonNull
  List<String> getInterfacesToImplement();
}
