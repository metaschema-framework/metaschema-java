/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.comparison;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.ComparisonFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public class ValueComparison
    extends AbstractComparison {

  /**
   * Create a new value comparison expression.
   *
   * @param left
   *          the expression to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the expression to compare with
   */
  public ValueComparison(
      @NonNull IExpression left,
      @NonNull ComparisonFunctions.Operator operator,
      @NonNull IExpression right) {
    super(left, operator, right);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitValueComparison(this, context);
  }

  @Override
  public ISequence<? extends IBooleanItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    IAnyAtomicItem left = getFirstDataItem(getLeft().accept(dynamicContext, focus), false);
    IAnyAtomicItem right = getFirstDataItem(getRight().accept(dynamicContext, focus), false);

    return resultOrEmpty(left, right);
  }

  /**
   * Compare the two atomic items.
   *
   * @param leftItem
   *          the first item to compare
   * @param rightItem
   *          the second item to compare
   * @return a or an empty {@link ISequence} if either item is {@code null}
   */
  @NonNull
  protected ISequence<? extends IBooleanItem> resultOrEmpty(@Nullable IAnyAtomicItem leftItem,
      @Nullable IAnyAtomicItem rightItem) {
    ISequence<? extends IBooleanItem> retval;
    if (leftItem == null || rightItem == null) {
      retval = ISequence.empty();
    } else {
      IBooleanItem result = ComparisonFunctions.valueCompairison(leftItem, getOperator(), rightItem);
      retval = ISequence.of(result);
    }
    return retval;
  }

}
