/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.math;

import java.util.List;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IExpression;
import dev.metaschema.core.metapath.cst.AbstractUnaryExpression;
import dev.metaschema.core.metapath.cst.ExpressionUtils;
import dev.metaschema.core.metapath.cst.IExpressionVisitor;
import dev.metaschema.core.metapath.function.FunctionUtils;
import dev.metaschema.core.metapath.function.impl.OperationFunctions;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.INumericItem;
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
    IAnyAtomicItem atomicItem = ISequence.of(getChild().accept(dynamicContext, focus).atomize()).getFirstItem(true);
    INumericItem item = atomicItem == null ? null : FunctionUtils.castToNumeric(atomicItem);
    if (item != null) {
      item = OperationFunctions.opNumericUnaryMinus(item);
    }
    return ISequence.of(item);
  }
}
