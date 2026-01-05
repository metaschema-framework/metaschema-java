/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath;

import java.util.Deque;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Raised when a Metapath treat expression fails because the sequence does not
 * match the required type.
 * <p>
 * This corresponds to XPath 3.1 error XPDY0050.
 */
public class InvalidTreatTypeDynamicMetapathException
    extends DynamicMetapathException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new exception with the provided {@code evaluationStack} and
   * {@code message} and no cause.
   *
   * @param evaluationStack
   *          the evaluation stack recording the expressions being evaluated
   * @param message
   *          the exception message
   */
  public InvalidTreatTypeDynamicMetapathException(
      @NonNull Deque<IExpression> evaluationStack,
      @Nullable String message) {
    super(TREAT_DOES_NOT_MATCH_TYPE, message);
    registerEvaluationContext(evaluationStack);
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
  public InvalidTreatTypeDynamicMetapathException(
      @Nullable String message,
      @Nullable Throwable cause) {
    super(TREAT_DOES_NOT_MATCH_TYPE, message, cause);
  }

  /**
   * Constructs a new exception with the provided {@code cause} and no message.
   *
   * @param cause
   *          the original exception cause
   */
  public InvalidTreatTypeDynamicMetapathException(
      @Nullable Throwable cause) {
    super(TREAT_DOES_NOT_MATCH_TYPE, cause);
  }
}
