/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#combining_seq">filtering
 * expression</a>.
 */
public abstract class AbstractFilterExpression
    extends AbstractBinaryExpression<IExpression, IExpression> {

  @NonNull
  private final Class<? extends IItem> staticResultType;

  /**
   * Construct a new filter expression.
   *
   * @param left
   *          an expression indicating the items to filter
   * @param right
   *          an expression indicating the items to use as the filter
   */
  @SuppressWarnings("null")
  public AbstractFilterExpression(@NonNull IExpression left, @NonNull IExpression right) {
    super(left, right);
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(IItem.class, List.of(left, right));
  }

  @Override
  public Class<? extends IItem> getStaticResultType() {
    return staticResultType;
  }

  @Override
  public ISequence<? extends IItem> accept(
      @NonNull DynamicContext dynamicContext,
      @NonNull ISequence<?> focus) {

    ISequence<?> left = getLeft().accept(dynamicContext, focus);
    ISequence<?> right = getRight().accept(dynamicContext, focus);
    List<? extends IItem> rightList = right.getValue();

    return applyFilterTo(left, rightList);
  }

  /**
   * A callback used to apply the filter to the result of evaluating the left
   * expression.
   *
   * @param result
   *          the set of items to filter
   * @param items
   *          a list of items to filter with
   * @return the filtered result set
   */
  @NonNull
  protected abstract ISequence<?> applyFilterTo(
      @NonNull ISequence<?> result,
      @NonNull List<? extends IItem> items);
}
