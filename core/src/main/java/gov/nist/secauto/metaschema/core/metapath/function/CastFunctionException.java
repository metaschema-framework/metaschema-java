/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.impl.CodedMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * FOCA: Exceptions related to type casting.
 */
public class CastFunctionException
    extends CodedMetapathException {
  @NonNull
  private static final String PREFIX = "FOCA";
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOCA0002">err:FOCA0002</a>:
   * Raised by fn:resolve-QName and fn:QName when a supplied value does not have
   * the lexical form of a QName or URI respectively; and when casting to decimal,
   * if the supplied value is NaN or Infinity.
   */
  public static final int INVALID_LEXICAL_VALUE = 2;

  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFOCA0003">err:FOCA0003</a>:
   * Raised when casting to xs:integer if the supplied value exceeds the
   * implementation-defined limits for the datatype.
   */
  public static final int INPUT_VALUE_TOO_LARGE = 3;

  /**
   * the serial version UUID.
   */
  private static final long serialVersionUID = 1L;

  @NonNull
  private final IAnyAtomicItem item;

  /**
   * Constructs a new exception with the provided {@code code}, {@code item}, and
   * no cause.
   *
   * @param code
   *          the error code value
   * @param item
   *          the item the exception applies to
   * @param message
   *          the exception message text
   */
  public CastFunctionException(int code, @NonNull IAnyAtomicItem item, String message) {
    super(PREFIX, code, message);
    this.item = item;
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code item}, and
   * {@code cause}.
   *
   * @param code
   *          the error code value
   * @param item
   *          the item the exception applies to
   * @param message
   *          the exception message text
   * @param cause
   *          the original exception cause
   */
  public CastFunctionException(int code, @NonNull IAnyAtomicItem item, String message, Throwable cause) {
    super(PREFIX, code, message, cause);
    this.item = item;
  }

  /**
   * Get the item associated with the exception.
   *
   * @return the associated item
   */
  @NonNull
  public IAnyAtomicItem getItem() {
    return item;
  }
}
