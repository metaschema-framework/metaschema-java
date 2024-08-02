/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.schemagen;

import gov.nist.secauto.metaschema.core.configuration.AbstractConfigurationFeature;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Configuration options for schema generation.
 *
 * @param <V>
 *          the feature value type
 */
public final class SchemaGenerationFeature<V>
    extends AbstractConfigurationFeature<V> {

  /**
   * If enabled, definitions that are defined inline will be generated as inline
   * types. If disabled, definitions will always be generated as global types.
   */
  @NonNull
  public static final SchemaGenerationFeature<Boolean> INLINE_DEFINITIONS
      = new SchemaGenerationFeature<>("inline-definitions", Boolean.class, false);

  /**
   * If enabled, child definitions of a choice that are defined inline will be
   * generated as inline types. If disabled, child definitions of a choice will
   * always be generated as global types. This option will only be used if
   * {@link #INLINE_DEFINITIONS} is also enabled.
   */
  @NonNull
  public static final SchemaGenerationFeature<Boolean> INLINE_CHOICE_DEFINITIONS
      = new SchemaGenerationFeature<>("inline-choice-definitions", Boolean.class, false);

  private SchemaGenerationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }

}
