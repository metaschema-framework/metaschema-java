/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.IErrorCode;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class UnidentifiedFunctionError
    extends FunctionMetapathError {
  @NonNull
  private static final IErrorCode ERROR_CODE = IErrorCode.of("FOER", 0);

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new Metapath exception with the provided {@code message} and
   * {@code cause}.
   *
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public UnidentifiedFunctionError(@Nullable String message, @Nullable Throwable cause) {
    super(ERROR_CODE, message, cause);
  }

  /**
   * Constructs a new Metapath exception with the provided {@code message} and no
   * cause.
   *
   * @param message
   *          the exception message
   */
  public UnidentifiedFunctionError(@Nullable String message) {
    super(ERROR_CODE, message);
  }

  /**
   * Constructs a new Metapath exception with a {@code null} message and the
   * provided {@code cause}.
   *
   * @param cause
   *          the original exception cause
   */
  public UnidentifiedFunctionError(@Nullable Throwable cause) {
    super(ERROR_CODE, cause);
  }
}
