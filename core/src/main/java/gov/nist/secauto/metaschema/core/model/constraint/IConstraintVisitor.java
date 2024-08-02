/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 *
 * Supports a visitor pattern over constraint instances.
 *
 * @param <T>
 *          the Java type of a state object passed to the visitor
 * @param <R>
 *          the Java type of the result returned by the visitor methods
 */
public interface IConstraintVisitor<T, R> {

  /**
   * Implementation of this method support visitation of an
   * {@link IAllowedValuesConstraint}.
   *
   * @param constraint
   *          the constraint to visit
   * @param state
   *          a state object passed to the visitor
   * @return the visitation result
   */
  R visitAllowedValues(@NonNull IAllowedValuesConstraint constraint, T state);

  /**
   * Implementation of this method support visitation of an
   * {@link ICardinalityConstraint}.
   *
   * @param constraint
   *          the constraint to visit
   * @param state
   *          a state object passed to the visitor
   * @return the visitation result
   */
  R visitCardinalityConstraint(@NonNull ICardinalityConstraint constraint, T state);

  /**
   * Implementation of this method support visitation of an
   * {@link IExpectConstraint}.
   *
   * @param constraint
   *          the constraint to visit
   * @param state
   *          a state object passed to the visitor
   * @return the visitation result
   */
  R visitExpectConstraint(@NonNull IExpectConstraint constraint, T state);

  /**
   * Implementation of this method support visitation of an
   * {@link IMatchesConstraint}.
   *
   * @param constraint
   *          the constraint to visit
   * @param state
   *          a state object passed to the visitor
   * @return the visitation result
   */
  R visitMatchesConstraint(@NonNull IMatchesConstraint constraint, T state);

  /**
   * Implementation of this method support visitation of an
   * {@link IIndexConstraint}.
   *
   * @param constraint
   *          the constraint to visit
   * @param state
   *          a state object passed to the visitor
   * @return the visitation result
   */
  R visitIndexConstraint(@NonNull IIndexConstraint constraint, T state);

  /**
   * Implementation of this method support visitation of an
   * {@link IIndexHasKeyConstraint}.
   *
   * @param constraint
   *          the constraint to visit
   * @param state
   *          a state object passed to the visitor
   * @return the visitation result
   */
  R visitIndexHasKeyConstraint(@NonNull IIndexHasKeyConstraint constraint, T state);

  /**
   * Implementation of this method support visitation of an
   * {@link IUniqueConstraint}.
   *
   * @param constraint
   *          the constraint to visit
   * @param state
   *          a state object passed to the visitor
   * @return the visitation result
   */
  R visitUniqueConstraint(@NonNull IUniqueConstraint constraint, T state);
}
