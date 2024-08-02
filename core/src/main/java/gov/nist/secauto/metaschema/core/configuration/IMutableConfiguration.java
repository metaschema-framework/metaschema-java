/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.configuration;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This interface provides methods for retrieving and updating the configuration
 * of processors and parsers in this library. This provides a mutable view of
 * the current configuration.
 *
 * @param <T>
 *          the type of the feature set
 */
public interface IMutableConfiguration<T extends IConfigurationFeature<?>>
    extends IConfiguration<T> {
  /**
   * Turn on the provided feature.
   *
   * @param feature
   *          the feature to turn on
   * @return the updated configuration
   * @throws UnsupportedOperationException
   *           if the feature is not a boolean valued feature
   * @see IConfigurationFeature#getValueClass()
   */
  @NonNull
  default IMutableConfiguration<T> enableFeature(@NonNull T feature) {
    return set(feature, true);
  }

  /**
   * Turn off the provided feature.
   *
   * @param feature
   *          the feature to turn off
   * @return the updated configuration
   * @throws UnsupportedOperationException
   *           if the feature is not a boolean valued feature
   * @see IConfigurationFeature#getValueClass()
   */
  @NonNull
  default IMutableConfiguration<T> disableFeature(@NonNull T feature) {
    return set(feature, false);
  }

  /**
   * Replace this configuration with the {@code other} configuration.
   *
   * @param other
   *          the new configuration
   * @return the updated configuration
   */
  @NonNull
  IMutableConfiguration<T> applyConfiguration(@NonNull IConfiguration<T> other);

  /**
   * Set the value of the provided {@code feature} to the provided value.
   *
   * @param feature
   *          the feature to set
   * @param value
   *          the value to set
   * @return the updated configuration
   * @throws UnsupportedOperationException
   *           if the provided feature value is not assignment compatible with the
   *           features value type
   * @see IConfigurationFeature#getValueClass()
   */
  @NonNull
  IMutableConfiguration<T> set(@NonNull T feature, @NonNull Object value);
}
