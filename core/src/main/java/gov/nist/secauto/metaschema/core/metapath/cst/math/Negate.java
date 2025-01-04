/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractUnaryExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.ExpressionUtils;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.impl.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-arithmetic">arithmetic
 * expression</a> supporting negation.
 */
public class Negate
    extends AbstractUnaryExpression {

  @NonNull
  private final Class<? extends INumericItem> staticResultType;

  /**
   * Create an expression that gets the complement of a number.
   *
   * @param text
   *          the parsed text of the expression
   * @param expr
   *          the expression whose item result will be complemented
   */
  @SuppressWarnings("null")
  public Negate(@NonNull String text, @NonNull IExpression expr) {
    super(text, expr);
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(INumericItem.class, List.of(expr));
  }

  @Override
  public Class<INumericItem> getBaseResultType() {
    return INumericItem.class;
  }

  @Override
  public Class<? extends INumericItem> getStaticResultType() {
    return staticResultType;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitNegate(this, context);
  }

  @Override
  protected ISequence<? extends INumericItem> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    INumericItem item = FunctionUtils.toNumericOrNull(
        ISequence.of(getChild().accept(dynamicContext, focus).atomize()).getFirstItem(true));
    if (item != null) {
      item = OperationFunctions.opNumericUnaryMinus(item);
    }
    return ISequence.of(item);
  }
}
