/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

/**
 * MPDY: Exceptions related to the Metapath dynamic context and dynamic
 * evaluation.
 */
public class DynamicMetapathException
    extends AbstractCodedMetapathException {

  /**
   * the serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * <a href= "https://www.w3.org/TR/xpath-31/#ERRXPDY0002">err:MPDY0002</a>: It
   * is a <a href="https://www.w3.org/TR/xpath-31/#dt-dynamic-error">dynamic
   * error</a> if evaluation of an expression relies on some part of the
   * <a href="https://www.w3.org/TR/xpath-31/#dt-dynamic-context">dynamic
   * context</a> that is
   * <a href="https://www.w3.org/TR/xpath-datamodel-31/#dt-absent">absent</a>.
   */
  public static final int DYNAMIC_CONTEXT_ABSENT = 2;

  /**
   * <a href= "https://www.w3.org/TR/xpath-31/#ERRXPDY0050">err:MPDY0050</a>: It
   * is a <a href="https://www.w3.org/TR/xpath-31/#dt-dynamic-error">dynamic
   * error</a> if the
   * <a href="https://www.w3.org/TR/xpath-31/#dt-dynamic-type">dynamic type</a> of
   * the operand of a <code>treat</code> expression does not match the
   * <a href="https://www.w3.org/TR/xpath-31/#dt-sequence-type">sequence type</a>
   * specified by the <code>treat</code> expression. This error might also be
   * raised by a path expression beginning with "/" or "//" if the context node is
   * not in a tree that is rooted at a document node. This is because a leading
   * "/" or "//" in a path expression is an abbreviation for an initial step that
   * includes the clause <code>treat as document-node()</code>.
   */
  public static final int TREAT_DOES_NOT_MATCH_TYPE = 50;

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and no cause.
   *
   * @param code
   *          the error code value
   * @param message
   *          the exception message
   */
  public DynamicMetapathException(int code, String message) {
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
  public DynamicMetapathException(int code, String message, Throwable cause) {
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
  public DynamicMetapathException(int code, Throwable cause) {
    super(code, cause);
  }

  @Override
  public String getCodePrefix() {
    return "MPDY";
  }
}
