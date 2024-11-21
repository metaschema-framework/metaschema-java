/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import gov.nist.secauto.metaschema.core.model.ISource;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides an base implementation for a set of constraints that target a
 * definition using a target Metapath expression.
 *
 * @param <T>
 *          the Java type of the constraint container
 */
public abstract class AbstractTargetedConstraints<T extends IValueConstrained>
    implements ITargetedConstraints, IFeatureValueConstrained {
  @NonNull
  private final ISource source;
  @NonNull
  private final String targetExpression;
  @NonNull
  private final T constraints;

  /**
   * Construct a new set of targeted constraints.
   *
   * @param source
   *          information about the resource the constraints were sources from
   * @param target
   *          the Metapath expression that can be used to find matching targets
   * @param constraints
   *          the constraints to apply to matching targets
   */
  protected AbstractTargetedConstraints(
      @NonNull ISource source,
      @NonNull String target,
      @NonNull T constraints) {
    this.source = source;
    this.targetExpression = target;
    this.constraints = constraints;
  }

  @Override
  public ISource getSource() {
    return source;
  }

  @Override
  public String getTargetExpression() {
    return targetExpression;
  }

  @Override
  public T getConstraintSupport() {
    return constraints;
  }
}
