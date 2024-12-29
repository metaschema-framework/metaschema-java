/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

public class InvalidTreatTypeDynamicMetapathException
    extends DynamicMetapathException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the provided {@code message} and no cause.
   *
   * @param message
   *          the exception message
   */
  public InvalidTreatTypeDynamicMetapathException(String message) {
    super(TREAT_DOES_NOT_MATCH_TYPE, message);
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
  public InvalidTreatTypeDynamicMetapathException(String message, Throwable cause) {
    super(TREAT_DOES_NOT_MATCH_TYPE, message, cause);
  }

  /**
   * Constructs a new exception with the provided {@code cause} and no message.
   *
   * @param cause
   *          the original exception cause
   */
  public InvalidTreatTypeDynamicMetapathException(Throwable cause) {
    super(TREAT_DOES_NOT_MATCH_TYPE, cause);
  }
}
