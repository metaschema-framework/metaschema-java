/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFlagContainerBuilder<T extends IFlagInstance> {
  /**
   * Add a flag instance to the flag container.
   *
   * @param instance
   *          the flag instance to add
   * @return this builder
   */
  @NonNull
  IFlagContainerBuilder<T> flag(@NonNull T instance);

  /**
   * Build the flag container.
   *
   * @return the built flag container
   */
  @NonNull
  IContainerFlagSupport<T> build();
}
