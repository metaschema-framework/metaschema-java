/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.IErrorCode;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

// TODO: remove this intermediate exception?
public class FunctionMetapathError
    extends MetapathException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new Metapath exception with the provided {@code code},
   * {@code message}, and no cause.
   *
   * @param errorCode
   *          the error code that identifies the type of error
   * @param message
   *          the exception message
   */
  public FunctionMetapathError(
      @NonNull IErrorCode errorCode,
      @Nullable String message) {
    super(errorCode, message, null);
  }

  /**
   * Constructs a new Metapath exception with the provided {@code code},
   * {@code message}, and {@code cause}.
   *
   * @param errorCode
   *          the error code that identifies the type of error
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public FunctionMetapathError(
      @NonNull IErrorCode errorCode,
      @Nullable String message,
      @Nullable Throwable cause) {
    super(errorCode, message, cause);
  }

  /**
   * Constructs a new Metapath exception with a {@code null} message and the
   * provided {@code cause}.
   *
   * @param errorCode
   *          the error code that identifies the type of error
   * @param cause
   *          the original exception cause
   */
  public FunctionMetapathError(
      @NonNull IErrorCode errorCode,
      @Nullable Throwable cause) {
    super(errorCode, null, cause);
  }
}
