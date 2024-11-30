/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.IErrorCode;

import edu.umd.cs.findbugs.annotations.NonNull;

public class ErrorCodeImpl implements IErrorCode {
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
   * Construct a new error code.
   *
   * @param prefix
   *          the error code prefix, which indicates what type of error it is
   * @param code
   *          the error code value, which indicates the specific error
   */
  public ErrorCodeImpl(@NonNull String prefix, int code) {
    this.prefix = prefix;
    this.code = code;
  }

  /**
   * Get the error code prefix, which indicates what type of error it is.
   *
   * @return the error code prefix
   */
  @Override
  public String getPrefix() {
    return prefix;
  }

  /**
   * Get the error code value.
   *
   * @return the error code value
   */
  @Override
  public int getCode() {
    return code;
  }

  /**
   * Get a combination of the error code family and value.
   *
   * @return the full error code.
   */
  @Override
  public final String toString() {
    return getCodeAsString();
  }
}
