/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Negate
    extends AbstractUnaryExpression {

  @NonNull
  private final Class<? extends INumericItem> staticResultType;

  /**
   * Create an expression that gets the complement of a number.
   *
   * @param expr
   *          the expression whose item result will be complemented
   */
  @SuppressWarnings("null")
  public Negate(@NonNull IExpression expr) {
    super(expr);
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
  public ISequence<? extends INumericItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    INumericItem item = FunctionUtils.toNumericOrNull(
        getFirstDataItem(getChild().accept(dynamicContext, focus), true));
    if (item != null) {
      item = OperationFunctions.opNumericUnaryMinus(item);
    }
    return ISequence.of(item);
  }
}
