/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.ModelInitializationException;

/**
 * Identifies that an unexpected error occurred while initializing or using
 * constraints associated with a Metaschema-based model.
 */
public class ConstraintInitializationException
    extends ModelInitializationException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the provided {@code message} and no cause.
   *
   * @param message
   *          the exception message
   */
  public ConstraintInitializationException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the provided {@code cause}.
   * <p>
   * The message used will be the message provided by the underlying cause.
   *
   * @param cause
   *          the original exception cause
   */
  public ConstraintInitializationException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new exception with the provided {@code message} and
   * {@code cause}.
   *
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public ConstraintInitializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
