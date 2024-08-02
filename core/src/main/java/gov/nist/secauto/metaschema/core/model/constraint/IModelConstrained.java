/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a container of rules constraining the effective model of a
 * Metaschema assembly data instance.
 */
public interface IModelConstrained extends IValueConstrained {
  /**
   * Get the collection of index constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IIndexConstraint> getIndexConstraints();

  /**
   * Get the collection of unique constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends IUniqueConstraint> getUniqueConstraints();

  /**
   * Get the collection of cardinality constraints, if any.
   *
   * @return the constraints or an empty list
   */
  @NonNull
  List<? extends ICardinalityConstraint> getHasCardinalityConstraints();

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IIndexConstraint constraint);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull IUniqueConstraint constraint);

  /**
   * Add a new constraint.
   *
   * @param constraint
   *          the constraint to add
   */
  void addConstraint(@NonNull ICardinalityConstraint constraint);
}
