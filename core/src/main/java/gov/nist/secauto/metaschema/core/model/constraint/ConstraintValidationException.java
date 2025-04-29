/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import edu.umd.cs.findbugs.annotations.Nullable;

public class ConstraintValidationException
    extends Exception {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 2L;

  /**
   * Constructs a new exception with the specified detail message. The cause is
   * not initialized, and may subsequently be initialized by a call to
   * {@link #initCause}.
   *
   * @param message
   *          the detail message, which is saved for later retrieval by the
   *          {@link #getMessage()} method.
   */
  public ConstraintValidationException(@Nullable String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified cause.
   * <p>
   * If the cause has a detail message (i.e.
   * {@code (cause==null ? null : cause.toString())} then the message provided by
   * the cause will be used.
   * <p>
   * This constructor is useful for when this exception is raised as a wrapper of
   * another exception.
   *
   * @param cause
   *          the cause (which is saved for later retrieval by the
   *          {@link #getCause()} method). A {@code null} value is permitted, and
   *          indicates that the cause is nonexistent or unknown.
   */
  public ConstraintValidationException(@Nullable Throwable cause) {
    super(cause);
  }

  public ConstraintValidationException(@Nullable String message, @Nullable Throwable cause) {
    super(message, cause);
  }
}
