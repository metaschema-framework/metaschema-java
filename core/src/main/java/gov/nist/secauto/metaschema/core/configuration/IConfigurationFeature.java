/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.configuration;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The common interface that all configuration features must implement.
 * <p>
 * This approach is inspired by the configuration implementation in the
 * <a href="https://github.com/FasterXML/jackson-databind">Jackson databind
 * library</a>.
 *
 * @param <V>
 *          the value type of the feature
 */
public interface IConfigurationFeature<V> {
  /**
   * Get the name of the configuration feature.
   *
   * @return the name
   */
  @NonNull
  String getName();

  /**
   * Get the default value of the configuration feature.
   *
   * @return the default value
   */
  @NonNull
  V getDefault();

  /**
   * Get the class of the feature's value.
   *
   * @return the value's class
   */
  @NonNull
  Class<V> getValueClass();
}
