/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnDoc;

/**
 * FODC: Exceptions representing document related errors.
 */
public class DocumentFunctionException
    extends AbstractCodedMetapathException {
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFODC0002">err:FODC0002</a>:
   * Raised by {@link FnDoc}, fn:collection, and fn:uri-collection to indicate
   * that either the supplied URI cannot be dereferenced to obtain a resource, or
   * the resource that is returned is not parseable as XML.
   */
  public static final int ERROR_RETRIEVING_RESOURCE = 2;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFODC0003">err:FODC0003</a>:
   * Raised by {@link FnDoc}, fn:collection, and fn:uri-collection to indicate
   * that it is not possible to return a result that is guaranteed deterministic.
   */
  public static final int FUNCTION_NOT_DEFINED_AS_DETERMINISTIC = 3;
  /**
   * <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#ERRFODC0002">err:FODC0002</a>:
   * Raised (optionally) by {@link FnDoc} and fn:doc-available if the argument is
   * not a valid URI reference.
   */
  public static final int INVALID_ARGUMENT = 5;

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
  public DocumentFunctionException(int code, String message) {
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
  public DocumentFunctionException(int code, String message, Throwable cause) {
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
  public DocumentFunctionException(int code, Throwable cause) {
    super(code, cause);
  }

  @Override
  public String getCodePrefix() {
    return "FODC";
  }

}
