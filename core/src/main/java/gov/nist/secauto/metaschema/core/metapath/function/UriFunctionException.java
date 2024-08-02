/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnResolveUri;

/**
 * FONS: Exceptions related to function namespaces.
 */
public class UriFunctionException
    extends AbstractCodedMetapathException {
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFONS0004">err:FONS0004</a>:
   * Raised by <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-resolve-QName">fn:resolve-QName</a>
   * and analogous functions if a supplied QName has a prefix that has no binding
   * to a namespace.
   */
  public static final int NO_NAMESPACE_FOUND_FOR_PREFIX = 4;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFONS0005">err:FONS0005</a>:
   * Raised by {@link FnResolveUri} if no base URI is available for resolving a
   * relative URI.
   */
  public static final int BASE_URI_NOT_DEFINED_IN_STATIC_CONTEXT = 5;

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 2L;

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and no cause.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   */
  public UriFunctionException(int code, String message) {
    super(code, message);
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and {@code cause}.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public UriFunctionException(int code, String message, Throwable cause) {
    super(code, message, cause);
  }

  /**
   * Constructs a new exception with the provided {@code code}, no message, and
   * the {@code cause}.
   *
   * @param code
   *          the error code value
   * @param cause
   *          the original exception cause
   */
  public UriFunctionException(int code, Throwable cause) {
    super(code, cause);
  }

  @Override
  public String getCodePrefix() {
    return "FONS";
  }

}
