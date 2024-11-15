/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.cli.processor;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implementations provide details around the result of processing a set of
 * command line arguments.
 */
public interface ExitStatus {
  /**
   * Get the exit code information associated with this exit status.
   *
   * @return the exit code information
   */
  @NonNull
  ExitCode getExitCode();

  /**
   * Get a throwable that is associated with this exit status.
   *
   * @return the throwable or {@code null} if no throwable is associated
   */
  @Nullable
  Throwable getThrowable();

  /**
   * Process the exit status.
   *
   * @param showStackTrace
   *          include the stack trace for the throwable, if associated
   * @see #withThrowable(Throwable)
   */
  void generateMessage(boolean showStackTrace);

  /**
   * Associate a throwable with the exit status.
   *
   * @param throwable
   *          the throwable
   * @return this exit status
   */
  @NonNull
  ExitStatus withThrowable(@NonNull Throwable throwable);
}
