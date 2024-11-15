/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command.impl;

import gov.nist.secauto.metaschema.cli.processor.command.ExtraArgument;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This implementation of an extra argument.
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
