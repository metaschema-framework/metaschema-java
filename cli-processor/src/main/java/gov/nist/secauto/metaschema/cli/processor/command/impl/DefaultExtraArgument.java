/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command.impl;

import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;

import edu.umd.cs.findbugs.annotations.NonNull;

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

  /**
   * Construct a new instance.
   *
   * @param name
   *          the argument name
   * @param required
   *          {@code true} if the argument is required, or {@code false} otherwise
   */
  public DefaultExtraArgument(@NonNull String name, boolean required) {
    this.name = name;
    this.required = required;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isRequired() {
    return required;
  }
}
