/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

/**
 * This Metapath exception base class is used for all exceptions that have a
 * defined error code family and value.
 */
public abstract class AbstractCodedMetapathException
    extends MetapathException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The error code.
   */
  private final int code;

  /**
   * Constructs a new Metapath exception with the provided {@code code},
   * {@code message}, and no cause.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   */
  public AbstractCodedMetapathException(int code, String message) {
    super(message);
    this.code = code;
  }

  /**
   * Constructs a new Metapath exception with the provided {@code code},
   * {@code message}, and {@code cause}.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public AbstractCodedMetapathException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  /**
   * Constructs a new Metapath exception with a {@code null} message and the
   * provided {@code cause}.
   *
   * @param code
   *          the error code value
   * @param cause
   *          the original exception cause
   */
  public AbstractCodedMetapathException(int code, Throwable cause) {
    super(cause);
    this.code = code;
  }

  @Override
  public String getMessage() {
    return String.format("%s: %s", getCodeAsString(), super.getMessage());
  }

  /**
   * Get the error code value.
   *
   * @return the error code value
   */
  public int getCode() {
    return code;
  }

  /**
   * Get the error code family.
   *
   * @return the error code family
   */
  public abstract String getCodePrefix();

  /**
   * Get a combination of the error code family and value.
   *
   * @return the full error code.
   */
  protected String getCodeAsString() {
    return String.format("%s%04d", getCodePrefix(), getCode());
  }
}
