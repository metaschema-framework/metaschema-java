/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.configuration;

import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The base interface for getting the configuration of processors and parsers in
 * this library. This provides an immutable view of the current configuration.
 *
 * @param <T>
 *          the type of the feature set
 */
public interface IConfiguration<T extends IConfigurationFeature<?>> {
  /**
   * Determines if a specific feature is enabled.
   *
   * @param feature
   *          the feature to check for
   * @return {@code true} if the feature is enabled, or {@code false} otherwise
   * @throws UnsupportedOperationException
   *           if the feature is not a boolean valued feature
   * @see IConfigurationFeature#getValueClass()
   */
  boolean isFeatureEnabled(@NonNull T feature);

  /**
   * Get the configuration value of the provided {@code feature}.
   *
   * @param <V>
   *          the value type
   * @param feature
   *          the requested feature
   * @return the value of the feature
   */
  @SuppressWarnings("unchecked")
  @NonNull
  default <V> V get(@NonNull T feature) {
    V value = (V) getFeatureValues().get(feature);
    if (value == null) {
      value = (V) feature.getDefault();
    }
    return value;
  }

  /**
   * Get the mapping of each feature mapped to its value.
   *
   * @return the mapping
   */
  @NonNull
  Map<T, Object> getFeatureValues();
}
