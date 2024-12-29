/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An immutable binary expression that supports basic arithmetic evaluation.
 * <p>
 * The result type is determined through static analysis of the sub-expressions,
 * which may result in a more specific type that is a sub-class of the base
 * result type.
 * <p>
 * The arithmetic operation method
 * {@link #operation(IAnyAtomicItem, IAnyAtomicItem)} must be implemented by
 * extending classes to provide the evaluation logic.
 */
public abstract class AbstractBasicArithmeticExpression
    extends AbstractArithmeticExpression<IAnyAtomicItem> {

  /**
   * An expression that represents a basic arithmetic operation on two values.
   *
   * @param left
   *          the first item
   * @param right
   *          the second item
   */
  public AbstractBasicArithmeticExpression(@NonNull IExpression left, @NonNull IExpression right) {
    super(left, right, IAnyAtomicItem.class);
  }

  @Override
  public Class<IAnyAtomicItem> getBaseResultType() {
    return IAnyAtomicItem.class;
  }

  @Override
  public ISequence<? extends IAnyAtomicItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    IAnyAtomicItem leftItem = ISequence.of(getLeft().accept(dynamicContext, focus).atomize()).getFirstItem(true);
    IAnyAtomicItem rightItem = ISequence.of(getRight().accept(dynamicContext, focus).atomize()).getFirstItem(true);

    return resultOrEmpty(leftItem, rightItem);
  }

  /**
   * Setup the operation on two atomic items.
   *
   * @param leftItem
   *          the first item
   * @param rightItem
   *          the second item
   * @return the result of the operation or an empty {@link ISequence} if either
   *         item is {@code null}
   */
  @NonNull
  protected ISequence<? extends IAnyAtomicItem> resultOrEmpty(
      @Nullable IAnyAtomicItem leftItem,
      @Nullable IAnyAtomicItem rightItem) {
    ISequence<? extends IAnyAtomicItem> retval;
    if (leftItem == null || rightItem == null) {
      retval = ISequence.empty();
    } else {
      IAnyAtomicItem result = operation(leftItem, rightItem);
      retval = ISequence.of(result);
    }
    return retval;
  }

  /**
   * Performs the arithmetic operation using the two provided values.
   *
   * @param left
   *          the first item
   * @param right
   *          the second item
   * @return the result of the operation
   */
  @NonNull
  protected abstract IAnyAtomicItem operation(@NonNull IAnyAtomicItem left, @NonNull IAnyAtomicItem right);
}
