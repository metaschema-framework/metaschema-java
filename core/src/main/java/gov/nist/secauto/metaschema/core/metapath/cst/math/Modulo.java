/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class Modulo
    extends AbstractArithmeticExpression<INumericItem> {

  /**
   * Create an expression that gets the numeric remainder from dividing the
   * dividend by the divisor, also called the "modulo operation".
   *
   * @param dividend
   *          the item to be divided
   * @param divisor
   *          the item to divide by
   */
  public Modulo(@NonNull IExpression dividend, @NonNull IExpression divisor) {
    super(dividend, divisor, INumericItem.class);
  }

  @Override
  public Class<INumericItem> getBaseResultType() {
    return INumericItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitModulo(this, context);
  }

  @Override
  public ISequence<? extends INumericItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    INumericItem dividend = FunctionUtils.toNumeric(getLeft().accept(dynamicContext, focus), true);
    INumericItem divisor = FunctionUtils.toNumeric(getRight().accept(dynamicContext, focus), true);
    return resultOrEmpty(dividend, divisor);
  }

  /**
   * Get the numeric remainder from dividing the dividend by the divisor.
   *
   * @param dividend
   *          the item to be divided
   * @param divisor
   *          the item to divide by
   * @return the remainder or an empty {@link ISequence} if either item is
   *         {@code null}
   */
  @NonNull
  protected static ISequence<? extends INumericItem> resultOrEmpty(@Nullable INumericItem dividend,
      @Nullable INumericItem divisor) {
    ISequence<? extends INumericItem> retval;
    if (dividend == null || divisor == null) {
      retval = ISequence.empty();
    } else {
      INumericItem result = OperationFunctions.opNumericMod(dividend, divisor);
      retval = ISequence.of(result);
    }
    return retval;
  }
}
