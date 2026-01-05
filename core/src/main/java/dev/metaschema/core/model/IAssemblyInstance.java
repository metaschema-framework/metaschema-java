/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

/**
 * Represents an assembly instance within another assembly definition.
 * <p>
 * An assembly instance references an assembly definition and specifies how that
 * assembly is used within its containing assembly.
 */
public interface IAssemblyInstance extends IAssembly, INamedModelInstance {

  /**
   * Retrieves the assembly definition referenced by this instance.
   *
   * @return the assembly definition
   */
  @Override
  IAssemblyDefinition getDefinition();

  @Override
  default boolean isEffectiveValueWrappedInXml() {
    // assembly instances are always wrapped
    return true;
  }
}
