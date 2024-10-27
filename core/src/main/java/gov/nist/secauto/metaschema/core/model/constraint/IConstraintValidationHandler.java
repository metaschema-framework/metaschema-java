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

/**
 * Provides a set of callback methods used to process the result of evaluating a
 * constraint.
 */
public interface IConstraintValidationHandler {

  /**
   * Handle a cardinality constraint minimum violation.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param target
   *          the node used as the evaluation focus to determine the items to test
   * @param testedItems
   *          the items tested
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleCardinalityMinimumViolation(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem target,
      @NonNull ISequence<? extends INodeItem> testedItems,
      @NonNull DynamicContext dynamicContext);

  /**
   * Handle a cardinality constraint maximum violation.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param target
   *          the node used as the evaluation focus to determine the items to test
   * @param testedItems
   *          the items tested
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleCardinalityMaximumViolation(
      @NonNull ICardinalityConstraint constraint,
      @NonNull INodeItem target,
      @NonNull ISequence<? extends INodeItem> testedItems,
      @NonNull DynamicContext dynamicContext);

  /**
   * Handle a duplicate index violation.
   *
   * @param constraint
   *          the constraint that was evaluated
   * @param node
   *          the node used as the evaluation focus to determine constraint
   *          targets
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleIndexDuplicateViolation(
      @NonNull IIndexConstraint constraint,
      @NonNull INodeItem node,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleIndexDuplicateKeyViolation(
      @NonNull IIndexConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleUniqueKeyViolation(
      @NonNull IUniqueConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem oldItem,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleKeyMatchError(
      @NonNull IKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull MetapathException exception,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleMissingIndexViolation(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String message,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleIndexMiss(
      @NonNull IIndexHasKeyConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull List<String> key,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleMatchPatternViolation(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull Pattern pattern,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleMatchDatatypeViolation(
      @NonNull IMatchesConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull String value,
      @NonNull IDataTypeAdapter<?> adapter,
      @NonNull IllegalArgumentException cause,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleExpectViolation(
      @NonNull IExpectConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext);

  /**
   * Handle an allowed values constraint violation.
   *
   * @param failedConstraints
   *          the allowed values constraints that did not match.
   * @param target
   *          the target of evaluation
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleAllowedValuesViolation(
      @NonNull List<IAllowedValuesConstraint> failedConstraints,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handlePass(
      @NonNull IConstraint constraint,
      @NonNull INodeItem node,
      @NonNull INodeItem target,
      @NonNull DynamicContext dynamicContext);

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
   * @param dynamicContext
   *          the Metapath dynamic execution context to use for Metapath
   *          evaluation
   */
  void handleError(
      @NonNull IConstraint constraint,
      @NonNull INodeItem node,
      @NonNull String message,
      @NonNull Throwable exception,
      @NonNull DynamicContext dynamicContext);
}
