/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.io;

/**
 * Used to report exceptional conditions related to processing bound objects.
 */
public class BindingException
    extends Exception {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;

  // public BindingException(String message, Throwable cause, boolean
  // enableSuppression, boolean writableStackTrace) {
  // super(message, cause, enableSuppression, writableStackTrace);
  // }

  /**
   * Construct a new binding exception with the provided detail message and cause.
   *
   * @param message
   *          the detail message
   * @param cause
   *          the cause of the exception
   */
  public BindingException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Construct a new binding exception with the provided detail message.
   *
   * @param message
   *          the detail message
   */
  public BindingException(String message) {
    super(message);
  }

  /**
   * Construct a new binding exception with the provided cause.
   *
   * @param cause
   *          the cause of the exception
   */
  public BindingException(Throwable cause) {
    super(cause);
  }

}
