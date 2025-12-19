/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.command.impl.DefaultExtraArgument;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A representation of an extra, non-option command line argument.
 */
public interface ExtraArgument {
  /**
   * Create a new extra argument instance.
   *
   * @param name
   *          the argument name
   * @param required
   *          {@code true} if the argument is required, or {@code false} otherwise
   * @return the instance
   */
  @NonNull
  static ExtraArgument newInstance(@NonNull String name, boolean required) {
    if (name.isBlank()) {
      throw new IllegalArgumentException("name cannot be empty or blank");
    }
    return new DefaultExtraArgument(name, required);
  }

  /**
   * Create a new extra argument instance with type information for shell
   * completion.
   *
   * @param name
   *          the argument name
   * @param required
   *          {@code true} if the argument is required, or {@code false} otherwise
   * @param type
   *          the type class for completion lookup, or {@code null} for freeform
   *          input
   * @return the instance
   */
  @NonNull
  static ExtraArgument newInstance(@NonNull String name, boolean required, @Nullable Class<?> type) {
    if (name.isBlank()) {
      throw new IllegalArgumentException("name cannot be empty or blank");
    }
    return new DefaultExtraArgument(name, required, type);
  }

  /**
   * Get the argument name.
   *
   * @return the name
   */
  String getName();

  /**
   * Get if the argument is required.
   *
   * @return {@code true} if the argument is required, or {@code false} otherwise
   */
  boolean isRequired();

  /**
   * Get the allow number of arguments of this type.
   *
   * @return the allowed number of arguments as a positive number or {@code -1}
   *         for unlimited
   */
  default int getNumber() {
    return 1;
  }

  /**
   * Get the type for shell completion purposes.
   * <p>
   * The type is used by
   * {@link gov.nist.secauto.metaschema.cli.processor.completion.CompletionTypeRegistry}
   * to lookup the appropriate completion behavior for this argument.
   *
   * @return the type class used to determine shell completion behavior, or
   *         {@code null} for freeform input
   */
  @Nullable
  default Class<?> getType() {
    return null;
  }
}
