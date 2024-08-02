/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;

/**
 * FORG: Exceptions related to argument types.
 */
public class InvalidArgumentFunctionException
    extends AbstractCodedMetapathException {

  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFORG0001">err:FORG0001</a>: A
   * general-purpose error raised when casting, if a cast between two datatypes is
   * allowed in principle, but the supplied value cannot be converted: for example
   * when attempting to cast the string "nine" to an integer.
   */
  public static final int INVALID_VALUE_FOR_CAST = 1;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFORG0002">err:FORG0002</a>:
   * Raised when either argument to fn:resolve-uri is not a valid URI/IRI.
   */
  public static final int INVALID_ARGUMENT_TO_RESOLVE_URI = 2;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFORG0006">err:FORG0006</a>:
   * Raised by functions such as fn:max, fn:min, fn:avg, fn:sum if the supplied
   * sequence contains values inappropriate to this function.
   */
  public static final int INVALID_ARGUMENT_TYPE = 6;

  /**
   * the serial version UUID.
   */
  private static final long serialVersionUID = 1L;

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
  public InvalidArgumentFunctionException(int code, String message, Throwable cause) {
    super(code, message, cause);
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and no cause.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   */
  public InvalidArgumentFunctionException(int code, String message) {
    super(code, message);
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
  public InvalidArgumentFunctionException(int code, Throwable cause) {
    super(code, cause);
  }

  @Override
  public String getCodePrefix() {
    return "FORG";
  }
}
