/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

/**
 * <a href= "https://www.w3.org/TR/xpath-31/#ERRXPDY0002">err:MPDY0002</a>: It
 * is a <a href="https://www.w3.org/TR/xpath-31/#dt-dynamic-error">dynamic
 * error</a> if evaluation of an expression relies on some part of the
 * <a href="https://www.w3.org/TR/xpath-31/#dt-dynamic-context">dynamic
 * context</a> that is
 * <a href="https://www.w3.org/TR/xpath-datamodel-31/#dt-absent">absent</a>.
 */
public class ContextAbsentDynamicMetapathException
    extends DynamicMetapathException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the provided {@code message} and no cause.
   *
   * @param message
   *          the exception message
   */
  public ContextAbsentDynamicMetapathException(String message) {
    super(DYNAMIC_CONTEXT_ABSENT, message);
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
  public ContextAbsentDynamicMetapathException(String message, Throwable cause) {
    super(DYNAMIC_CONTEXT_ABSENT, message, cause);
  }

  /**
   * Constructs a new exception with the provided {@code cause} and no message.
   *
   * @param cause
   *          the original exception cause
   */
  public ContextAbsentDynamicMetapathException(Throwable cause) {
    super(DYNAMIC_CONTEXT_ABSENT, cause);
  }
}
