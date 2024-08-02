/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;

/**
 * Represents an error that occurred while performing mathematical operations.
 */
public class ArrayException
    extends AbstractCodedMetapathException {
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOAY0001">err:FOAY0001</a>:
   * This error is raised when the $length argument to array:subarray is negative.
   */
  public static final int INDEX_OUT_OF_BOUNDS = 1;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOAY0002">err:FOAY0002</a>:
   * This error is raised whenever numeric operations result in an overflow or
   * underflow.
   */
  public static final int NEGATIVE_ARRAY_LENGTH = 2;

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
  public ArrayException(int code, String message) {
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
  public ArrayException(int code, String message, Throwable cause) {
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
  public ArrayException(int code, Throwable cause) {
    super(code, cause);
  }

  @Override
  public String getCodePrefix() {
    return "FOAY";
  }

}
