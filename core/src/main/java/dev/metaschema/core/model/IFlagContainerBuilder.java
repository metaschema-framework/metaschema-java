/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Builder for constructing flag container instances.
 * <p>
 * This interface provides a fluent API for building flag containers by adding
 * flag instances and then building the final container.
 *
 * @param <T>
 *          the Java type of flag instances
 */
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
