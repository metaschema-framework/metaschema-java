/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen.config;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides binding configuration for a choice group within an assembly
 * definition.
 *
 * <p>
 * Choice group bindings enable fine-grained control over code generation for
 * choice groups, particularly for specifying custom collection types with
 * type-safe item bounds.
 */
public interface IChoiceGroupBindingConfiguration {

  /**
   * Get the name of the choice group to match.
   *
   * <p>
   * This name corresponds to the {@code group-as} name specified in the
   * Metaschema module for the choice group.
   *
   * @return the choice group name
   */
  @NonNull
  String getGroupAsName();

  /**
   * Get the fully qualified Java type name to use for collection items.
   *
   * <p>
   * When specified, the generated field and getter will use this type instead of
   * {@link Object} for the collection item type. This allows for type-safe
   * collections when all choice alternatives share a common supertype.
   *
   * @return the fully qualified Java type name, or {@code null} if not specified
   */
  @Nullable
  String getItemTypeName();

  /**
   * Determine whether to use a wildcard bounded type for the collection.
   *
   * <p>
   * When {@code true}, generates {@code List<? extends Type>} instead of
   * {@code List<Type>}. This provides additional flexibility when the exact item
   * type may vary while still maintaining type safety.
   *
   * <p>
   * Defaults to {@code true} if an item type is specified.
   *
   * @return {@code true} to use wildcard bounds, {@code false} otherwise
   */
  boolean isUseWildcard();
}
