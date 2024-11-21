/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public interface IFlagInstance extends IFlag, IValuedInstance, IInstanceAbsolute {

  boolean DEFAULT_FLAG_REQUIRED = false;

  @Override
  IModelDefinition getParentContainer();

  @Override
  IFlagDefinition getDefinition();

  @Override
  default IModelDefinition getContainingDefinition() {
    return getParentContainer();
  }

  /**
   * Determines if a flag value is required to be provided.
   *
   * @return {@code true} if a value is required, or {@code false} otherwise
   * @see #DEFAULT_FLAG_REQUIRED
   */
  default boolean isRequired() {
    return DEFAULT_FLAG_REQUIRED;
  }
}
