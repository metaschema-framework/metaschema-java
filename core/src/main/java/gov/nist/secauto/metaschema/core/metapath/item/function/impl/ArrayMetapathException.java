/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function.impl;

import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathError;
import gov.nist.secauto.metaschema.core.metapath.IErrorCode;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents an error that occurred while performing mathematical operations.
 */
public class ArrayMetapathException
    extends DynamicMetapathError {
  @NonNull
  private static final String PREFIX = "FOAY";
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOAY0001">err:FOAY0001</a>:
   * This error is raised when an integer used to select a member of an array is
   * outside the range of values for that array.
   */
  protected static final int INDEX_OUT_OF_BOUNDS = 1;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOAY0001">err:FOAY0001</a>:
   * This error is raised when the $length argument to array:subarray is negative.
   */
  public static final int NEGATIVE_ARRAY_LENGTH = 2;

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 2L;

  @NonNull
  private final IArrayItem<?> item;

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and no cause.
   *
   * @param code
   *          the error code value
   * @param item
   *          the array item involved
   * @param message
   *          the exception message
   */
  public ArrayMetapathException(int code, @NonNull IArrayItem<?> item, String message) {
    super(IErrorCode.of(PREFIX, code), message);
    this.item = item;
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and {@code cause}.
   *
   * @param code
   *          the error code value
   * @param item
   *          the array item involved
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public ArrayMetapathException(int code, @NonNull IArrayItem<?> item, String message, Throwable cause) {
    super(IErrorCode.of(PREFIX, code), message, cause);
    this.item = item;
  }

  /**
   * Constructs a new exception with the provided {@code code}, no message, and
   * the {@code cause}.
   *
   * @param code
   *          the error code value
   * @param item
   *          the array item involved
   * @param cause
   *          the original exception cause
   */
  public ArrayMetapathException(int code, @NonNull IArrayItem<?> item, Throwable cause) {
    super(IErrorCode.of(PREFIX, code), cause);
    this.item = item;
  }

  /**
   * Get the array item involved in the exception.
   *
   * @return the array item
   */
  public IArrayItem<?> getArrayItem() {
    return item;
  }
}
