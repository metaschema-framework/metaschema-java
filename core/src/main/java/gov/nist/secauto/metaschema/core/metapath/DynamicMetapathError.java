/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * This Metapath exception base class is used for all exceptions that have a
 * defined error code family and value.
 */
public class DynamicMetapathError
    extends MetapathException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The error prefix which identifies what kind of error it is.
   */
  @NonNull
  private final IErrorCode errorCode;

  /**
   * Constructs a new Metapath exception with the provided {@code code},
   * {@code message}, and no cause.
   *
   * @param errorCode
   *          the error code that identifies the type of error
   * @param message
   *          the exception message
   */
  public DynamicMetapathError(@NonNull IErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Constructs a new Metapath exception with the provided {@code code},
   * {@code message}, and {@code cause}.
   *
   * @param errorCode
   *          the error code that identifies the type of error
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  protected DynamicMetapathError(@NonNull IErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  /**
   * Constructs a new Metapath exception with a {@code null} message and the
   * provided {@code cause}.
   *
   * @param errorCode
   *          the error code that identifies the type of error
   * @param cause
   *          the original exception cause
   */
  public DynamicMetapathError(@NonNull IErrorCode errorCode, Throwable cause) {
    super(cause);
    this.errorCode = errorCode;
  }

  @Override
  public final String getMessage() {
    String message = getMessageText();
    return String.format("%s%s", getErrorCode().toString(), message == null ? "" : ": " + message);
  }

  /**
   * Get the message text without the error code prefix.
   *
   * @return the message text or {@code null}
   */
  @Nullable
  public final String getMessageText() {
    return super.getMessage();
  }

  /**
   * Get the error code, which indicates what type of error it is.
   *
   * @return the error code
   */
  public final IErrorCode getErrorCode() {
    return errorCode;
  }
}
