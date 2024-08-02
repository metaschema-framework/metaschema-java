/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class DefaultExtraArgument implements ExtraArgument {
  private final String name;
  private final boolean required;
  private final int number;

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public DefaultExtraArgument(String name, boolean required) {
    this(name, required, 1);
  }

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "Use of final fields")
  public DefaultExtraArgument(String name, boolean required, int number) {
    if (number < 1) {
      throw new IllegalArgumentException("number must be a positive value");
    }
    this.name = name;
    this.required = required;
    this.number = number;
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
  public int getNumber() {
    return number;
  }

}
