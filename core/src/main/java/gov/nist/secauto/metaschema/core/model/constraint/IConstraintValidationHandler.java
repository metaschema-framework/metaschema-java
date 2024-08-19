/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.List;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IConstraintValidationHandler {

  /**
   * Handle a cardinality constraint minimum violation.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param targets
   *          the targets of evaluation
   */
  void handleCardinalityMinimumViolation(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem node,
      @NonNull ISequence<? extends INodeItem> targets);

  /**
   * Handle a cardinality constraint maximum violation.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param targets
   *          the targets of evaluation
   */
  void handleCardinalityMaximumViolation(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem node,
      @NonNull ISequence<? extends INodeItem> targets);

  /**
   * Handle a duplicate index violation.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   */
  void handleIndexDuplicateViolation(
      @NonNull IIndexConstraint constraint,
      @NonNull INodeItem node);

  /**
   * Handle an index duplicate key violation.
   * <p>
   * This happens when two target nodes have the same key.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param oldItem
   *          the node that exists in the index for the related key
   * @param target
   *          the target of evaluation
   */
  void handleIndexDuplicateKeyViolation(
      @NonNull IIndexConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target);

  /**
   * Handle an unique key violation.
   * <p>
   * This happens when two target nodes have the same key.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param oldItem
   *          the other node with the same key
   * @param target
   *          the target of evaluation
   */
  void handleUniqueKeyViolation(
      @NonNull IUniqueConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target);

  /**
   * Handle an error that occurred while generating a key.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param target
   *          the target of evaluation
   * @param exception
   *          the resulting Metapath exception
   */
  void handleKeyMatchError(
      @NonNull IKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull MetapathException exception);

  /**
   * Handle a missing index violation.
   * <p>
   * This happens when an index-has-key constraint references a missing index.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param target
   *          the target of evaluation
   * @param message
   *          the error message
   */
  void handleMissingIndexViolation(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String message);

  /**
   * Handle an index lookup key miss violation.
   * <p>
   * This happens when another node references an expected member of an index that
   * does not actually exist in the index.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param target
   *          the target of evaluation
   * @param key
   *          the key that was used to lookup the index entry
   */
  void handleIndexMiss(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull List<String> key);

  /**
   * Handle a match pattern violation.
   * <p>
   * This happens when the target value does not match the specified pattern.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param target
   *          the target of evaluation
   * @param value
   *          the value used for pattern matching
   * @param pattern
   *          the pattern used for pattern matching
   */
  void handleMatchPatternViolation(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull Pattern pattern);

  /**
   * Handle a match data type violation.
   * <p>
   * This happens when the target value does not conform to the specified data
   * type.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param target
   *          the target of evaluation
   * @param value
   *          the value used for data type matching
   * @param adapter
   *          the data type used for data type matching
   * @param cause
   *          the data type exception related to this violation
   */
  void handleMatchDatatypeViolation(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull IDataTypeAdapter<?> adapter,
      @NonNull IllegalArgumentException cause);

  /**
   * Handle an expect test violation.
   * <p>
   * This happens when the test does not evaluate to true.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param target
   *          the target of evaluation
   * @param metapathContext
   *          the Metapath evaluation context
   */
  void handleExpectViolation(
      @NonNull IExpectConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull DynamicContext metapathContext);

  /**
   * Handle an allowed values constraint violation.
   *
   * @param failedConstraints
   *          the allowed values constraints that did not match.
   * @param target
   *          the target of evaluation
   */
  void handleAllowedValuesViolation(
      @NonNull List<IAllowedValuesConstraint> failedConstraints,
      @NonNull INodeItem target);

  /**
   * Handle a constraint that has passed validation.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param target
   *          the target of evaluation
   */
  void handlePass(
      @NonNull IConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target);

  /**
   * Handle a constraint that whose evaluation resulted in an unexpected error
   * during validation.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param message
   *          the error message
   * @param exception
   *          the causing exception
   */
  void handleError(
      @NonNull IConstraint constraint,
      @NonNull INodeItem node,
      @NonNull String message,
      @NonNull Throwable exception);
}
