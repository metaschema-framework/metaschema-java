/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.impl.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnNot;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUntypedAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import java.util.Locale;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A collection of comparison functions supporting value and general
 * comparisons.
 * <p>
 * Based on the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-comparisons">comparison
 * expressions</a> syntax.
 */
// FIXME: Add unit tests
@SuppressWarnings({ "PMD.GodClass", "PMD.CyclomaticComplexity" })
public final class ComparisonFunctions {
  /**
   * Comparison operators.
   */
  public enum Operator {
    /**
     * An equal comparison.
     */
    EQ,
    /**
     * A not equal comparison.
     */
    NE,
    /**
     * A less than comparison.
     */
    LT,
    /**
     * A less than or equal comparison.
     */
    LE,
    /**
     * A greater than comparison.
     */
    GT,
    /**
     * A greater than or equal comparison.
     */
    GE;
  }

  private ComparisonFunctions() {
    // disable construction
  }

  /**
   * Compare the two items using the provided {@code operator}.
   *
   * @param leftItem
   *          the first item to compare
   * @param operator
   *          the comparison operator
   * @param rightItem
   *          the second item to compare
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return the result of the comparison
   */
  @NonNull
  public static IBooleanItem valueCompairison(
      @NonNull IAnyAtomicItem leftItem,
      @NonNull Operator operator,
      @NonNull IAnyAtomicItem rightItem,
      @Nullable DynamicContext dynamicContext) {
    return compare(leftItem, operator, rightItem, dynamicContext);
  }

  /**
   * Compare the sets of atomic items.
   *
   * @param leftItems
   *          the first set of items to compare
   * @param operator
   *          the comparison operator
   * @param rightItems
   *          the second set of items to compare
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return a or an empty {@link ISequence} if either item is {@code null}
   */
  @NonNull
  public static IBooleanItem generalComparison( // NOPMD - acceptable complexity
      @NonNull ISequence<? extends IAnyAtomicItem> leftItems,
      @NonNull Operator operator,
      @NonNull ISequence<? extends IAnyAtomicItem> rightItems,
      @NonNull DynamicContext dynamicContext) {

    IBooleanItem retval = IBooleanItem.FALSE;
    for (IAnyAtomicItem left : leftItems) {
      assert left != null;
      for (IAnyAtomicItem right : rightItems) {
        assert right != null;
        IAnyAtomicItem leftCast;
        IAnyAtomicItem rightCast;
        if (left instanceof IUntypedAtomicItem) {
          if (right instanceof IUntypedAtomicItem) {
            leftCast = IStringItem.cast(left);
            rightCast = IStringItem.cast(right);
          } else {
            leftCast = applyGeneralComparisonCast(right, left);
            rightCast = right;
          }
        } else if (right instanceof IUntypedAtomicItem) {
          leftCast = left;
          rightCast = applyGeneralComparisonCast(left, right);
        } else {
          leftCast = left;
          rightCast = right;
        }

        assert leftCast != null;
        IBooleanItem result = compare(leftCast, operator, rightCast, dynamicContext);
        if (IBooleanItem.TRUE.equals(result)) {
          retval = IBooleanItem.TRUE;
        }
      }
    }
    return retval;
  }

  /**
   * Attempts to cast the provided {@code other} item to the type of the
   * {@code item}.
   *
   * @param item
   *          the item whose type the other item is to be cast to
   * @param other
   *          the item to cast
   * @return the casted item
   */
  @NonNull
  private static IAnyAtomicItem applyGeneralComparisonCast(
      @NonNull IAnyAtomicItem item,
      @NonNull IAnyAtomicItem other) {
    IAnyAtomicItem retval;
    if (item instanceof INumericItem) {
      retval = IDecimalItem.cast(other);
    } else if (item instanceof IDayTimeDurationItem) {
      retval = IDayTimeDurationItem.cast(other);
    } else if (item instanceof IYearMonthDurationItem) {
      retval = IYearMonthDurationItem.cast(other);
    } else {
      retval = item.castAsType(other);
    }
    return retval;
  }

  /**
   * Compare the {@code right} item with the {@code left} item using the specified
   * {@code operator}.
   *
   * @param left
   *          the value to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the value to compare with
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return the comparison result
   */
  @NonNull
  public static IBooleanItem compare( // NOPMD - unavoidable
      @NonNull IAnyAtomicItem left,
      @NonNull Operator operator,
      @NonNull IAnyAtomicItem right,
      @Nullable DynamicContext dynamicContext) {
    @NonNull
    IBooleanItem retval;
    if (left instanceof IStringItem || right instanceof IStringItem) {
      retval = stringCompare(IStringItem.cast(left), operator, IStringItem.cast(right));
    } else if (left instanceof INumericItem && right instanceof INumericItem) {
      retval = numericCompare((INumericItem) left, operator, (INumericItem) right);
    } else if (left instanceof IBooleanItem && right instanceof IBooleanItem) {
      retval = booleanCompare((IBooleanItem) left, operator, (IBooleanItem) right);
    } else if (left instanceof IDateTimeItem && right instanceof IDateTimeItem) {
      retval = dateTimeCompare((IDateTimeItem) left, operator, (IDateTimeItem) right, dynamicContext);
    } else if (left instanceof IDateItem && right instanceof IDateItem) {
      retval = dateTimeCompare(
          ((IDateItem) left).asDateTime(),
          operator,
          ((IDateItem) right).asDateTime(),
          dynamicContext);
    } else if (left instanceof ITimeItem && right instanceof ITimeItem) {
      retval = dateTimeCompare(
          IDateTimeItem.valueOf(OperationFunctions.DATE_1972_12_31, (ITimeItem) left),
          operator,
          IDateTimeItem.valueOf(OperationFunctions.DATE_1972_12_31, (ITimeItem) right),
          dynamicContext);
    } else if (left instanceof IDurationItem && right instanceof IDurationItem) {
      retval = durationCompare((IDurationItem) left, operator, (IDurationItem) right);
    } else if (left instanceof IBase64BinaryItem && right instanceof IBase64BinaryItem) {
      retval = binaryCompare((IBase64BinaryItem) left, operator, (IBase64BinaryItem) right);
    } else {
      throw new InvalidTypeMetapathException(
          null,
          String.format("invalid types for comparison: %s %s %s", left.getClass().getName(),
              operator.name().toLowerCase(Locale.ROOT), right.getClass().getName()));
    }
    return retval;
  }

  /**
   * Perform a string-based comparison of the {@code right} item against the
   * {@code left} item using the specified {@code operator}.
   *
   * @param left
   *          the value to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the value to compare with
   * @return the comparison result
   */
  @NonNull
  public static IBooleanItem stringCompare(
      @NonNull IStringItem left,
      @NonNull Operator operator,
      @NonNull IStringItem right) {
    int result = left.compareTo(right);
    boolean retval;
    switch (operator) {
    case EQ:
      retval = result == 0;
      break;
    case GE:
      retval = result >= 0;
      break;
    case GT:
      retval = result > 0;
      break;
    case LE:
      retval = result <= 0;
      break;
    case LT:
      retval = result < 0;
      break;
    case NE:
      retval = result != 0;
      break;
    default:
      throw new IllegalArgumentException(
          String.format("Unsupported operator '%s'", operator.name())); // NOPMD
    }
    return IBooleanItem.valueOf(retval);
  }

  /**
   * Perform a number-based comparison of the {@code right} item against the
   * {@code left} item using the specified {@code operator}.
   *
   * @param left
   *          the value to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the value to compare with
   * @return the comparison result
   */
  @NonNull
  public static IBooleanItem numericCompare(@NonNull INumericItem left, @NonNull Operator operator,
      @NonNull INumericItem right) {
    IBooleanItem retval;
    switch (operator) {
    case EQ:
      retval = OperationFunctions.opNumericEqual(left, right);
      break;
    case GE: {
      IBooleanItem gt = OperationFunctions.opNumericGreaterThan(left, right);
      IBooleanItem eq = OperationFunctions.opNumericEqual(left, right);
      retval = IBooleanItem.valueOf(gt.toBoolean() || eq.toBoolean());
      break;
    }
    case GT:
      retval = OperationFunctions.opNumericGreaterThan(left, right);
      break;
    case LE: {
      IBooleanItem lt = OperationFunctions.opNumericLessThan(left, right);
      IBooleanItem eq = OperationFunctions.opNumericEqual(left, right);
      retval = IBooleanItem.valueOf(lt.toBoolean() || eq.toBoolean());
      break;
    }
    case LT:
      retval = OperationFunctions.opNumericLessThan(left, right);
      break;
    case NE:
      retval = FnNot.fnNot(OperationFunctions.opNumericEqual(left, right));
      break;
    default:
      throw new IllegalArgumentException(String.format("Unsupported operator '%s'", operator.name()));
    }
    return retval;
  }

  /**
   * Perform a boolean-based comparison of the {@code right} item against the
   * {@code left} item using the specified {@code operator}.
   *
   * @param left
   *          the value to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the value to compare with
   * @return the comparison result
   */
  @NonNull
  public static IBooleanItem booleanCompare(
      @NonNull IBooleanItem left,
      @NonNull Operator operator,
      @NonNull IBooleanItem right) {
    IBooleanItem retval;
    switch (operator) {
    case EQ:
      retval = OperationFunctions.opBooleanEqual(left, right);
      break;
    case GE: {
      IBooleanItem gt = OperationFunctions.opBooleanGreaterThan(left, right);
      IBooleanItem eq = OperationFunctions.opBooleanEqual(left, right);
      retval = IBooleanItem.valueOf(gt.toBoolean() || eq.toBoolean());
      break;
    }
    case GT:
      retval = OperationFunctions.opBooleanGreaterThan(left, right);
      break;
    case LE: {
      IBooleanItem lt = OperationFunctions.opBooleanLessThan(left, right);
      IBooleanItem eq = OperationFunctions.opBooleanEqual(left, right);
      retval = IBooleanItem.valueOf(lt.toBoolean() || eq.toBoolean());
      break;
    }
    case LT:
      retval = OperationFunctions.opBooleanLessThan(left, right);
      break;
    case NE:
      retval = FnNot.fnNot(OperationFunctions.opBooleanEqual(left, right));
      break;
    default:
      throw new IllegalArgumentException(String.format("Unsupported operator '%s'", operator.name()));
    }
    return retval;
  }

  /**
   * Perform a date and time-based comparison of the {@code right} item against
   * the {@code left} item using the specified {@code operator}.
   *
   * @param left
   *          the value to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the value to compare with
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return the comparison result
   */
  @NonNull
  public static IBooleanItem dateTimeCompare(
      @NonNull IDateTimeItem left,
      @NonNull Operator operator,
      @NonNull IDateTimeItem right,
      @Nullable DynamicContext dynamicContext) {
    IBooleanItem retval;
    switch (operator) {
    case EQ:
      retval = OperationFunctions.opDateTimeEqual(left, right, dynamicContext);
      break;
    case GE: {
      // pre-normalize for efficiency
      IDateTimeItem leftNormalized = dynamicContext == null ? left : left.normalize(dynamicContext);
      IDateTimeItem rightNormalized = dynamicContext == null ? right : right.normalize(dynamicContext);
      retval = IBooleanItem.valueOf(
          OperationFunctions.opDateTimeGreaterThan(leftNormalized, rightNormalized, null).toBoolean()
              || OperationFunctions.opDateTimeEqual(leftNormalized, rightNormalized, null).toBoolean());
      break;
    }
    case GT:
      retval = OperationFunctions.opDateTimeGreaterThan(left, right, dynamicContext);
      break;
    case LE: {
      // pre-normalize for efficiency
      IDateTimeItem leftNormalized = dynamicContext == null ? left : left.normalize(dynamicContext);
      IDateTimeItem rightNormalized = dynamicContext == null ? right : right.normalize(dynamicContext);
      retval = IBooleanItem.valueOf(
          OperationFunctions.opDateTimeLessThan(leftNormalized, rightNormalized, null).toBoolean()
              || OperationFunctions.opDateTimeEqual(leftNormalized, rightNormalized, null).toBoolean());
      break;
    }
    case LT:
      retval = OperationFunctions.opDateTimeLessThan(left, right, dynamicContext);
      break;
    case NE:
      retval = FnNot.fnNot(OperationFunctions.opDateTimeEqual(left, right, dynamicContext));
      break;
    default:
      throw new IllegalArgumentException(String.format("Unsupported operator '%s'", operator.name()));
    }
    return retval;
  }

  /**
   * Perform a duration-based comparison of the {@code right} item against the
   * {@code left} item using the specified {@code operator}.
   *
   * @param left
   *          the value to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the value to compare with
   * @return the comparison result
   */
  @NonNull
  public static IBooleanItem durationCompare( // NOPMD - unavoidable
      @NonNull IDurationItem left,
      @NonNull Operator operator,
      @NonNull IDurationItem right) {
    IBooleanItem retval = null;
    switch (operator) {
    case EQ:
      retval = OperationFunctions.opDurationEqual(left, right);
      break;
    case GE:
      if (left instanceof IYearMonthDurationItem && right instanceof IYearMonthDurationItem) {
        IBooleanItem gt = OperationFunctions.opYearMonthDurationGreaterThan(
            (IYearMonthDurationItem) left,
            (IYearMonthDurationItem) right);
        IBooleanItem eq = OperationFunctions.opDurationEqual(left, right);
        retval = IBooleanItem.valueOf(gt.toBoolean() || eq.toBoolean());
      } else if (left instanceof IDayTimeDurationItem && right instanceof IDayTimeDurationItem) {
        IBooleanItem gt = OperationFunctions.opDayTimeDurationGreaterThan(
            (IDayTimeDurationItem) left,
            (IDayTimeDurationItem) right);
        IBooleanItem eq = OperationFunctions.opDurationEqual(left, right);
        retval = IBooleanItem.valueOf(gt.toBoolean() || eq.toBoolean());
      }
      break;
    case GT:
      if (left instanceof IYearMonthDurationItem && right instanceof IYearMonthDurationItem) {
        retval = OperationFunctions.opYearMonthDurationGreaterThan(
            (IYearMonthDurationItem) left,
            (IYearMonthDurationItem) right);
      } else if (left instanceof IDayTimeDurationItem && right instanceof IDayTimeDurationItem) {
        retval = OperationFunctions.opDayTimeDurationGreaterThan(
            (IDayTimeDurationItem) left,
            (IDayTimeDurationItem) right);
      }
      break;
    case LE:
      if (left instanceof IYearMonthDurationItem && right instanceof IYearMonthDurationItem) {
        IBooleanItem lt = OperationFunctions.opYearMonthDurationLessThan(
            (IYearMonthDurationItem) left,
            (IYearMonthDurationItem) right);
        IBooleanItem eq = OperationFunctions.opDurationEqual(left, right);
        retval = IBooleanItem.valueOf(lt.toBoolean() || eq.toBoolean());
      } else if (left instanceof IDayTimeDurationItem && right instanceof IDayTimeDurationItem) {
        IBooleanItem lt = OperationFunctions.opDayTimeDurationLessThan(
            (IDayTimeDurationItem) left,
            (IDayTimeDurationItem) right);
        IBooleanItem eq = OperationFunctions.opDurationEqual(left, right);
        retval = IBooleanItem.valueOf(lt.toBoolean() || eq.toBoolean());
      }
      break;
    case LT:
      if (left instanceof IYearMonthDurationItem && right instanceof IYearMonthDurationItem) {
        retval = OperationFunctions.opYearMonthDurationLessThan(
            (IYearMonthDurationItem) left,
            (IYearMonthDurationItem) right);
      } else if (left instanceof IDayTimeDurationItem && right instanceof IDayTimeDurationItem) {
        retval = OperationFunctions.opDayTimeDurationLessThan(
            (IDayTimeDurationItem) left,
            (IDayTimeDurationItem) right);
      }
      break;
    case NE:
      retval = FnNot.fnNot(OperationFunctions.opDurationEqual(left, right));
      break;
    default:
      throw new IllegalArgumentException(String.format("Unsupported operator '%s'", operator.name()));
    }

    if (retval == null) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("The item types '%s' and '%s' are not comparable",
              left.getClass().getName(),
              right.getClass().getName()));
    }
    return retval;
  }

  /**
   * Perform a binary data-based comparison of the {@code right} item against the
   * {@code left} item using the specified {@code operator}.
   *
   * @param left
   *          the value to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the value to compare with
   * @return the comparison result
   */
  @NonNull
  public static IBooleanItem binaryCompare(@NonNull IBase64BinaryItem left, @NonNull Operator operator,
      @NonNull IBase64BinaryItem right) {
    IBooleanItem retval;
    switch (operator) {
    case EQ:
      retval = OperationFunctions.opBase64BinaryEqual(left, right);
      break;
    case GE: {
      IBooleanItem gt = OperationFunctions.opBase64BinaryGreaterThan(left, right);
      IBooleanItem eq = OperationFunctions.opBase64BinaryEqual(left, right);
      retval = IBooleanItem.valueOf(gt.toBoolean() || eq.toBoolean());
      break;
    }
    case GT:
      retval = OperationFunctions.opBase64BinaryGreaterThan(left, right);
      break;
    case LE: {
      IBooleanItem lt = OperationFunctions.opBase64BinaryLessThan(left, right);
      IBooleanItem eq = OperationFunctions.opBase64BinaryEqual(left, right);
      retval = IBooleanItem.valueOf(lt.toBoolean() || eq.toBoolean());
      break;
    }
    case LT:
      retval = OperationFunctions.opBase64BinaryLessThan(left, right);
      break;
    case NE:
      retval = FnNot.fnNot(OperationFunctions.opBase64BinaryEqual(left, right));
      break;
    default:
      throw new IllegalArgumentException(String.format("Unsupported operator '%s'", operator.name()));
    }
    return retval;
  }
}
