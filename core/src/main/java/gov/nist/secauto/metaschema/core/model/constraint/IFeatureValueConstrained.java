/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IFeatureValueConstrained extends IValueConstrained {
  /**
   * Lazy initialize the instances for the constraints when the constraints are
   * first accessed.
   *
   * @return the constraints instance
   */
  @NonNull
  IValueConstrained getConstraintSupport();

  @Override
  default ILet addLetExpression(ILet let) {
    return getConstraintSupport().addLetExpression(let);
  }

  @Override
  default Map<QName, ILet> getLetExpressions() {
    return getConstraintSupport().getLetExpressions();
  }

  @Override
  default List<? extends IConstraint> getConstraints() {
    return getConstraintSupport().getConstraints();
  }

  @Override
  default List<? extends IAllowedValuesConstraint> getAllowedValuesConstraints() {
    return getConstraintSupport().getAllowedValuesConstraints();
  }

  @Override
  default List<? extends IMatchesConstraint> getMatchesConstraints() {
    return getConstraintSupport().getMatchesConstraints();
  }

  @Override
  default List<? extends IIndexHasKeyConstraint> getIndexHasKeyConstraints() {
    return getConstraintSupport().getIndexHasKeyConstraints();
  }

  @Override
  default List<? extends IExpectConstraint> getExpectConstraints() {
    return getConstraintSupport().getExpectConstraints();
  }

  @Override
  default void addConstraint(IAllowedValuesConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @Override
  default void addConstraint(IMatchesConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @Override
  default void addConstraint(IIndexHasKeyConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @Override
  default void addConstraint(@NonNull IExpectConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }
}
