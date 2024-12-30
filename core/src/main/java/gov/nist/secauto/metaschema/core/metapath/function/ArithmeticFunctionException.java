/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;

/**
 * Represents an error that occurred while performing mathematical operations.
 */
public class ArithmeticFunctionException
    extends AbstractCodedMetapathException {
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOAR0001">err:FOAR0001</a>:
   * This error is raised whenever an attempt is made to divide by zero.
   */
  public static final int DIVISION_BY_ZERO = 1;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOAR0002">err:FOAR0002</a>:
   * This error is raised whenever numeric operations result in an overflow or
   * underflow.
   */
  public static final int OVERFLOW_UNDERFLOW_ERROR = 2;

  /**
   * Error message associated with {@link #DIVISION_BY_ZERO}.
   */
  public static final String DIVISION_BY_ZERO_MESSAGE = "Division by zero";

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
  public ArithmeticFunctionException(int code, String message) {
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
  public ArithmeticFunctionException(int code, String message, Throwable cause) {
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
  public ArithmeticFunctionException(int code, Throwable cause) {
    super(code, cause);
  }

  @Override
  public String getCodePrefix() {
    return "FOAR";
  }

}
