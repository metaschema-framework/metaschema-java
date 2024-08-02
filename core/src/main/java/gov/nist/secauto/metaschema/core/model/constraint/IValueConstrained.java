/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a container of rules constraining the effective model of a
 * Metaschema field or flag data instance.
 */
public interface IValueConstrained extends IConstrained {
  /**
   * Get the collection of let expressions, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  Map<QName, ILet> getLetExpressions();

  /**
   * Get the collection of allowed value constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IAllowedValuesConstraint> getAllowedValuesConstraints();

  /**
   * Get the collection of matches constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IMatchesConstraint> getMatchesConstraints();

  /**
   * Get the collection of index key reference constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints();

  /**
   * Get the collection of expect constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IExpectConstraint> getExpectConstraints();

  /**
   * Add a new let expression.
   *
   * @param let
   *          the let statement to add
   * @return the original let with the same name or {@code null} if no let existed
   *         with the same name
   */
  ILet addLetExpression(@NonNull ILet let);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IAllowedValuesConstraint constraint);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IMatchesConstraint constraint);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IIndexHasKeyConstraint constraint);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IExpectConstraint constraint);
}
