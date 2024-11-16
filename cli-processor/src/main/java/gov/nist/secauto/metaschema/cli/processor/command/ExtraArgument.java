/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.command.impl.DefaultExtraArgument;

import edu.umd.cs.findbugs.annotations.NonNull;

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
}
