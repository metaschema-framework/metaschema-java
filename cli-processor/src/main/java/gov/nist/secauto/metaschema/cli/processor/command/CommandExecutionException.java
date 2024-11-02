/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor.command;

import gov.nist.secauto.metaschema.cli.processor.ExitCode;
import gov.nist.secauto.metaschema.cli.processor.ExitStatus;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * For use in commands to short-circut command execution.
 */
public class CommandExecutionException
    extends Exception {
  private final ExitCode exitCode;

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the provided {@code code}, and no message or
   * cause.
   *
   * @param code
   *          the exit code associated with this error
   */
  public CommandExecutionException(@NonNull ExitCode code) {
    this.exitCode = code;
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and no cause.
   *
   * @param code
   *          the exit code associated with this error
   * @param message
   *          the exception message
   */
  public CommandExecutionException(@NonNull ExitCode code, String message) {
    super(message);
    this.exitCode = code;
  }

  /**
   * Constructs a new exception with no message and the provided {@code code}
   * and {@code cause}.
   *
   * @param code
   *          the exit code associated with this error
   * @param cause
   *          the original exception cause
   */
  public CommandExecutionException(@NonNull ExitCode code, Throwable cause) {
    super(cause);
    this.exitCode = code;
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and {@code cause}.
   *
   * @param code
   *          the exit code associated with this error
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public CommandExecutionException(@NonNull ExitCode code, String message, Throwable cause) {
    super(message, cause);
    this.exitCode = code;
  }

  /**
   * Generate an {@link ExitStatus} based on this exception.
   *
   * @return the exit status
   */
  @NonNull
  public ExitStatus toExitStatus() {
    String message = getLocalizedMessage();

    ExitStatus retval = message == null
        ? exitCode.exit()
        : exitCode.exitMessage(message);

    Throwable cause = getCause();
    if (cause != null) {
      retval.withThrowable(cause);
    }
    return retval;
  }
}
