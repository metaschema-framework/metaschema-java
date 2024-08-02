/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Addition
    extends AbstractBasicArithmeticExpression {

  /**
   * An expression that sums two atomic data items.
   *
   * @param left
   *          an expression whose result is summed
   * @param right
   *          an expression whose result is summed
   */
  public Addition(@NonNull IExpression left, @NonNull IExpression right) {
    super(left, right);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitAddition(this, context);
  }

  /**
   * Get the sum of two atomic items.
   *
   * @param left
   *          the first item to sum
   * @param right
   *          the second item to sum
   * @return the sum of both items or an empty {@link ISequence} if either item is
   *         {@code null}
   */
  @Override
  protected IAnyAtomicItem operation(@NonNull IAnyAtomicItem left, @NonNull IAnyAtomicItem right) {
    return sum(left, right);
  }

  /**
   * Get the sum of two atomic items.
   *
   * @param leftItem
   *          the first item to sum
   * @param rightItem
   *          the second item to sum
   * @return the sum of both items
   */
  @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity" })
  @NonNull
  public static IAnyAtomicItem sum(
      @NonNull IAnyAtomicItem leftItem, // NOPMD - intentional
      @NonNull IAnyAtomicItem rightItem) {
    IAnyAtomicItem retval = null;
    if (leftItem instanceof IDateItem) {
      IDateItem left = (IDateItem) leftItem;
      if (rightItem instanceof IYearMonthDurationItem) {
        retval = OperationFunctions.opAddYearMonthDurationToDate(left, (IYearMonthDurationItem) rightItem);
      } else if (rightItem instanceof IDayTimeDurationItem) {
        retval = OperationFunctions.opAddDayTimeDurationToDate(left, (IDayTimeDurationItem) rightItem);
      }
    } else if (leftItem instanceof IDateTimeItem) {
      IDateTimeItem left = (IDateTimeItem) leftItem;
      if (rightItem instanceof IYearMonthDurationItem) {
        retval = OperationFunctions.opAddYearMonthDurationToDateTime(left, (IYearMonthDurationItem) rightItem);
      } else if (rightItem instanceof IDayTimeDurationItem) {
        retval = OperationFunctions.opAddDayTimeDurationToDateTime(left, (IDayTimeDurationItem) rightItem);
      }
    } else if (leftItem instanceof IYearMonthDurationItem) {
      IYearMonthDurationItem left = (IYearMonthDurationItem) leftItem;
      if (rightItem instanceof IDateItem) {
        retval = OperationFunctions.opAddYearMonthDurationToDate((IDateItem) rightItem, left);
      } else if (rightItem instanceof IDateTimeItem) {
        retval = OperationFunctions.opAddYearMonthDurationToDateTime((IDateTimeItem) rightItem, left);
      } else if (rightItem instanceof IYearMonthDurationItem) {
        retval = OperationFunctions.opSubtractYearMonthDurations(left, (IYearMonthDurationItem) rightItem);
      }
    } else if (leftItem instanceof IDayTimeDurationItem) {
      IDayTimeDurationItem left = (IDayTimeDurationItem) leftItem;
      if (rightItem instanceof IDateItem) {
        retval = OperationFunctions.opAddDayTimeDurationToDate((IDateItem) rightItem, left);
      } else if (rightItem instanceof IDateTimeItem) {
        retval = OperationFunctions.opAddDayTimeDurationToDateTime((IDateTimeItem) rightItem, left);
      } else if (rightItem instanceof IDayTimeDurationItem) {
        retval = OperationFunctions.opAddDayTimeDurations(left, (IDayTimeDurationItem) rightItem);
      }
    } else {
      // handle as numeric
      INumericItem left = FunctionUtils.toNumeric(leftItem);
      INumericItem right = FunctionUtils.toNumeric(rightItem);
      retval = OperationFunctions.opNumericAdd(left, right);
    }
    if (retval == null) {
      throw new UnsupportedOperationException(
          String.format("The expression '%s + %s' is not supported", leftItem.getClass().getName(),
              rightItem.getClass().getName()));
    }
    return retval;
  }
}
