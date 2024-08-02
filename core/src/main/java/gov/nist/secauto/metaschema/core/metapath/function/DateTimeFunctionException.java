/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;

/**
 * FODT: Exceptions related to Date/Time/Duration errors.
 */
public class DateTimeFunctionException
    extends AbstractCodedMetapathException {
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFODT0001">err:FODT0001</a>:
   * Raised when casting to date/time datatypes, or performing arithmetic with
   * date/time values, if arithmetic overflow or underflow occurs.
   */
  public static final int DATE_TIME_OVERFLOW_UNDERFLOW_ERROR = 1;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFODT0002">err:FODT0002</a>:
   * Raised when casting to duration datatypes, or performing arithmetic with
   * duration values, if arithmetic overflow or underflow occurs.
   */
  public static final int DURATION_OVERFLOW_UNDERFLOW_ERROR = 2;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFODT0003">err:FODT0003</a>:
   * Raised by adjust-date-to-timezone and related functions if the supplied
   * timezone is invalid.
   */
  public static final int INVALID_TIME_ZONE_VALUE_ERROR = 3;

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 2L;

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and no cause.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   */
  public DateTimeFunctionException(int code, String message) {
    super(code, message);
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
  public DateTimeFunctionException(int code, String message, Throwable cause) {
    super(code, message, cause);
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
  public DateTimeFunctionException(int code, Throwable cause) {
    super(code, cause);
  }

  @Override
  public String getCodePrefix() {
    return "FODT";
  }

}
