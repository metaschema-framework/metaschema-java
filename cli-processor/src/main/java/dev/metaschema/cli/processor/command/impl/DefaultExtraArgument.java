/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.cli.processor.command.impl;

import dev.metaschema.cli.processor.command.ExtraArgument;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A default implementation of the {@link ExtraArgument} interface that
 * represents a named command-line argument which can be marked as required or
 * optional.
 * <p>
 * This implementation is used by the command processor to handle additional
 * arguments that are not covered by specific command options.
 */
public class DefaultExtraArgument implements ExtraArgument {
  private final String name;
  private final boolean required;
  @Nullable
  private final Class<?> type;

  /**
   * Construct a new instance.
   *
   * @param name
   *          the argument name
   * @param required
   *          {@code true} if the argument is required, or {@code false} otherwise
   */
  public DefaultExtraArgument(@NonNull String name, boolean required) {
    this(name, required, null);
  }

  /**
   * Construct a new instance with type information for shell completion.
   *
   * @param name
   *          the argument name
   * @param required
   *          {@code true} if the argument is required, or {@code false} otherwise
   * @param type
   *          the type class for completion lookup, or {@code null} for freeform
   *          input
   */
  public DefaultExtraArgument(@NonNull String name, boolean required, @Nullable Class<?> type) {
    this.name = name;
    this.required = required;
    this.type = type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isRequired() {
    return required;
  }

  @Override
  @Nullable
  public Class<?> getType() {
    return type;
  }
}
