/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An immutable representation of an {@link IExpression} that has two
 * sub-expression children.
 *
 * @param <L>
 *          the type of the left expression
 * @param <R>
 *          the type of the right expression
 */
public abstract class AbstractBinaryExpression<L extends IExpression, R extends IExpression>
    extends AbstractExpression {
  @NonNull
  private final L left;
  @NonNull
  private final R right;

  /**
   * Construct a new binary expression.
   *
   * @param left
   *          the first sub-expression to evaluate
   * @param right
   *          the second sub-expression to evaluate
   */
  public AbstractBinaryExpression(@NonNull L left, @NonNull R right) {
    this.left = Objects.requireNonNull(left);
    this.right = Objects.requireNonNull(right);
  }

  /**
   * Retrieve the first sub-expression.
   *
   * @return the first sub-expression
   */
  @NonNull
  public L getLeft() {
    return left;
  }

  /**
   * Retrieve the second sub-expression.
   *
   * @return the second sub-expression
   */
  @NonNull
  public R getRight() {
    return right;
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of(left, right);
  }
}
