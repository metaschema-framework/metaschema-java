/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

/**
 * {@code MetapathException} is the superclass of all exceptions that can be
 * thrown during the compilation and evaluation of a Metapath.
 */
public class MetapathException
    extends RuntimeException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new Metapath exception with a {@code null} message and no cause.
   */
  public MetapathException() {
    // no message
  }

  /**
   * Constructs a new Metapath exception with the provided {@code message} and no
   * cause.
   *
   * @param message
   *          the exception message
   */
  public MetapathException(String message) {
    super(message);
  }

  /**
   * Constructs a new Metapath exception with a {@code null} message and the
   * provided {@code cause}.
   *
   * @param cause
   *          the exception cause
   */
  public MetapathException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new Metapath exception with the provided {@code message} and
   * {@code cause}.
   *
   * @param message
   *          the exception message
   * @param cause
   *          the exception cause
   */
  public MetapathException(String message, Throwable cause) {
    super(message, cause);
  }
}
