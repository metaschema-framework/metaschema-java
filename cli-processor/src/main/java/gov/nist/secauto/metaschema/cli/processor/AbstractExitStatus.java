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

  @Override
  public void generateMessage(boolean showStackTrace) {
    LogBuilder logBuilder = null;
    if (getExitCode().getStatusCode() <= 0) {
      if (LOGGER.isInfoEnabled()) {
        logBuilder = LOGGER.atInfo();
      }
    } else if (LOGGER.isErrorEnabled()) {
      logBuilder = LOGGER.atError();
    }

    if (logBuilder != null) {
      if (showStackTrace && throwable != null) {
        Throwable throwable = getThrowable();
        logBuilder.withThrowable(throwable);
      }

      String message = getMessage();
      if (message == null && throwable != null) {
        message = throwable.getLocalizedMessage();
      }

      if (message != null && !message.isEmpty()) {
        logBuilder.log(message);
      } else if (showStackTrace && throwable != null) {
        // log the throwable
        logBuilder.log();
      }
    }
  }

}
