/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.impl.CodedMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

public class UnidentifiedFunctionError
    extends CodedMetapathException {
  @NonNull
  private static final String PREFIX = "FOER";
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
  public UnidentifiedFunctionError(String message, Throwable cause) {
    super(PREFIX, 0, message, cause);
  }

  /**
   * Constructs a new Metapath exception with the provided {@code message} and no
   * cause.
   *
   * @param message
   *          the exception message
   */
  public UnidentifiedFunctionError(String message) {
    super(PREFIX, 0, message);
  }

  /**
   * Constructs a new Metapath exception with a {@code null} message and the
   * provided {@code cause}.
   *
   * @param cause
   *          the original exception cause
   */
  public UnidentifiedFunctionError(Throwable cause) {
    super(PREFIX, 0, cause);
  }
}
