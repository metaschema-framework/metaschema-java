/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.io;

import dev.metaschema.core.configuration.AbstractConfigurationFeature;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Configuration features that control the serialization behavior of
 * Metaschema-bound object writers.
 * <p>
 * Each feature has a default value that can be overridden when configuring a
 * serializer.
 *
 * @param <V>
 *          the value type of the configuration feature
 */
public final class SerializationFeature<V>
    extends AbstractConfigurationFeature<V> {
  /**
   * If enabled, generate document level constructs in the underlying data format.
   * In XML this would include XML declarations. In JSON or YAML, this would
   * include an outer object and field with the name associated with the root
   * node.
   */
  @NonNull
  public static final SerializationFeature<Boolean> SERIALIZE_ROOT
      = new SerializationFeature<>("serialize-root", Boolean.class, true);

  /**
   * Construct a new serialization feature.
   *
   * @param name
   *          the feature name used for identification
   * @param valueClass
   *          the class of the feature value type
   * @param defaultValue
   *          the default value for this feature
   */
  private SerializationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }
}
