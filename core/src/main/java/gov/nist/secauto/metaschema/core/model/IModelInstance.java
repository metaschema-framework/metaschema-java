/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

/**
 * This marker interface is used to identify a field or assembly instance that
 * is a member of an assembly's model.
 */
public interface IModelInstance extends IGroupable {
  @Override
  IContainerModel getParentContainer();

  @Override
  IAssemblyDefinition getContainingDefinition();

  /**
   * Indicate if the instance allows values without an XML element wrapper.
   *
   * @return {@code true} if the underlying data type is allowed to be unwrapped,
   *         or {@code false} otherwise
   */
  boolean isEffectiveValueWrappedInXml();
}
