/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.MetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * This Metapath exception base class is used for all exceptions that have a
 * defined error code family and value.
 */
public class CodedMetapathException
    extends MetapathException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The error prefix which identifies what kind of error it is.
   */
  @NonNull
  private final String prefix;

  /**
   * The error code.
   */
  private final int code;

  /**
   * Constructs a new Metapath exception with the provided {@code code},
   * {@code message}, and no cause.
   *
   * @param prefix
   *          the error code prefix
   * @param code
   *          the error code
   * @param message
   *          the exception message
   */
  public CodedMetapathException(@NonNull String prefix, int code, String message) {
    super(message);
    this.prefix = prefix;
    this.code = code;
  }

  /**
   * Constructs a new Metapath exception with the provided {@code code},
   * {@code message}, and {@code cause}.
   *
   * @param prefix
   *          the error code prefix
   * @param code
   *          the error code
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  protected CodedMetapathException(@NonNull String prefix, int code, String message, Throwable cause) {
    super(message, cause);
    this.prefix = prefix;
    this.code = code;
  }

  /**
   * Constructs a new Metapath exception with a {@code null} message and the
   * provided {@code cause}.
   *
   * @param prefix
   *          the error code prefix
   * @param code
   *          the error code
   * @param cause
   *          the original exception cause
   */
  public CodedMetapathException(@NonNull String prefix, int code, Throwable cause) {
    super(cause);
    this.prefix = prefix;
    this.code = code;
  }

  @Override
  public final String getMessage() {
    String message = getMessageText();
    return String.format("%s%s", getCodeAsString(), message == null ? "" : ": " + message);
  }

  /**
   * Get the message text without the error code prefix.
   *
   * @return the message text or {@code null}
   */
  @Nullable
  public final String getMessageText() {
    return super.getMessage();
  }

  /**
   * Get the error code prefix, which indicates what type of error it is.
   *
   * @return the error code prefix
   */
  public final String getPrefix() {
    return prefix;
  }

  /**
   * Get the error code value.
   *
   * @return the error code value
   */
  public final int getCode() {
    return code;
  }

  /**
   * Get a combination of the error code family and value.
   *
   * @return the full error code.
   */
  public final String getCodeAsString() {
    return String.format("%s%04d", getPrefix(), getCode());
  }
}
