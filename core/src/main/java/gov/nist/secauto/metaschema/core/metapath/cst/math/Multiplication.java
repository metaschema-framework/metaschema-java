/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.impl.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-arithmetic">arithmetic
 * expression</a> supporting multiplication.
 * <p>
 * Supports multiplication between:
 * <ul>
 * <li>Numeric values</li>
 * <li>YearMonthDuration × Numeric</li>
 * <li>DayTimeDuration × Numeric</li>
 * </ul>
 *
 * <p>
 * Numeric operands are automatically converted using
 * {@link FunctionUtils#toNumeric}.
 */
public class Multiplication
    extends AbstractBasicArithmeticExpression {

  /**
   * An expression that gets the product result by multiplying two values.
   *
   * @param left
   *          the item to be divided
   * @param right
   *          the item to divide by
   */
  public Multiplication(@NonNull IExpression left, @NonNull IExpression right) {
    super(left, right);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitMultiplication(this, context);
  }

  /**
   * Get the sum of two atomic items.
   *
   * @param left
   *          the first item to multiply
   * @param right
   *          the second item to multiply
   * @return the product of both items or an empty {@link ISequence} if either
   *         item is {@code null}
   * @throws InvalidTypeMetapathException
   *           for unsupported operand combinations.
   */
  @Override
  @NonNull
  protected IAnyAtomicItem operation(@NonNull IAnyAtomicItem left, @NonNull IAnyAtomicItem right) {
    return multiply(left, right);
  }

  /**
   * Get the sum of two atomic items.
   *
   * @param leftItem
   *          the first item to multiply
   * @param rightItem
   *          the second item to multiply
   * @return the product of both items
   * @throws InvalidTypeMetapathException
   *           for unsupported operand combinations.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  @NonNull
  public static IAnyAtomicItem multiply(
      @NonNull IAnyAtomicItem leftItem,
      @NonNull IAnyAtomicItem rightItem) {
    IAnyAtomicItem retval = null;
    if (leftItem instanceof IYearMonthDurationItem) {
      IYearMonthDurationItem left = (IYearMonthDurationItem) leftItem;
      if (rightItem instanceof INumericItem) {
        retval = OperationFunctions.opMultiplyYearMonthDuration(left, (INumericItem) rightItem);
      }
    } else if (leftItem instanceof IDayTimeDurationItem) {
      IDayTimeDurationItem left = (IDayTimeDurationItem) leftItem;
      if (rightItem instanceof INumericItem) {
        retval = OperationFunctions.opMultiplyDayTimeDuration(left, (INumericItem) rightItem);
      }
    } else {
      // handle as numeric
      INumericItem left = FunctionUtils.toNumeric(leftItem);
      if (rightItem instanceof INumericItem) {
        INumericItem right = FunctionUtils.toNumeric(rightItem);
        retval = OperationFunctions.opNumericMultiply(left, right);
      } else if (rightItem instanceof IYearMonthDurationItem) {
        retval = OperationFunctions.opMultiplyYearMonthDuration((IYearMonthDurationItem) rightItem, left);
      } else if (rightItem instanceof IDayTimeDurationItem) {
        retval = OperationFunctions.opMultiplyDayTimeDuration((IDayTimeDurationItem) rightItem, left);
      }
    }
    if (retval == null) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Multiplication between %s and %s is not supported.",
              leftItem.toSignature(),
              rightItem.toSignature()));
    }
    return retval;
  }
}
