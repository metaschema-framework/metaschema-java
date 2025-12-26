/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Default implementation of {@link IMutablePropertyBindingConfiguration}.
 */
public class DefaultPropertyBindingConfiguration implements IMutablePropertyBindingConfiguration {
  @Nullable
  private String collectionClassName;

  /**
   * Constructs a new empty property binding configuration.
   */
  public DefaultPropertyBindingConfiguration() {
    // empty constructor
  }

  /**
   * Constructs a new property binding configuration by copying values from an
   * existing configuration.
   *
   * @param config
   *          the configuration to copy from
   */
  public DefaultPropertyBindingConfiguration(@NonNull IPropertyBindingConfiguration config) {
    this.collectionClassName = config.getCollectionClassName();
  }

  @Override
  @Nullable
  public String getCollectionClassName() {
    return collectionClassName;
  }

  @Override
  public void setCollectionClassName(@NonNull String className) {
    this.collectionClassName = className;
  }
}
