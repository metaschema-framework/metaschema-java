/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.config;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides binding configuration for a specific property (field or assembly
 * instance) within a definition.
 *
 * <p>
 * Property bindings allow fine-grained control over code generation for
 * individual model instances, such as specifying custom collection
 * implementation classes.
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface IPropertyBindingConfiguration {

  /**
   * Get the fully qualified class name to use for collection initialization.
   *
   * <p>
   * When specified, this class will be used instead of the default
   * {@link java.util.LinkedList} or {@link java.util.LinkedHashMap} for
   * collection properties.
   *
   * @return the fully qualified class name, or {@code null} if the default
   *         collection class should be used
   */
  @Nullable
  String getCollectionClassName();
}
