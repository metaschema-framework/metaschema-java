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
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-arithmetic">arithmetic
 * expression</a> supporting subtraction.
 * <p>
 * Supports subtraction operations between:
 * <ul>
 * <li>Numeric values</li>
 * <li>Dates (returning duration)</li>
 * <li>DateTimes (returning duration)</li>
 * <li>Date/DateTime - YearMonthDuration</li>
 * <li>Date/DateTime - DayTimeDuration</li>
 * <li>YearMonthDuration - YearMonthDuration</li>
 * <li>DayTimeDuration - DayTimeDuration</li>
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
  private static final Map<Class<?>, Map<Class<?>, SubtractionStrategy>> SUBTRACTION_STRATEGIES = generateStrategies();

  /**
   * An expression that gets the difference of two atomic data items.
   *
   * @param minuend
   *          an expression whose result is the value being subtracted from
   * @param subtrahend
   *          an expression whose result is the value being subtracted
   */
  public Subtraction(@NonNull IExpression minuend, @NonNull IExpression subtrahend) {
    super(minuend, subtrahend);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitSubtraction(this, context);
  }

  /**
   * Get the difference of two atomic items.
   *
   * @param minuend
   *          the item being subtracted from
   * @param subtrahend
   *          the item being subtracted
   * @return the difference of the items or an empty {@link ISequence} if either
   *         item is {@code null}
   */
  @Override
  @NonNull
  protected IAnyAtomicItem operation(@NonNull IAnyAtomicItem minuend, @NonNull IAnyAtomicItem subtrahend) {
    return subtract(minuend, subtrahend);
  }

  /**
   * Get the difference of two atomic items.
   *
   * @param minuend
   *          the item being subtracted from
   * @param subtrahend
   *          the item being subtracted
   * @return the difference of the items
   */
  @SuppressWarnings("PMD.OnlyOneReturn")
  @NonNull
  public static IAnyAtomicItem subtract(
      @NonNull IAnyAtomicItem minuend,
      @NonNull IAnyAtomicItem subtrahend) {
    Class<? extends IAnyAtomicItem> minuendClass = minuend.getClass();

    // Find matching strategy for minuend type
    Map<Class<?>, SubtractionStrategy> typeStrategies = null;
    for (Map.Entry<Class<?>, Map<Class<?>, SubtractionStrategy>> entry : SUBTRACTION_STRATEGIES.entrySet()) {
      if (entry.getKey().isAssignableFrom(minuendClass)) {
        // this is a matching strategy map
        typeStrategies = entry.getValue();
        break;
      }
    }

    if (typeStrategies == null) {
      return numericSubtract(minuend, subtrahend);
    }

    // Find matching strategy for subtrahend type
    Class<? extends IAnyAtomicItem> subtrahendClass = subtrahend.getClass();
    for (Map.Entry<Class<?>, SubtractionStrategy> entry : typeStrategies.entrySet()) {
      if (entry.getKey().isAssignableFrom(subtrahendClass)) {
        // this is matching strategy, execute it
        return entry.getValue().subtract(minuend, subtrahend);
      }
    }

    throw new UnsupportedOperationException(
        String.format("Subtraction of '%s' by '%s' is not supported.",
            minuend.toSignature(),
            subtrahend.toSignature()));
  }

  @NonNull
  private static IAnyAtomicItem numericSubtract(
      @NonNull IAnyAtomicItem left,
      @NonNull IAnyAtomicItem right) {
    // Default to numeric subtraction
    return OperationFunctions.opNumericSubtract(
        FunctionUtils.toNumeric(left),
        FunctionUtils.toNumeric(right));
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private static Map<Class<?>, Map<Class<?>, SubtractionStrategy>> generateStrategies() {
    // Date strategies
    Map<Class<?>, SubtractionStrategy> typeStrategies = new HashMap<>();
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
    Map<Class<?>, Map<Class<?>, SubtractionStrategy>> strategies = new HashMap<>();
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

  private interface SubtractionStrategy {
    @NonNull
    IAnyAtomicItem subtract(@NonNull IAnyAtomicItem minuend, @NonNull IAnyAtomicItem subtrahend);
  }
}
