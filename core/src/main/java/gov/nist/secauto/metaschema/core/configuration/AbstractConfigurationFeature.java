/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.configuration;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a complete, abstract implementation of a generalized feature.
 * Feature implementations can extend this class the implement configuration
 * sets for a given purpose.
 *
 * @param <V>
 *          the feature value Java type
 */
public abstract class AbstractConfigurationFeature<V> implements IConfigurationFeature<V> {
  @NonNull
  private final String name;
  @NonNull
  private final Class<V> valueClass;
  @NonNull
  private final V defaultValue;

  /**
   * Construct a new feature with a default value.
   *
   * @param name
   *          the name of the feature
   * @param valueClass
   *          the class of the feature's value
   * @param defaultValue
   *          the value's default
   */
  protected AbstractConfigurationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    this.name = name;
    this.valueClass = valueClass;
    this.defaultValue = defaultValue;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public V getDefault() {
    return defaultValue;
  }

  @Override
  public Class<V> getValueClass() {
    return valueClass;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append(getName())
        .append('(')
        .append(getDefault().toString())
        .append(')')
        .toString();
  }
}
