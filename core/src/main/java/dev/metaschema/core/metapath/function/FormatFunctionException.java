/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function;

import dev.metaschema.core.metapath.IErrorCode;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * FODF: Exceptions related to formatting errors in Metapath functions such as
 * {@code format-integer}, {@code format-number}, {@code format-dateTime}, and
 * {@code format-date}.
 */
public class FormatFunctionException
    extends FunctionMetapathError {
  @NonNull
  private static final String PREFIX = "FODF";
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFODF1310">err:FODF1310</a>:
   * Raised when a format token in a picture string is invalid.
   */
  public static final int INVALID_FORMAT_TOKEN = 1310;

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and no cause.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   */
  public FormatFunctionException(int code, String message) {
    super(IErrorCode.of(PREFIX, code), message);
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and {@code cause}.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public FormatFunctionException(int code, String message, Throwable cause) {
    super(IErrorCode.of(PREFIX, code), message, cause);
  }

  /**
   * Constructs a new exception with the provided {@code code}, no message, and
   * the {@code cause}.
   *
   * @param code
   *          the error code value
   * @param cause
   *          the original exception cause
   */
  public FormatFunctionException(int code, Throwable cause) {
    super(IErrorCode.of(PREFIX, code), cause);
  }
}
