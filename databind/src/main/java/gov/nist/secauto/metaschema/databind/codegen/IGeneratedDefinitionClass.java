/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.model.IModelDefinition;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IGeneratedDefinitionClass extends IGeneratedClass {

  /**
   * Get the Module definition associated with this class.
   *
   * @return the definition
   */
  @NonNull
  IModelDefinition getDefinition();

  /**
   * Indicates if the class represents a root Module assembly which can be the
   * top-level element/property of an XML, JSON, or YAML instance.
   *
   * @return {@code true} if the class is a root assembly, or {@code false}
   *         otherwise
   */
  boolean isRootClass();
}
