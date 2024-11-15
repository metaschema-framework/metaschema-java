/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Thrown when an option argument is found to be invalid during parsing of a
 * command-line.
 */
public class InvalidArgumentException
    extends ParseException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /** The option that had the invalid argument. */
  private Option option;

  /**
   * Generate a new exception.
   *
   * @param message
   *          the message
   */
  public InvalidArgumentException(String message) {
    super(message);
  }

  /**
   * Return the option requiring an argument that wasn't provided on the command
   * line.
   *
   * @return the related option
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "intended to expose option for error handling")
  public Option getOption() {
    return option;
  }

  /**
   * Assign the option requiring an argument that wasn't provided on the command
   * line.
   *
   * @param option
   *          the option to set
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "intended to expose option for error handling")
  public void setOption(@NonNull Option option) {
    this.option = option;
  }
}
