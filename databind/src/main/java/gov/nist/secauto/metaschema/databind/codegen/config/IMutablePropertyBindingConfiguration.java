/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen.config;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A mutable extension of {@link IPropertyBindingConfiguration} that allows
 * setting property binding configuration values.
 */
public interface IMutablePropertyBindingConfiguration extends IPropertyBindingConfiguration {

  /**
   * Set the fully qualified class name to use for collection initialization.
   *
   * @param className
   *          the fully qualified class name
   */
  void setCollectionClassName(@NonNull String className);
}
