/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.impl.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the '-' operator for Metapath subtraction operations.
 * <p>
 * An XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-arithmetic">arithmetic
 * expression</a> supporting subtraction.
 * <p>
 * Supports subtraction operations between:
 * <ul>
 * <li>Numeric values
 * <li>Dates (returning {@link IDayTimeDurationItem})
 * <li>DateTimes (returning {@link IDayTimeDurationItem})
 * <li>Times (returning {@link IDayTimeDurationItem})
 * <li>Date/DateTime - {@link IYearMonthDurationItem}
 * <li>Date/DateTime/Time - {@link IDayTimeDurationItem}
 * <li>{@link IYearMonthDurationItem} - {@link IYearMonthDurationItem}
 * <li>{@link IDayTimeDurationItem} - {@link IDayTimeDurationItem}
 * </ul>
 * <p>
 * Example Metapath usage:
 *
 * <pre>
 * // Numeric subtraction
 * 5 - 3 → 2
 * // Date subtraction
 * date('2024-01-01') - date('2023-01-01') → duration('P1Y')
 * // DateTime - Duration
 * date-time('2024-01-01T00:00:00') - duration('P1D') → date-time('2023-12-31T00:00:00')
 * </pre>
 */
public class Subtraction
    extends AbstractBasicArithmeticExpression {
  @NonNull
  private static final Map<
      Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> SUBTRACTION_STRATEGIES = generateStrategies();

  /**
   * An expression that gets the difference of two atomic data items.
   *
   * @param text
   *          the parsed text of the expression
   * @param minuend
   *          an expression whose result is the value being subtracted from
   * @param subtrahend
   *          an expression whose result is the value being subtracted
   */
  public Subtraction(
      @NonNull String text,
      @NonNull IExpression minuend,
      @NonNull IExpression subtrahend) {
    super(text, minuend, subtrahend);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitSubtraction(this, context);
  }

  @Override
  protected Map<Class<? extends IAnyAtomicItem>, Map<Class<? extends IAnyAtomicItem>, OperationStrategy>>
      getStrategies() {
    return SUBTRACTION_STRATEGIES;
  }

  @Override
  protected INumericItem operationAsNumeric(INumericItem left, INumericItem right) {
    // Default to numeric subtraction
    return OperationFunctions.opNumericSubtract(left, right);
  }

  @Override
  protected String unsupportedMessage(String left, String right) {
    return ObjectUtils.notNull(String.format("Subtraction of '%s' by '%s' is not supported.", left, right));
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private static Map<
      Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> generateStrategies() {
    // Date strategies
    Map<Class<? extends IAnyAtomicItem>, OperationStrategy> typeStrategies = new HashMap<>();
    typeStrategies.put(IDateItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractDates(
            (IDateItem) minuend,
            (IDateItem) subtrahend));
    typeStrategies.put(IYearMonthDurationItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractYearMonthDurationFromDate(
            (IDateItem) minuend,
            (IYearMonthDurationItem) subtrahend));
    typeStrategies.put(IDayTimeDurationItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractDayTimeDurationFromDate(
            (IDateItem) minuend,
            (IDayTimeDurationItem) subtrahend));
    Map<
        Class<? extends IAnyAtomicItem>,
        Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> strategies = new HashMap<>();
    strategies.put(IDateItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // DateTime strategies
    typeStrategies = new HashMap<>();
    typeStrategies.put(IDateTimeItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractDateTimes(
            (IDateTimeItem) minuend,
            (IDateTimeItem) subtrahend));
    typeStrategies.put(IYearMonthDurationItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractYearMonthDurationFromDateTime(
            (IDateTimeItem) minuend,
            (IYearMonthDurationItem) subtrahend));
    typeStrategies.put(IDayTimeDurationItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractDayTimeDurationFromDateTime(
            (IDateTimeItem) minuend,
            (IDayTimeDurationItem) subtrahend));
    strategies.put(IDateTimeItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // Time strategies
    typeStrategies = new HashMap<>();
    typeStrategies.put(ITimeItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractTimes(
            (ITimeItem) minuend,
            (ITimeItem) subtrahend));
    typeStrategies.put(IDayTimeDurationItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractDayTimeDurationFromTime(
            (ITimeItem) minuend,
            (IDayTimeDurationItem) subtrahend));
    strategies.put(ITimeItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // YearMonthDuration strategies
    typeStrategies = new HashMap<>();
    typeStrategies.put(IYearMonthDurationItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractYearMonthDurations(
            (IYearMonthDurationItem) minuend,
            (IYearMonthDurationItem) subtrahend));
    strategies.put(IYearMonthDurationItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // DayTimeDuration strategies
    typeStrategies = new HashMap<>();
    typeStrategies.put(IDayTimeDurationItem.class,
        (minuend, subtrahend) -> OperationFunctions.opSubtractDayTimeDurations(
            (IDayTimeDurationItem) minuend,
            (IDayTimeDurationItem) subtrahend));
    strategies.put(IDayTimeDurationItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    return CollectionUtil.unmodifiableMap(strategies);
  }
}
