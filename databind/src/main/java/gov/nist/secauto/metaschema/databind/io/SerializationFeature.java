/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

import gov.nist.secauto.metaschema.core.configuration.AbstractConfigurationFeature;

import edu.umd.cs.findbugs.annotations.NonNull;

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

  private SerializationFeature(
      @NonNull String name,
      @NonNull Class<V> valueClass,
      @NonNull V defaultValue) {
    super(name, valueClass, defaultValue);
  }

}
