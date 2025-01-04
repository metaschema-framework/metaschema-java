/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.IExpression;

import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An immutable expression with a single sub-expression.
 */
public abstract class AbstractUnaryExpression
    extends AbstractExpression {
  @NonNull
  private final IExpression expr;

  /**
   * Construct a new unary expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param expr
   *          the single sub-expression
   */
  public AbstractUnaryExpression(@NonNull String text, @NonNull IExpression expr) {
    super(text);
    this.expr = Objects.requireNonNull(expr, "expr");
  }

  /**
   * Retrieve the single child sub-expression.
   *
   * @return the sub-expression
   */
  @NonNull
  public IExpression getChild() {
    return expr;
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of(expr);
  }
}
