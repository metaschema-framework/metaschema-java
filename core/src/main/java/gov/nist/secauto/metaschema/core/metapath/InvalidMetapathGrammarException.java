/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

/**
 * An exception to be raised when a Metapath is not a valid instance of the
 * Metapath grammar.
 * <p>
 * This exception is associated with the
 * {@link StaticMetapathException#INVALID_PATH_GRAMMAR} error code.
 */
public class InvalidMetapathGrammarException
    extends StaticMetapathException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 2L;

  /**
   * Constructs a new exception with the provided {@code message} and
   * {@code cause}.
   *
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public InvalidMetapathGrammarException(String message, Throwable cause) {
    super(INVALID_PATH_GRAMMAR, message, cause);
  }

  /**
   * Constructs a new exception with the provided {@code message} and no cause.
   *
   * @param message
   *          the exception message
   */
  public InvalidMetapathGrammarException(String message) {
    super(INVALID_PATH_GRAMMAR, message);
  }

  /**
   * Constructs a new exception with no message and the provided {@code cause}.
   *
   * @param cause
   *          the original exception cause
   */
  public InvalidMetapathGrammarException(Throwable cause) {
    super(INVALID_PATH_GRAMMAR, cause);
  }
}
