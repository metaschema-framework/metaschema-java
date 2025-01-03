/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.ComparisonFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-general-comparisons">general
 * comparisons</a>.
 */
public class GeneralComparison
    extends AbstractComparison {

  /**
   * Create a new value comparison expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param left
   *          the expression to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the expression to compare with
   */
  public GeneralComparison(
      @NonNull String text,
      @NonNull IExpression left,
      @NonNull ComparisonFunctions.Operator operator,
      @NonNull IExpression right) {
    super(text, left, operator, right);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitGeneralComparison(this, context);
  }

  @Override
  public ISequence<? extends IBooleanItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<? extends IAnyAtomicItem> leftItems = ISequence.of(getLeft().accept(dynamicContext, focus).atomize());
    ISequence<? extends IAnyAtomicItem> rightItems = ISequence.of(getRight().accept(dynamicContext, focus).atomize());
    return ISequence.of(ComparisonFunctions.generalCompairison(leftItems, getOperator(), rightItems));
  }
}
