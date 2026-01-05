/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model.constraint;

import dev.metaschema.core.configuration.AbstractConfigurationFeature;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A set of configurable features that adjust Metaschema constraint validation
 * behavior.
 *
 * @param <V>
 *          the Java type of the configuration value
 */
public final class ValidationFeature<V>
    extends AbstractConfigurationFeature<V> {
  /**
   * If enabled, generate findings for passing constraints.
   */
  @NonNull
  public static final ValidationFeature<Boolean> VALIDATE_GENERATE_PASS_FINDINGS
      = new ValidationFeature<>("include-pass-findings", Boolean.class, false);
  /**
   * If enabled, throw an exception when an error occurs.
   */
  @NonNull
  public static final ValidationFeature<Boolean> THROW_EXCEPTION_ON_ERROR
      = new ValidationFeature<>("throw-exception-on-error", Boolean.class, false);
  /**
   * The number of threads to use for parallel constraint validation.
   * <p>
   * A value of 1 (the default) means sequential validation. Values greater than 1
   * enable experimental parallel validation with the specified number of threads.
   * <p>
   * <b>Warning:</b> Parallel validation is an experimental feature. Results
   * should be verified against sequential validation.
   */
  @NonNull
  public static final ValidationFeature<Integer> PARALLEL_THREADS
      = new ValidationFeature<>("parallel-threads", Integer.class, 1);

  private ValidationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }
}
