/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;

public class JsonFunctionException
    extends AbstractCodedMetapathException {
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOJS0003">err:FOJS0003</a>:
   * This error is raised if the input contains duplicate keys, when the chosen
   * policy is to reject duplicates.
   */
  public static final int DUPLICATE_KEYS = 3;

  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOJS0005">err:FOJS0005</a>:
   * This error is raised if the $options map contains an invalid entry.
   */
  public static final int INVALID_OPTION = 5;

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
  public JsonFunctionException(int code, String message) {
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
  public JsonFunctionException(int code, String message, Throwable cause) {
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
  public JsonFunctionException(int code, Throwable cause) {
    super(code, cause);
  }

  @Override
  public String getCodePrefix() {
    return "FOJS";
  }
}
