/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.configuration;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a basic configuration management implementation that allows mutable
 * access to configuration state.
 *
 * @param <T>
 *          the type of managed features
 */
public class DefaultConfiguration<T extends IConfigurationFeature<?>>
    implements IMutableConfiguration<T> {
  @NonNull
  private Map<T, Object> featureValues;

  /**
   * Create a new configuration.
   *
   */
  public DefaultConfiguration() {
    this.featureValues = new HashMap<>();
  }

  /**
   * Create a new configuration based on the provided feature value map.
   *
   * @param featureValues
   *          the set of enabled features
   */
  public DefaultConfiguration(@NonNull Map<T, Object> featureValues) {
    this.featureValues = new HashMap<>(featureValues);
  }

  /**
   * Create a new configuration based on the provided configuration.
   *
   * @param original
   *          the original configuration
   */
  public DefaultConfiguration(@NonNull DefaultConfiguration<T> original) {
    this(original.getFeatureValues());
  }

  @Override
  public Map<T, Object> getFeatureValues() {
    return CollectionUtil.unmodifiableMap(featureValues);
  }

  private void ensureBooleanValue(@NonNull T feature) {
    Class<?> valueClass = feature.getValueClass();
    if (!Boolean.class.isAssignableFrom(valueClass)) {
      throw new UnsupportedOperationException(
          String.format("Feature value class '%s' is boolean valued.", valueClass.getName()));
    }
  }

  @Override
  public boolean isFeatureEnabled(@NonNull T feature) {
    ensureBooleanValue(feature);
    return get(feature);
  }

  @Override
  public IMutableConfiguration<T> enableFeature(@NonNull T feature) {
    ensureBooleanValue(feature);
    featureValues.put(feature, true);
    return this;
  }

  @Override
  public IMutableConfiguration<T> disableFeature(@NonNull T feature) {
    ensureBooleanValue(feature);
    featureValues.put(feature, false);
    return this;
  }

  @Override
  public IMutableConfiguration<T> applyConfiguration(@NonNull IConfiguration<T> original) {
    this.featureValues.putAll(original.getFeatureValues());
    return this;
  }

  @Override
  public IMutableConfiguration<T> set(T feature, Object value) {
    Class<?> featureValueClass = feature.getValueClass();
    Class<?> valueClass = value.getClass();
    if (!featureValueClass.isAssignableFrom(valueClass)) {
      throw new UnsupportedOperationException(
          String.format("Provided value of class '%s' is not assignment compatible with feature value class '%s'.",
              valueClass.getName(),
              featureValueClass.getName()));
    }
    featureValues.put(feature, value);
    return this;
  }
}
