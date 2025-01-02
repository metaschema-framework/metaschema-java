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

import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements the '+' operator for Metapath addition operations.
 * <p>
 * An XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-arithmetic">arithmetic
 * expression</a> supporting addition.
 * <p>
 * Supports addition operations between:
 * <ul>
 * <li>Numeric values
 * <li>Date/DateTime + {@link IYearMonthDurationItem}
 * <li>Date/DateTime/Time + {@link IDayTimeDurationItem}
 * <li>Date/Time arithmetic (adding durations to dates/times)</li>
 * <li>{@link IYearMonthDurationItem} + {@link IYearMonthDurationItem}
 * <li>{@link IDayTimeDurationItem} + {@link IDayTimeDurationItem}
 * </ul>
 * <p>
 * Example Metapath usage:
 *
 * <pre>
 * // Numeric addition
 * 1 + 2 → 3
 * // Date/Time arithmetic
 * date + yearMonthDuration
 * dateTime + dayTimeDuration
 * </pre>
 */
public class Addition
    extends AbstractBasicArithmeticExpression {
  @NonNull
  private static final Map<Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> ADDITION_STRATEGIES = generateStrategies();

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

  @Override
  protected Map<
      Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> getStrategies() {
    return ADDITION_STRATEGIES;
  }

  @Override
  protected String unsupportedMessage(String left, String right) {
    return ObjectUtils.notNull(String.format("Addition of '%s' and '%s' is not supported.", left, right));
  }

  @Override
  protected INumericItem operationAsNumeric(INumericItem left, INumericItem right) {
    // Default to numeric addition
    return OperationFunctions.opNumericAdd(left, right);
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private static Map<
      Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> generateStrategies() {
    Map<Class<? extends IAnyAtomicItem>, Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> strategies
        = new LinkedHashMap<>();

    // Date strategies
    Map<Class<? extends IAnyAtomicItem>, OperationStrategy> typeStrategies = new LinkedHashMap<>();
    typeStrategies.put(IYearMonthDurationItem.class,
        (left, right) -> OperationFunctions.opAddYearMonthDurationToDate(
            (IDateItem) left,
            (IYearMonthDurationItem) right));
    typeStrategies.put(IDayTimeDurationItem.class,
        (left, right) -> OperationFunctions.opAddDayTimeDurationToDate(
            (IDateItem) left,
            (IDayTimeDurationItem) right));
    strategies.put(IDateItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // DateTime strategies
    typeStrategies = new LinkedHashMap<>();
    typeStrategies.put(IYearMonthDurationItem.class,
        (left, right) -> OperationFunctions.opAddYearMonthDurationToDateTime(
            (IDateTimeItem) left,
            (IYearMonthDurationItem) right));
    typeStrategies.put(IDayTimeDurationItem.class,
        (left, right) -> OperationFunctions.opAddDayTimeDurationToDateTime(
            (IDateTimeItem) left,
            (IDayTimeDurationItem) right));
    strategies.put(IDateTimeItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // time strategies
    typeStrategies = new LinkedHashMap<>();
    typeStrategies.put(IDayTimeDurationItem.class,
        (left, right) -> OperationFunctions.opAddDayTimeDurationToTime(
            (ITimeItem) left,
            (IDayTimeDurationItem) right));
    strategies.put(ITimeItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // YearMonthDuration strategies
    typeStrategies = new LinkedHashMap<>();
    typeStrategies.put(IDateItem.class,
        (left, right) -> OperationFunctions.opAddYearMonthDurationToDate(
            (IDateItem) right,
            (IYearMonthDurationItem) left));
    typeStrategies.put(IDateTimeItem.class,
        (left, right) -> OperationFunctions.opAddYearMonthDurationToDateTime(
            (IDateTimeItem) right,
            (IYearMonthDurationItem) left));
    typeStrategies.put(IYearMonthDurationItem.class,
        (left, right) -> OperationFunctions.opAddYearMonthDurations(
            (IYearMonthDurationItem) left,
            (IYearMonthDurationItem) right));
    strategies.put(IYearMonthDurationItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // DayTimeDuration strategies
    typeStrategies = new LinkedHashMap<>();
    typeStrategies.put(IDateItem.class,
        (left, right) -> OperationFunctions.opAddDayTimeDurationToDate(
            (IDateItem) right,
            (IDayTimeDurationItem) left));
    typeStrategies.put(IDateTimeItem.class,
        (left, right) -> OperationFunctions.opAddDayTimeDurationToDateTime(
            (IDateTimeItem) right,
            (IDayTimeDurationItem) left));
    typeStrategies.put(ITimeItem.class,
        (left, right) -> OperationFunctions.opAddDayTimeDurationToTime(
            (ITimeItem) right,
            (IDayTimeDurationItem) left));
    typeStrategies.put(IDayTimeDurationItem.class,
        (left, right) -> OperationFunctions.opAddDayTimeDurations(
            (IDayTimeDurationItem) left,
            (IDayTimeDurationItem) right));
    strategies.put(IDayTimeDurationItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    return CollectionUtil.unmodifiableMap(strategies);
  }
}
