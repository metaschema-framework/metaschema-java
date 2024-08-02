/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import edu.umd.cs.findbugs.annotations.NonNull;

public enum ExitCode {
  /**
   * The command executed without issue.
   */
  OK(0),
  /**
   * The command executed properly, but the operation failed.
   */
  FAIL(1),
  /**
   * An error occurred while reading or writing.
   */
  IO_ERROR(2),
  /**
   * A command was requested by name that doesn't exist or required arguments are
   * missing.
   */
  INVALID_COMMAND(3),
  /**
   * The target argument was not found or invalid.
   */
  INVALID_TARGET(4),
  /**
   * Handled errors that occur during command execution.
   */
  PROCESSING_ERROR(5),
  /**
   * Unhandled errors that occur during command execution.
   */
  RUNTIME_ERROR(6),
  /**
   * The provided argument information for a command fails to match argument use
   * requirements.
   */
  INVALID_ARGUMENTS(7);

  private final int statusCode;

  ExitCode(int statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Get the related status code for use with {@link System#exit(int)}.
   *
   * @return the statusCode
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Exit without a message.
   *
   * @return the exit status
   */
  @NonNull
  public ExitStatus exit() {
    return new NonMessageExitStatus(this);
  }

  /**
   * Exit with the associated message.
   *
   * @return the exit status
   */
  @NonNull
  public ExitStatus exitMessage() {
    return new MessageExitStatus(this);
  }

  /**
   * Exit with the associated message and message arguments.
   *
   * @param messageArguments
   *          any message parameters
   *
   * @return the exit status
   */
  @NonNull
  public ExitStatus exitMessage(@NonNull Object... messageArguments) {
    return new MessageExitStatus(this, messageArguments);
  }
}
