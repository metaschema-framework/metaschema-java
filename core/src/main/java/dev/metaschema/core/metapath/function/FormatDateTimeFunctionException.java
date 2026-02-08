/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function;

import dev.metaschema.core.metapath.IErrorCode;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * FOFD: Exceptions related to errors in formatting date/time values.
 *
 * @see <a href="https://www.w3.org/TR/xpath-functions-31/#formatting-dates">
 *      XPath Functions 3.1 - Formatting Dates and Times</a>
 */
public class FormatDateTimeFunctionException
    extends FunctionMetapathError {
  @NonNull
  private static final String PREFIX = "FOFD";
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOFD1340">err:FOFD1340</a>:
   * Raised when the picture string supplied to a date/time formatting function
   * does not conform to the required syntax.
   */
  public static final int INVALID_PICTURE_STRING = 1340;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOFD1350">err:FOFD1350</a>:
   * Raised when a component specifier within a picture string refers to a
   * component that is not available in the value being formatted.
   */
  public static final int COMPONENT_NOT_AVAILABLE = 1350;

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
  public FormatDateTimeFunctionException(int code, String message) {
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
  public FormatDateTimeFunctionException(int code, String message, Throwable cause) {
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
  public FormatDateTimeFunctionException(int code, Throwable cause) {
    super(IErrorCode.of(PREFIX, code), cause);
  }
}
