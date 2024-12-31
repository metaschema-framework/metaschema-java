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
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-arithmetic">arithmetic
 * expression</a> supporting addition.
 * <p>
 * Implements the '+' operator for XPath arithmetic operations. Supports:
 * <ul>
 * <li>Numeric addition (numbers)</li>
 * <li>Date/Time arithmetic (adding durations to dates/times)</li>
 * <li>Duration arithmetic (adding durations)</li>
 * </ul>
 *
 * <p>
 * Example usage:
 *
 * <pre>
 * // Numeric addition
 * numericValue1 + numericValue2
 *
 * // Date/Time arithmetic
 * date + yearMonthDuration
 * dateTime + dayTimeDuration
 * </pre>
 */
public class Addition
    extends AbstractBasicArithmeticExpression {
  @NonNull
  private static final Map<Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, AdditionStrategy>> ADDITION_STRATEGIES = generateStrategies();

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
   * @param left
   *          the first item to sum
   * @param right
   *          the second item to sum
   * @return the sum of both items
   */
  @SuppressWarnings("PMD.OnlyOneReturn")
  @NonNull
  public static IAnyAtomicItem sum(
      @NonNull IAnyAtomicItem left,
      @NonNull IAnyAtomicItem right) {
    Class<? extends IAnyAtomicItem> leftClass = left.getClass();
    return ObjectUtils.notNull(
        // lookup the strategy options using the left's type
        ADDITION_STRATEGIES.entrySet().stream()
            // filter strategy options that do not match the left's item type
            .filter(entry -> entry.getKey().isAssignableFrom(leftClass))
            // get the first left match
            .findFirst()
            // handle the first left match
            .map(strategies -> {
              Class<? extends IAnyAtomicItem> rightClass = right.getClass();
              // lookup the strategy options using the right's type
              return strategies.getValue().entrySet().stream()
                  // filter strategy options that do not match the rights's item type
                  .filter(entry -> entry.getKey().isAssignableFrom(rightClass))
                  // use the first right match
                  .findFirst()
                  // process the first left match
                  .map(strategy -> strategy.getValue().add(left, right))
                  // or throw if no right match
                  .orElseThrow(() -> new UnsupportedOperationException(
                      String.format("Addition of '%s' and '%s' is not supported.",
                          left.toSignature(),
                          right.toSignature())));
            })
            // if no left match, fallback to numeric add
            .orElseGet(() -> numericAdd(left, right)));
  }

  private static IAnyAtomicItem numericAdd(
      @NonNull IAnyAtomicItem left,
      @NonNull IAnyAtomicItem right) {
    // Default to numeric addition
    return OperationFunctions.opNumericAdd(
        FunctionUtils.toNumeric(left),
        FunctionUtils.toNumeric(right));
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private static Map<Class<? extends IAnyAtomicItem>, Map<Class<? extends IAnyAtomicItem>, AdditionStrategy>>
      generateStrategies() {
    Map<Class<? extends IAnyAtomicItem>, Map<Class<? extends IAnyAtomicItem>, AdditionStrategy>> strategies
        = new LinkedHashMap<>();

    // Date strategies
    Map<Class<? extends IAnyAtomicItem>, AdditionStrategy> typeStrategies = new LinkedHashMap<>();
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

  private interface AdditionStrategy {
    @NonNull
    IAnyAtomicItem add(@NonNull IAnyAtomicItem left, @NonNull IAnyAtomicItem right);
  }
}
