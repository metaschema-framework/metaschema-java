/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.metapath.impl.ErrorCodeImpl;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides an error code that identifies the type of message.
 */
public interface IErrorCode {
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  static IErrorCode of(@NonNull String prefix, int code) {
    return new ErrorCodeImpl(prefix, code);
  }

  /**
   * Get the error code prefix, which indicates what type of error it is.
   *
   * @return the error code prefix
   */
  @NonNull
  String getPrefix();

  /**
   * Get the error code value.
   *
   * @return the error code value
   */
  int getCode();

  /**
   * Get a combination of the error code family and value.
   *
   * @return the full error code.
   */
  @NonNull
  default String getCodeAsString() {
    return ObjectUtils.notNull(String.format("%s%04d", getPrefix(), getCode()));
  }
}
