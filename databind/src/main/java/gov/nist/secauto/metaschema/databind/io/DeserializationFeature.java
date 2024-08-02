/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.configuration.AbstractConfigurationFeature;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.DataClass") // not a data class
public final class DeserializationFeature<V>
    extends AbstractConfigurationFeature<V> {
  public static final int YAML_CODEPOINT_LIMIT_DEFAULT = Integer.MAX_VALUE - 1; // 2 GB
  public static final int FORMAT_DETECTION_LOOKAHEAD = 32_768; // 2 GB

  /**
   * If enabled, perform constraint validation on the deserialized bound objects.
   */
  @NonNull
  public static final DeserializationFeature<Boolean> DESERIALIZE_VALIDATE_CONSTRAINTS
      = new DeserializationFeature<>("validate", Boolean.class, false);

  /**
   * If enabled, perform constraint validation on the deserialized bound objects.
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

  private DeserializationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }
}
