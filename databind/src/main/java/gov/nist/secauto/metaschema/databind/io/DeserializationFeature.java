/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.configuration.AbstractConfigurationFeature;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Configuration features that control the deserialization behavior of
 * Metaschema-bound object readers.
 * <p>
 * Each feature has a default value that can be overridden when configuring a
 * deserializer.
 *
 * @param <V>
 *          the value type of the configuration feature
 */
@SuppressWarnings("PMD.DataClass") // not a data class
public final class DeserializationFeature<V>
    extends AbstractConfigurationFeature<V> {
  /**
   * The default maximum number of codepoints that can be read from a YAML
   * document.
   */
  public static final int YAML_CODEPOINT_LIMIT_DEFAULT = Integer.MAX_VALUE - 1; // 2 GB
  /**
   * The default number of bytes used for format detection lookahead.
   */
  public static final int FORMAT_DETECTION_LOOKAHEAD = 32_768; // 2 GB

  /**
   * If enabled, perform constraint validation on the deserialized bound objects.
   */
  @NonNull
  public static final DeserializationFeature<Boolean> DESERIALIZE_VALIDATE_CONSTRAINTS
      = new DeserializationFeature<>("validate", Boolean.class, false);

  /**
   * If enabled, allow inline XML entities to be automatically replaced.
   */
  @NonNull
  public static final DeserializationFeature<Boolean> DESERIALIZE_XML_ALLOW_ENTITY_RESOLUTION
      = new DeserializationFeature<>("allow-entity-resolution", Boolean.class, false);

  /**
   * If enabled, process the next JSON node as a field, whose name must match the
   * {@link IAssemblyDefinition#getRootJsonName()}. If not enabled, the next JSON
   * node is expected to be an object containing the data of the
   * {@link IAssemblyDefinition}.
   */
  @NonNull
  public static final DeserializationFeature<Boolean> DESERIALIZE_JSON_ROOT_PROPERTY
      = new DeserializationFeature<>("deserialize-root-property", Boolean.class, true);

  /**
   * Determines the max YAML codepoints that can be read.
   */
  @NonNull
  public static final DeserializationFeature<Integer> YAML_CODEPOINT_LIMIT
      = new DeserializationFeature<>("yaml-codepoint-limit", Integer.class, YAML_CODEPOINT_LIMIT_DEFAULT);

  /**
   * Determines how many bytes can be looked at to identify the format of a
   * document.
   */
  @NonNull
  public static final DeserializationFeature<Integer> FORMAT_DETECTION_LOOKAHEAD_LIMIT
      = new DeserializationFeature<>("format-detection-lookahead-limit", Integer.class, FORMAT_DETECTION_LOOKAHEAD);

  /**
   * If enabled, validate that required fields are present during deserialization.
   * When a required field is missing and has no default value, an error will be
   * thrown with a descriptive message.
   * <p>
   * Choice groups are handled correctly: if an instance belongs to a choice and
   * at least one sibling in that choice was provided, the instance is not
   * considered missing.
   * <p>
   * When using schema validation via CLI commands, this feature is automatically
   * disabled since the schema already validates required fields.
   */
  @NonNull
  public static final DeserializationFeature<Boolean> DESERIALIZE_VALIDATE_REQUIRED_FIELDS
      = new DeserializationFeature<>("validate-required-fields", Boolean.class, true);

  /**
   * Construct a new deserialization feature.
   *
   * @param name
   *          the feature name used for identification
   * @param valueClass
   *          the class of the feature value type
   * @param defaultValue
   *          the default value for this feature
   */
  private DeserializationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }
}
