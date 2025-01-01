/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * FOTY: Exceptions related to type errors.
 */
public class CastFunctionException
    extends AbstractCodedMetapathException {
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
    super(code, message);
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
    super(code, message, cause);
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

  @Override
  public String getCodePrefix() {
    return "FOCA";
  }
}
