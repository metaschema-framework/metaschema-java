/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

public class MetaschemaException
    extends Exception {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Create a new Metaschema exception with a provided message.
   *
   * @param message
   *          text describing the cause of the exception
   */
  public MetaschemaException(String message) {
    super(message);
  }

  /**
   * Create a new Metaschema exception based on the provided cause.
   *
   * @param cause
   *          the exception that caused this exception
   */
  public MetaschemaException(Throwable cause) {
    super(cause);
  }

  /**
   * Create a new Metaschema exception with a provided message based on the
   * provided cause.
   *
   * @param message
   *          text describing the cause of the exception
   * @param cause
   *          the exception that caused this exception
   */
  public MetaschemaException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Create a new Metaschema exception with a provided message based on the
   * provided cause.
   *
   *
   * @param message
   *          text describing the cause of the exception
   * @param cause
   *          the exception that caused this exception
   * @param enableSuppression
   *          whether or not suppression is enabled or disabled
   * @param writableStackTrace
   *          whether or not the stack trace should be writable
   */
  public MetaschemaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
