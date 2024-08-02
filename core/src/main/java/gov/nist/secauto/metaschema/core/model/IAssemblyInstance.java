/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public interface IAssemblyInstance extends IAssembly, INamedModelInstance {

  @Override
  IAssemblyDefinition getDefinition();

  @Override
  default boolean isEffectiveValueWrappedInXml() {
    // assembly instances are always wrapped
    return true;
  }
}
