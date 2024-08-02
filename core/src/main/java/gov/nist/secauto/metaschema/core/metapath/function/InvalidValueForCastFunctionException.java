/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

public class InvalidValueForCastFunctionException
    extends InvalidArgumentFunctionException {

  /**
   * the serial version UUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the provided {@code message} and no cause.
   *
   * @param message
   *          the exception message
   */
  public InvalidValueForCastFunctionException(String message) {
    super(INVALID_VALUE_FOR_CAST, message);
  }

  /**
   * Constructs a new exception with the provided {@code message} and
   * {@code cause}.
   *
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public InvalidValueForCastFunctionException(String message, Throwable cause) {
    super(INVALID_VALUE_FOR_CAST, message, cause);
  }

  /**
   * Constructs a new exception with no message and the provided {@code cause}.
   *
   * @param cause
   *          the original exception cause
   */
  public InvalidValueForCastFunctionException(Throwable cause) {
    super(INVALID_VALUE_FOR_CAST, cause);
  }

}
