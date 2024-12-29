/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A marker interface for a Metaschema model construct (i.e. assembly, field)
 * that can be constrained using Metaschema constraints.
 */
public interface IFeatureModelConstrained extends IModelConstrained, IFeatureValueConstrained {
  @Override
  IModelConstrained getConstraintSupport();

  @Override
  default List<? extends IIndexConstraint> getIndexConstraints() {
    return getConstraintSupport().getIndexConstraints();
  }

  @Override
  default List<? extends IUniqueConstraint> getUniqueConstraints() {
    return getConstraintSupport().getUniqueConstraints();
  }

  @Override
  default List<? extends ICardinalityConstraint> getHasCardinalityConstraints() {
    return getConstraintSupport().getHasCardinalityConstraints();
  }

  @Override
  default void addConstraint(@NonNull IIndexConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @Override
  default void addConstraint(@NonNull IUniqueConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }

  @Override
  default void addConstraint(@NonNull ICardinalityConstraint constraint) {
    getConstraintSupport().addConstraint(constraint);
  }
}
