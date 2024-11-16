/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Records information about the exit status of a CLI command.
 * <p>
 * This abstract class provides base functionality for handling CLI command exit
 * statuses, including error logging and throwable management. Implementing
 * classes must provide the {@link #getMessage()} implementation to define the
 * status message content.
 */
public abstract class AbstractExitStatus implements ExitStatus {
  private static final Logger LOGGER = LogManager.getLogger(AbstractExitStatus.class);

  @NonNull
  private final ExitCode exitCode;

  private Throwable throwable;

  /**
   * Construct a new exit status based on the provided {@code exitCode}.
   *
   * @param exitCode
   *          the exit code
   */
  public AbstractExitStatus(@NonNull ExitCode exitCode) {
    this.exitCode = exitCode;
  }

  @Override
  public ExitCode getExitCode() {
    return exitCode;
  }

  /**
   * Get the associated throwable.
   *
   * @return the throwable or {@code null}
   */
  @Override
  public Throwable getThrowable() {
    return throwable;
  }

  @Override
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "intended as a exposed property")
  public ExitStatus withThrowable(@NonNull Throwable throwable) {
    this.throwable = throwable;
    return this;
  }

  /**
   * Get the associated message.
   *
   * @return the message or {@code null}
   */
  @Nullable
  protected abstract String getMessage();

  /**
   * Determines the appropriate LogBuilder based on the exit code status. For
   * non-positive exit codes (success/info), returns an INFO level builder. For
   * positive exit codes (errors), returns an ERROR level builder.
   *
   * @return the appropriate LogBuilder based on exit status, or {@code null} if
   *         logging is disabled at the determined level
   */
  @Nullable
  private LogBuilder getLogBuilder() {
    LogBuilder logBuilder = null;
    if (getExitCode().getStatusCode() <= 0) {
      if (LOGGER.isInfoEnabled()) {
        logBuilder = LOGGER.atInfo();
      }
    } else if (LOGGER.isErrorEnabled()) {
      logBuilder = LOGGER.atError();
    }
    return logBuilder;
  }

  /**
   * Generates and logs a message based on the current exit status. The message is
   * logged at either INFO level (for success/info status) or ERROR level (for
   * error status).
   *
   * @param showStackTrace
   *          if {@code true} and a throwable is present, includes the stack trace
   *          in the log
   */
  @Override
  public void generateMessage(boolean showStackTrace) {
    LogBuilder logBuilder = getLogBuilder();
    if (logBuilder == null) {
      return;
    }

    boolean useStackTrace = showStackTrace && throwable != null;
    if (useStackTrace) {
      logBuilder.withThrowable(throwable);
    }

    String message = getMessage();
    if (throwable != null && message == null) {
      message = throwable.getLocalizedMessage();
    }

    if (message != null && !message.isEmpty()) {
      logBuilder.log(message);
    } else if (useStackTrace) {
      // log the throwable
      logBuilder.log();
    } // else avoid an empty log line
  }

}
