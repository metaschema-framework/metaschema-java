/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnBoolean;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of
 * <a href="https://www.w3.org/TR/xpath-31/#doc-xpath31-IfExpr">If
 * expression</a> supporting conditional evaluation.
 */
@SuppressWarnings("PMD.ShortClassName")
public class If
    extends AbstractExpression {
  private final IExpression testExpression;
  private final IExpression thenExpression;
  private final IExpression elseExpression;

  /**
   * Construct a new conditional expression.
   *
   * @param testExpression
   *          the first expression to evaluate
   * @param thenExpression
   *          the expression to evaluate if the test is {@code true}
   * @param elseExpression
   *          the expression to evaluate if the test is {@code false}
   */
  public If(
      @NonNull IExpression testExpression,
      @NonNull IExpression thenExpression,
      @NonNull IExpression elseExpression) {
    this.testExpression = testExpression;
    this.thenExpression = thenExpression;
    this.elseExpression = elseExpression;
  }

  /**
   * Get the "test" expression.
   *
   * @return the expression
   */
  protected IExpression getTestExpression() {
    return testExpression;
  }

  /**
   * Get the "then" expression.
   *
   * @return the expression
   */
  protected IExpression getThenExpression() {
    return thenExpression;
  }

  /**
   * Get the "else" expression.
   *
   * @return the expression
   */
  protected IExpression getElseExpression() {
    return elseExpression;
  }

  @Override
  public List<IExpression> getChildren() {
    return ObjectUtils.notNull(List.of(testExpression, thenExpression, elseExpression));
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<?> result = getTestExpression().accept(dynamicContext, focus);

    ISequence<?> retval;
    IBooleanItem effectiveResult = FnBoolean.fnBoolean(result);
    if (effectiveResult.toBoolean()) {
      retval = getThenExpression().accept(dynamicContext, focus);
    } else {
      retval = getElseExpression().accept(dynamicContext, focus);
    }
    return retval;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitIf(this, context);
  }
}
