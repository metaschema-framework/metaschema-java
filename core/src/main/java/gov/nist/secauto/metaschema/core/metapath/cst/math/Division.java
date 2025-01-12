/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.impl.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-arithmetic">arithmetic
 * expression</a> supporting division.
 */
public class Division
    extends AbstractBasicArithmeticExpression {
  @NonNull
  private static final Map<Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> DIVISION_STRATEGIES = generateStrategies();

  /**
   * An expression that gets the quotient result by dividing the dividend by the
   * divisor.
   *
   * @param text
   *          the parsed text of the expression
   * @param dividend
   *          the expression whose result is to be divided
   * @param divisor
   *          the expression whose result is to divide by
   */
  public Division(
      @NonNull String text,
      @NonNull IExpression dividend,
      @NonNull IExpression divisor) {
    super(text, dividend, divisor);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitDivision(this, context);
  }

  @Override
  protected Map<
      Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> getStrategies() {
    return DIVISION_STRATEGIES;
  }

  @Override
  protected String unsupportedMessage(String dividend, String divisor) {
    return ObjectUtils.notNull(String.format("Division of '%s' by '%s' is not supported.", dividend, divisor));
  }

  @Override
  protected INumericItem operationAsNumeric(INumericItem dividend, INumericItem divisor) {
    // Default to numeric division
    return OperationFunctions.opNumericDivide(dividend, divisor);
  }

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private static Map<
      Class<? extends IAnyAtomicItem>,
      Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> generateStrategies() {
    Map<Class<? extends IAnyAtomicItem>, Map<Class<? extends IAnyAtomicItem>, OperationStrategy>> strategies
        = new LinkedHashMap<>();

    // IYearMonthDurationItem strategies
    Map<Class<? extends IAnyAtomicItem>, OperationStrategy> typeStrategies = new LinkedHashMap<>();
    typeStrategies.put(INumericItem.class,
        (dividend, divisor, dynamicContext) -> OperationFunctions.opDivideYearMonthDuration(
            (IYearMonthDurationItem) dividend,
            (INumericItem) divisor));
    typeStrategies.put(IYearMonthDurationItem.class,
        (dividend, divisor, dynamicContext) -> OperationFunctions.opDivideYearMonthDurationByYearMonthDuration(
            (IYearMonthDurationItem) dividend,
            (IYearMonthDurationItem) divisor));
    strategies.put(IYearMonthDurationItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // IDayTimeDurationItem strategies
    typeStrategies = new LinkedHashMap<>();
    typeStrategies.put(INumericItem.class,
        (dividend, divisor, dynamicContext) -> OperationFunctions.opDivideDayTimeDuration(
            (IDayTimeDurationItem) dividend,
            (INumericItem) divisor));
    typeStrategies.put(IDayTimeDurationItem.class,
        (dividend, divisor, dynamicContext) -> OperationFunctions.opDivideDayTimeDurationByDayTimeDuration(
            (IDayTimeDurationItem) dividend,
            (IDayTimeDurationItem) divisor));
    strategies.put(IDayTimeDurationItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    // INumericItem strategies
    typeStrategies = new LinkedHashMap<>();
    typeStrategies.put(INumericItem.class,
        (dividend, divisor, dynamicContext) -> OperationFunctions.opNumericDivide(
            (INumericItem) dividend,
            (INumericItem) divisor));
    strategies.put(INumericItem.class, CollectionUtil.unmodifiableMap(typeStrategies));

    return CollectionUtil.unmodifiableMap(strategies);
  }
}
