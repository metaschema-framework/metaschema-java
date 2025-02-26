/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.impl;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.ArithmeticFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.CastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnDateTime;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IUntypedAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Implementations of the XPath 3.1 operation functions.
 */
@SuppressWarnings({
    // FIXME: break out methods into separate classes organized as in XPath
    "PMD.CouplingBetweenObjects",
    "PMD.ExcessivePublicCount",
    "PMD.CyclomaticComplexity"
})
public final class OperationFunctions {
  @NonNull
  public static final IDateItem DATE_1972_12_31 = IDateItem.valueOf(ObjectUtils.notNull(LocalDate.of(1972, 12, 31)));

  /**
   * Identifies the types and substypes that support aggregation.
   */
  @NonNull
  public static final Set<Class<? extends IAnyAtomicItem>> AGGREGATE_MATH_TYPES = ObjectUtils.notNull(Set.of(
      IDayTimeDurationItem.class,
      IYearMonthDurationItem.class,
      INumericItem.class));

  private OperationFunctions() {
    // disable
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-add-yearMonthDuration-to-date">op:add-yearMonthDuration-to-date</a>.
   *
   * @param instant
   *          a point in time
   * @param duration
   *          the duration to add
   * @return the result of adding the duration to the date
   */
  @NonNull
  public static IDateItem opAddYearMonthDurationToDate(@NonNull IDateItem instant,
      @NonNull IYearMonthDurationItem duration) {
    return opAddYearMonthDurationToDateTime(instant.asDateTime(), duration).asDate();
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-add-dayTimeDuration-to-date">op:add-dayTimeDuration-to-date</a>.
   *
   * @param instant
   *          a point in time
   * @param duration
   *          the duration to add
   * @return the result of adding the duration to the date
   */
  @NonNull
  public static IDateItem opAddDayTimeDurationToDate(
      @NonNull IDateItem instant,
      @NonNull IDayTimeDurationItem duration) {
    return opAddDayTimeDurationToDateTime(instant.asDateTime(), duration).asDate();
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-add-yearMonthDurations">op:add-yearMonthDurations</a>.
   *
   * @param arg1
   *          the first duration
   * @param arg2
   *          the second duration
   * @return the sum of two duration values
   */
  @NonNull
  public static IYearMonthDurationItem opAddYearMonthDurations(
      @NonNull IYearMonthDurationItem arg1,
      @NonNull IYearMonthDurationItem arg2) {
    Period duration1 = arg1.asPeriod();
    Period duration2 = arg2.asPeriod();

    Period result;
    try {
      result = duration1.plus(duration2);
    } catch (ArithmeticException ex) {
      throw new DateTimeFunctionException(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    assert result != null;
    return IYearMonthDurationItem.valueOf(result);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-add-dayTimeDurations">op:add-dayTimeDurations</a>.
   *
   * @param arg1
   *          the first duration
   * @param arg2
   *          the second duration
   * @return the sum of two duration values
   */
  @NonNull
  public static IDayTimeDurationItem opAddDayTimeDurations(
      @NonNull IDayTimeDurationItem arg1,
      @NonNull IDayTimeDurationItem arg2) {
    Duration duration1 = arg1.asDuration();
    Duration duration2 = arg2.asDuration();

    Duration result;
    try {
      result = duration1.plus(duration2);
    } catch (ArithmeticException ex) {
      throw new DateTimeFunctionException(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    assert result != null;
    return IDayTimeDurationItem.valueOf(result);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-add-yearMonthDuration-to-dateTime">op:add-yearMonthDuration-to-dateTime</a>.
   *
   * @param instant
   *          a point in time
   * @param duration
   *          the duration to add
   * @return the result of adding the duration to the date
   */
  @NonNull
  public static IDateTimeItem opAddYearMonthDurationToDateTime(
      @NonNull IDateTimeItem instant,
      @NonNull IYearMonthDurationItem duration) {
    ZonedDateTime result;
    try {
      result = instant.asZonedDateTime().plus(duration.asPeriod());
    } catch (ArithmeticException ex) {
      throw new ArithmeticFunctionException(ArithmeticFunctionException.OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    assert result != null;
    // preserve the same timezone "presence"
    return IDateTimeItem.valueOf(result, instant.hasTimezone());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-add-dayTimeDuration-to-dateTime">op:add-dayTimeDuration-to-dateTime</a>.
   *
   * @param instant
   *          a point in time
   * @param duration
   *          the duration to add
   * @return the result of adding the duration to the date
   */
  @NonNull
  public static IDateTimeItem opAddDayTimeDurationToDateTime(
      @NonNull IDateTimeItem instant,
      @NonNull IDayTimeDurationItem duration) {
    ZonedDateTime result;
    try {
      result = instant.asZonedDateTime().plus(duration.asDuration());
    } catch (ArithmeticException ex) {
      throw new ArithmeticFunctionException(ArithmeticFunctionException.OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    assert result != null;
    // preserve the same timezone "presence"
    return IDateTimeItem.valueOf(result, instant.hasTimezone());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-add-dayTimeDuration-to-time">op:add-dayTimeDuration-to-time</a>.
   *
   * @param instant
   *          a point in time
   * @param duration
   *          the duration to add
   * @return the result of adding the duration to the date
   */
  @NonNull
  public static ITimeItem opAddDayTimeDurationToTime(
      @NonNull ITimeItem instant,
      @NonNull IDayTimeDurationItem duration) {
    long seconds = duration.asDuration().toSeconds() % 86_400;
    int nano = duration.asDuration().getNano();

    OffsetTime result;
    try {
      result = instant.asOffsetTime().plus(Duration.ofSeconds(seconds, nano));
    } catch (ArithmeticException ex) {
      throw new ArithmeticFunctionException(ArithmeticFunctionException.OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    assert result != null;
    // preserve the same timezone "presence"
    return ITimeItem.valueOf(result, instant.hasTimezone());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-dates">op:subtract-dates</a>.
   *
   * @param date1
   *          the first point in time
   * @param date2
   *          the second point in time
   * @param dynamicContext
   *          the dynamic context used to provide the implicit timezone
   * @return the elapsed time between the starting instant and ending instant
   */
  @NonNull
  public static IDayTimeDurationItem opSubtractDates(
      @NonNull IDateItem date1,
      @NonNull IDateItem date2,
      @NonNull DynamicContext dynamicContext) {
    return opSubtractDateTimes(
        date1.asDateTime(),
        date2.asDateTime(),
        dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-yearMonthDuration-from-date">op:subtract-yearMonthDuration-from-date</a>.
   *
   * @param date
   *          a point in time
   * @param duration
   *          the duration to subtract
   * @return the result of subtracting the duration from the date
   */
  @NonNull
  public static IDateItem opSubtractYearMonthDurationFromDate(
      @NonNull IDateItem date,
      @NonNull IYearMonthDurationItem duration) {
    return opAddYearMonthDurationToDate(date, duration.negate());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-dayTimeDuration-from-date">op:subtract-dayTimeDuration-from-date</a>.
   *
   * @param date
   *          a point in time
   * @param duration
   *          the duration to subtract
   * @return the result of subtracting the duration from the date
   */
  @NonNull
  public static IDateItem opSubtractDayTimeDurationFromDate(
      @NonNull IDateItem date,
      @NonNull IDayTimeDurationItem duration) {
    return opAddDayTimeDurationToDate(date, duration.negate());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-dayTimeDuration-from-time">op:subtract-dayTimeDuration-from-time</a>.
   *
   * @param time
   *          a point in time
   * @param duration
   *          the duration to subtract
   * @return the result of subtracting the duration from the time
   */
  @NonNull
  public static ITimeItem opSubtractDayTimeDurationFromTime(
      @NonNull ITimeItem time,
      @NonNull IDayTimeDurationItem duration) {
    return opAddDayTimeDurationToTime(time, duration.negate());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-times">op:subtract-times</a>.
   *
   * @param arg1
   *          the first duration
   * @param arg2
   *          the second duration
   * @param dynamicContext
   *          the dynamic context used to provide the implicit timezone
   * @return the result of subtracting the second duration from the first
   */
  @NonNull
  public static IDayTimeDurationItem opSubtractTimes(
      @NonNull ITimeItem arg1,
      @NonNull ITimeItem arg2,
      @NonNull DynamicContext dynamicContext) {
    return opSubtractDateTimes(
        FnDateTime.fnDateTime(DATE_1972_12_31, arg1),
        FnDateTime.fnDateTime(DATE_1972_12_31, arg2),
        dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-yearMonthDurations">op:subtract-yearMonthDurations</a>.
   *
   * @param arg1
   *          the first duration
   * @param arg2
   *          the second duration
   * @return the result of subtracting the second duration from the first
   */
  @NonNull
  public static IYearMonthDurationItem opSubtractYearMonthDurations(
      @NonNull IYearMonthDurationItem arg1,
      @NonNull IYearMonthDurationItem arg2) {
    Period duration1 = arg1.asPeriod();
    Period duration2 = arg2.asPeriod();

    Period result;
    try {
      result = ObjectUtils.notNull(duration1.minus(duration2));
    } catch (ArithmeticException ex) {
      throw new DateTimeFunctionException(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    return IYearMonthDurationItem.valueOf(result);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-dayTimeDurations">op:subtract-dayTimeDurations</a>.
   *
   * @param arg1
   *          the first duration
   * @param arg2
   *          the second duration
   * @return the result of subtracting the second duration from the first
   */
  @NonNull
  public static IDayTimeDurationItem opSubtractDayTimeDurations(
      @NonNull IDayTimeDurationItem arg1,
      @NonNull IDayTimeDurationItem arg2) {
    Duration duration1 = arg1.asDuration();
    Duration duration2 = arg2.asDuration();

    Duration result;
    try {
      result = ObjectUtils.notNull(duration1.minus(duration2));
    } catch (ArithmeticException ex) {
      throw new DateTimeFunctionException(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    return IDayTimeDurationItem.valueOf(result);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-dateTimes">op:subtract-dateTimes</a>.
   *
   * @param instant1
   *          the first point in time
   * @param instant2
   *          the second point in time
   * @param dynamicContext
   *          the dynamic context used to provide the implicit timezone
   * @return the duration the occurred between the two points in time
   */
  @NonNull
  public static IDayTimeDurationItem opSubtractDateTimes(
      @NonNull IDateTimeItem instant1,
      @NonNull IDateTimeItem instant2,
      @NonNull DynamicContext dynamicContext) {
    return between(
        instant2.normalize(dynamicContext).asZonedDateTime(),
        instant1.normalize(dynamicContext).asZonedDateTime());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-dateTimes">op:subtract-dateTimes</a>
   * and <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-times">op:subtract-times</a>.
   *
   * @param time1
   *          the first point in time
   * @param time2
   *          the second point in time
   * @return the duration the occurred between the two points in time
   */
  @NonNull
  private static IDayTimeDurationItem between(@NonNull Temporal time1, @NonNull Temporal time2) {
    return IDayTimeDurationItem.valueOf(ObjectUtils.notNull(Duration.between(time1, time2)));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-yearMonthDuration-from-dateTime">op:subtract-yearMonthDuration-from-dateTime</a>.
   *
   * @param moment
   *          a point in time
   * @param duration
   *          the duration to subtract
   * @return the result of subtracting the duration from a point in time
   */
  @NonNull
  public static IDateTimeItem opSubtractYearMonthDurationFromDateTime(
      @NonNull IDateTimeItem moment,
      @NonNull IYearMonthDurationItem duration) {
    return opAddYearMonthDurationToDateTime(moment, duration.negate());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-subtract-dayTimeDuration-from-dateTime">op:subtract-dayTimeDuration-from-dateTime</a>.
   *
   * @param moment
   *          a point in time
   * @param duration
   *          the duration to subtract
   * @return the result of subtracting the duration from a point in time
   */
  @NonNull
  public static IDateTimeItem opSubtractDayTimeDurationFromDateTime(
      @NonNull IDateTimeItem moment,
      @NonNull IDayTimeDurationItem duration) {
    // preserve the same timezone "presence"
    return opAddDayTimeDurationToDateTime(moment, duration.negate());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-multiply-yearMonthDuration">op:multiply-yearMonthDuration</a>.
   *
   * @param arg1
   *          the duration value
   * @param arg2
   *          the number to multiply by
   * @return the result of multiplying a {@link IYearMonthDurationItem} by a
   *         number
   * @throws DateTimeFunctionException
   *           with the code
   *           {@link DateTimeFunctionException#DURATION_OVERFLOW_UNDERFLOW_ERROR}
   *           if arithmetic overflow occurs
   */
  @NonNull
  public static IYearMonthDurationItem opMultiplyYearMonthDuration(
      @NonNull IYearMonthDurationItem arg1,
      @NonNull INumericItem arg2) {
    IDecimalItem months = IDecimalItem.valueOf(arg1.asMonths());
    INumericItem result = months.multiply(arg2);
    Period period;
    try {
      period = Period.ofMonths(result.round().toIntValueExact());
    } catch (CastFunctionException ex) {
      throw new DateTimeFunctionException(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    return IYearMonthDurationItem.valueOf(ObjectUtils.notNull(period));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-multiply-dayTimeDuration">op:multiply-dayTimeDuration</a>.
   *
   * @param arg1
   *          the duration value
   * @param arg2
   *          the number to multiply by
   * @return the result of multiplying a {@link IDayTimeDurationItem} by a number
   */
  @NonNull
  public static IDayTimeDurationItem opMultiplyDayTimeDuration(
      @NonNull IDayTimeDurationItem arg1,
      @NonNull INumericItem arg2) {
    IDecimalItem seconds = IDecimalItem.valueOf(arg1.asSeconds());
    INumericItem result = seconds.multiply(arg2);
    Duration duration;
    try {
      duration = Duration.ofSeconds(result.round().toIntValueExact());
    } catch (CastFunctionException ex) {
      throw new DateTimeFunctionException(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, ex);
    }
    return IDayTimeDurationItem.valueOf(ObjectUtils.notNull(duration));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-divide-yearMonthDuration">op:divide-yearMonthDuration</a>.
   *
   * @param arg1
   *          the duration value
   * @param arg2
   *          the number to divide by
   * @return the result of dividing a {@link IYearMonthDurationItem} by a number
   */
  @NonNull
  public static IYearMonthDurationItem opDivideYearMonthDuration(
      @NonNull IYearMonthDurationItem arg1,
      @NonNull INumericItem arg2) {
    IDecimalItem months = IDecimalItem.valueOf(arg1.asMonths());
    INumericItem result = months.divide(arg2);

    Period period = Period.ofMonths(result.round().toIntValueExact());
    return IYearMonthDurationItem.valueOf(ObjectUtils.notNull(period));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-divide-yearMonthDuration-by-yearMonthDuration">op:divide-yearMonthDuration-by-yearMonthDuration</a>.
   *
   * @param arg1
   *          the first duration value
   * @param arg2
   *          the second duration value
   * @return the result of dividing a the first duration value by the second
   *         duration value
   */
  @NonNull
  public static IDecimalItem opDivideYearMonthDurationByYearMonthDuration(
      @NonNull IYearMonthDurationItem arg1,
      @NonNull IYearMonthDurationItem arg2) {
    IIntegerItem totalMonths1 = IIntegerItem.valueOf(arg1.asMonths());
    IIntegerItem totalMonths2 = IIntegerItem.valueOf(arg2.asMonths());

    return totalMonths1.divide(totalMonths2);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-divide-dayTimeDuration">op:divide-dayTimeDuration</a>.
   *
   * @param arg1
   *          the duration value
   * @param arg2
   *          the number to divide by
   * @return the result of dividing a {@link IDayTimeDurationItem} by a number
   */
  @NonNull
  public static IDayTimeDurationItem opDivideDayTimeDuration(
      @NonNull IDayTimeDurationItem arg1,
      @NonNull INumericItem arg2) {
    IDecimalItem seconds = IDecimalItem.valueOf(arg1.asSeconds());
    INumericItem result = seconds.divide(arg2);

    Duration duration = Duration.ofSeconds(result.round().toIntValueExact());
    return IDayTimeDurationItem.valueOf(ObjectUtils.notNull(duration));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-divide-dayTimeDuration-by-dayTimeDuration">op:divide-dayTimeDuration-by-dayTimeDuration</a>.
   *
   * @param arg1
   *          the first duration value
   * @param arg2
   *          the second duration value
   * @return the ratio of two {@link IDayTimeDurationItem} values, as a decimal
   *         number
   */
  @NonNull
  public static IDecimalItem opDivideDayTimeDurationByDayTimeDuration(
      @NonNull IDayTimeDurationItem arg1,
      @NonNull IDayTimeDurationItem arg2) {
    return IDecimalItem.cast(
        opNumericDivide(
            IDecimalItem.valueOf(arg1.asSeconds()),
            IDecimalItem.valueOf(arg2.asSeconds())));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-date-equal">op:date-equal</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is the same instant in time as the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDateEqual(
      @NonNull IDateItem arg1,
      @NonNull IDateItem arg2,
      @NonNull DynamicContext dynamicContext) {
    return opDateTimeEqual(arg1.asDateTime(), arg2.asDateTime(), dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-dateTime-equal">op:dateTime-equal</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is the same instant in time as the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDateTimeEqual(
      @NonNull IDateTimeItem arg1,
      @NonNull IDateTimeItem arg2,
      @NonNull DynamicContext dynamicContext) {
    IDateTimeItem arg1Normalized = arg1.normalize(dynamicContext);
    IDateTimeItem arg2Normalized = arg2.normalize(dynamicContext);
    return IBooleanItem.valueOf(arg1Normalized.asZonedDateTime().isEqual(arg2Normalized.asZonedDateTime()));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-time-equal">op:time-equal</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is the same instant in time as the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opTimeEqual(
      @NonNull ITimeItem arg1,
      @NonNull ITimeItem arg2,
      @NonNull DynamicContext dynamicContext) {
    IDateTimeItem time1 = IDateTimeItem.valueOf(DATE_1972_12_31, arg1);
    IDateTimeItem time2 = IDateTimeItem.valueOf(DATE_1972_12_31, arg2);
    return opDateTimeEqual(time1, time2, dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-duration-equal">op:duration-equal</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @return {@code true} if the first argument is the same duration as the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDurationEqual(@NonNull IDurationItem arg1, @NonNull IDurationItem arg2) {
    return IBooleanItem.valueOf(arg1.compareTo(arg2) == 0);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-base64Binary-equal">op:base64Binary-equal</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @return {@code true} if the first argument is equal to the second, or
   *         {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opBase64BinaryEqual(@NonNull IBase64BinaryItem arg1, @NonNull IBase64BinaryItem arg2) {
    return IBooleanItem.valueOf(arg1.asByteBuffer().equals(arg2.asByteBuffer()));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-date-greater-than">op:date-greater-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is a later instant in time than
   *         the second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDateGreaterThan(
      @NonNull IDateItem arg1,
      @NonNull IDateItem arg2,
      @NonNull DynamicContext dynamicContext) {
    return opDateLessThan(arg2, arg1, dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-dateTime-greater-than">op:dateTime-greater-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is a later instant in time than
   *         the second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDateTimeGreaterThan(
      @NonNull IDateTimeItem arg1,
      @NonNull IDateTimeItem arg2,
      @NonNull DynamicContext dynamicContext) {
    return opDateTimeLessThan(arg2, arg1, dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-time-greater-than">op:time-greater-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is a later instant in time than
   *         the second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opTimeGreaterThan(
      @NonNull ITimeItem arg1,
      @NonNull ITimeItem arg2,
      @NonNull DynamicContext dynamicContext) {
    return opTimeLessThan(arg2, arg1, dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-yearMonthDuration-greater-than">op:yearMonthDuration-greater-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @return {@code true} if the first argument is a longer duration than the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opYearMonthDurationGreaterThan(
      @NonNull IYearMonthDurationItem arg1,
      @NonNull IYearMonthDurationItem arg2) {
    return opYearMonthDurationLessThan(arg2, arg1);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-dayTimeDuration-greater-than">op:dayTimeDuration-greater-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @return {@code true} if the first argument is a longer duration than the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDayTimeDurationGreaterThan(
      @NonNull IDayTimeDurationItem arg1,
      @NonNull IDayTimeDurationItem arg2) {
    return opDayTimeDurationLessThan(arg2, arg1);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-base64Binary-greater-than">op:base64Binary-greater-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @return {@code true} if the first argument is greater than the second, or
   *         {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opBase64BinaryGreaterThan(
      @NonNull IBase64BinaryItem arg1,
      @NonNull IBase64BinaryItem arg2) {
    return opBase64BinaryLessThan(arg2, arg1);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-date-less-than">op:date-less-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is an earlier instant in time than
   *         the second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDateLessThan(
      @NonNull IDateItem arg1,
      @NonNull IDateItem arg2,
      @NonNull DynamicContext dynamicContext) {
    return opDateTimeLessThan(arg1.asDateTime(), arg2.asDateTime(), dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-dateTime-less-than">op:dateTime-less-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is an earlier instant in time than
   *         the second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opTimeLessThan(
      @NonNull ITimeItem arg1,
      @NonNull ITimeItem arg2,
      @NonNull DynamicContext dynamicContext) {
    return opDateTimeLessThan(
        IDateTimeItem.valueOf(DATE_1972_12_31, arg1),
        IDateTimeItem.valueOf(DATE_1972_12_31, arg2),
        dynamicContext);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-time-less-than">op:time-less-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the first argument is an earlier instant in time than
   *         the second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDateTimeLessThan(
      @NonNull IDateTimeItem arg1,
      @NonNull IDateTimeItem arg2,
      @NonNull DynamicContext dynamicContext) {
    IDateTimeItem arg1Normalized = arg1.normalize(dynamicContext);
    IDateTimeItem arg2Normalized = arg2.normalize(dynamicContext);
    return IBooleanItem.valueOf(arg1Normalized.asZonedDateTime().isBefore(arg2Normalized.asZonedDateTime()));
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-yearMonthDuration-less-than">op:yearMonthDuration-less-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @return {@code true} if the first argument is a shorter duration than the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opYearMonthDurationLessThan(
      @NonNull IYearMonthDurationItem arg1,
      @NonNull IYearMonthDurationItem arg2) {
    return IBooleanItem.valueOf(arg1.asMonths() < arg2.asMonths());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-dayTimeDuration-less-than">op:dayTimeDuration-less-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @return {@code true} if the first argument is a shorter duration than the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opDayTimeDurationLessThan(
      @NonNull IDayTimeDurationItem arg1,
      @NonNull IDayTimeDurationItem arg2) {
    return IBooleanItem.valueOf(arg1.asSeconds() < arg2.asSeconds());
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-base64Binary-less-than">op:base64Binary-less-than</a>.
   *
   * @param arg1
   *          the first value
   * @param arg2
   *          the second value
   * @return {@code true} if the first argument is less than the second, or
   *         {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opBase64BinaryLessThan(
      @NonNull IBase64BinaryItem arg1,
      @NonNull IBase64BinaryItem arg2) {
    return IBooleanItem.valueOf(arg1.compareTo(arg2) < 0);
  }

  @NonNull
  private static <R extends INumericItem> R performArithmeticOperation(
      @NonNull INumericItem left,
      @NonNull INumericItem right,
      @NonNull ArithmeticOperation<IIntegerItem, IIntegerItem, R> integerOp,
      @NonNull ArithmeticOperation<IDecimalItem, IDecimalItem, R> decimalOp) {
    R retval;
    if (left instanceof IIntegerItem && right instanceof IIntegerItem) {
      retval = integerOp.apply((IIntegerItem) left, (IIntegerItem) right);
    } else {
      retval = decimalOp.apply((IDecimalItem) left, (IDecimalItem) right);
    }
    return retval;
  }

  /**
   * Represents an arithmetic operation performed using two arguments.
   *
   * @param <T>
   *          the Java type of the first argument to the function
   * @param <U>
   *          the Java type of the second argument to the function
   * @param <R>
   *          the Java type of the result of the function
   */
  @FunctionalInterface
  private interface ArithmeticOperation<T, U, R> {
    /**
     * Perform the operation.
     *
     * @param left
     *          the left side of the operation
     * @param right
     *          the right side of the operation
     * @return the operation result
     */
    @NonNull
    R apply(@NonNull T left, @NonNull U right);
  }

  /**
   * Create a new sum by adding first provided addend value to the second provided
   * addend value.
   * <p>
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-add">op:numeric-add</a>.
   *
   * @param addend1
   *          the first addend
   * @param addend2
   *          the second addend
   * @return the result of adding the second number to the first number
   */
  @NonNull
  public static INumericItem opNumericAdd(@NonNull INumericItem addend1, @NonNull INumericItem addend2) {
    return performArithmeticOperation(
        addend1,
        addend2,
        IIntegerItem::add,
        IDecimalItem::add);
  }

  /**
   * Determine the difference by subtracting the provided subtrahend value from
   * the provided minuend value.
   * <p>
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-subtract">op:numeric-subtract</a>.
   *
   * @param minuend
   *          the value to subtract from
   * @param subtrahend
   *          the value to subtract
   * @return a new value resulting from subtracting the subtrahend from the
   *         minuend
   */
  @NonNull
  public static INumericItem opNumericSubtract(
      @NonNull INumericItem minuend,
      @NonNull INumericItem subtrahend) {
    return performArithmeticOperation(
        minuend,
        subtrahend,
        IIntegerItem::subtract,
        IDecimalItem::subtract);
  }

  /**
   * Multiply the provided multiplicand value by the provided multiplier value.
   * <p>
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-multiply">op:numeric-multiply</a>.
   *
   * @param multiplicand
   *          the value to multiply
   * @param multiplier
   *          the value to multiply by
   * @return a new value resulting from multiplying the multiplicand by the
   *         multiplier
   */
  @NonNull
  public static INumericItem opNumericMultiply(
      @NonNull INumericItem multiplicand,
      @NonNull INumericItem multiplier) {
    return performArithmeticOperation(
        multiplicand,
        multiplier,
        IIntegerItem::multiply,
        IDecimalItem::multiply);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-divide">op:numeric-divide</a>.
   *
   * @param dividend
   *          the number to be divided
   * @param divisor
   *          the number to divide by
   * @return the quotient
   * @throws ArithmeticFunctionException
   *           with the code {@link ArithmeticFunctionException#DIVISION_BY_ZERO}
   *           if the divisor is zero
   */
  @NonNull
  public static IDecimalItem opNumericDivide(@NonNull INumericItem dividend, @NonNull INumericItem divisor) {
    return ((IDecimalItem) dividend).divide((IDecimalItem) divisor);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-integer-divide">op:numeric-integer-divide</a>.
   *
   * @param dividend
   *          the number to be divided
   * @param divisor
   *          the number to divide by
   * @return the quotient
   */
  @NonNull
  public static IIntegerItem opNumericIntegerDivide(
      @NonNull INumericItem dividend,
      @NonNull INumericItem divisor) {
    return performArithmeticOperation(
        dividend,
        divisor,
        IIntegerItem::integerDivide,
        IDecimalItem::integerDivide);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-mod">op:numeric-mod</a>.
   *
   * @param dividend
   *          the number to be divided
   * @param divisor
   *          the number to divide by
   * @return the remainder
   */
  @NonNull
  public static INumericItem opNumericMod(@NonNull INumericItem dividend, @NonNull INumericItem divisor) {
    return performArithmeticOperation(
        dividend,
        divisor,
        IIntegerItem::mod,
        IDecimalItem::mod);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-unary-minus">op:numeric-unary-minus</a>.
   *
   * @param item
   *          the number whose sign is to be reversed
   * @return the number with a reversed sign
   */
  @NonNull
  public static INumericItem opNumericUnaryMinus(@NonNull INumericItem item) {
    INumericItem retval;
    if (item instanceof IIntegerItem) {
      retval = ((IIntegerItem) item).negate();
    } else if (item instanceof IDecimalItem) {
      retval = ((IDecimalItem) item).negate();
    } else {
      throw new InvalidTypeMetapathException(item, String.format("Unsupported numeric type '%s'.", item.getClass()));
    }
    return retval;
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-equal">op:numeric-equal</a>.
   *
   * @param arg1
   *          the first number to check for equality
   * @param arg2
   *          the second number to check for equality
   * @return {@code true} if the numbers are numerically equal or {@code false}
   *         otherwise
   */
  @NonNull
  public static IBooleanItem opNumericEqual(@Nullable INumericItem arg1, @Nullable INumericItem arg2) {
    IBooleanItem retval;
    if (arg1 == null || arg2 == null) {
      retval = IBooleanItem.FALSE;
    } else if (arg1 instanceof IIntegerItem && arg2 instanceof IIntegerItem) {
      retval = IBooleanItem.valueOf(arg1.asInteger().equals(arg2.asInteger()));
    } else {
      retval = IBooleanItem.valueOf(arg1.asDecimal().compareTo(arg2.asDecimal()) == 0);
    }
    return retval;
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-greater-than">op:numeric-greater-than</a>.
   *
   * @param arg1
   *          the first number to check
   * @param arg2
   *          the second number to check
   * @return {@code true} if the first number is greater than or equal to the
   *         second, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opNumericGreaterThan(@Nullable INumericItem arg1, @Nullable INumericItem arg2) {
    return opNumericLessThan(arg2, arg1);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-numeric-less-than">op:numeric-less-than</a>.
   *
   * @param arg1
   *          the first number to check
   * @param arg2
   *          the second number to check
   * @return {@code true} if the first number is less than or equal to the second,
   *         or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opNumericLessThan(@Nullable INumericItem arg1, @Nullable INumericItem arg2) {
    IBooleanItem retval;
    if (arg1 == null || arg2 == null) {
      retval = IBooleanItem.FALSE;
    } else if (arg1 instanceof IIntegerItem && arg2 instanceof IIntegerItem) {
      int result = arg1.asInteger().compareTo(arg2.asInteger());
      retval = IBooleanItem.valueOf(result < 0);
    } else {
      int result = arg1.asDecimal().compareTo(arg2.asDecimal());
      retval = IBooleanItem.valueOf(result < 0);
    }
    return retval;
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-boolean-equal">op:boolean-equal</a>.
   *
   * @param arg1
   *          the first boolean to check
   * @param arg2
   *          the second boolean to check
   * @return {@code true} if the first boolean is equal to the second, or
   *         {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opBooleanEqual(@Nullable IBooleanItem arg1, @Nullable IBooleanItem arg2) {
    boolean left = arg1 != null && arg1.toBoolean();
    boolean right = arg2 != null && arg2.toBoolean();

    return IBooleanItem.valueOf(left == right);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-boolean-greater-than">op:boolean-greater-than</a>.
   *
   * @param arg1
   *          the first boolean to check
   * @param arg2
   *          the second boolean to check
   * @return {@code true} if the first argument is {@link IBooleanItem#TRUE} and
   *         the second is {@link IBooleanItem#FALSE}, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opBooleanGreaterThan(@Nullable IBooleanItem arg1, @Nullable IBooleanItem arg2) {
    return opBooleanLessThan(arg2, arg1);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-boolean-less-than">op:boolean-less-than</a>.
   *
   * @param arg1
   *          the first boolean to check
   * @param arg2
   *          the second boolean to check
   * @return {@code true} if the first argument is {@link IBooleanItem#FALSE} and
   *         the second is {@link IBooleanItem#TRUE}, or {@code false} otherwise
   */
  @NonNull
  public static IBooleanItem opBooleanLessThan(@Nullable IBooleanItem arg1, @Nullable IBooleanItem arg2) {
    boolean left = arg1 != null && arg1.toBoolean();
    boolean right = arg2 != null && arg2.toBoolean();

    return IBooleanItem.valueOf(!left && right);
  }

  /**
   * Based on XPath 3.1 <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-same-key">op:same-key</a>.
   *
   * @param k1
   *          the first key to compare
   * @param k2
   *          the second key to compare
   * @param dynamicContext
   *          used to get the implicit timezone from the evaluation context
   * @return {@code true} if the compared keys are the same, or {@code false}
   *         otherwise
   */
  public static boolean opSameKey(
      @NonNull IAnyAtomicItem k1,
      @NonNull IAnyAtomicItem k2,
      @NonNull DynamicContext dynamicContext) {
    boolean retval;
    if ((k1 instanceof IStringItem || k1 instanceof IAnyUriItem || k1 instanceof IUntypedAtomicItem)
        && (k2 instanceof IStringItem || k2 instanceof IAnyUriItem || k2 instanceof IUntypedAtomicItem)) {
      retval = k1.asString().equals(k2.asString());
    } else if (k1 instanceof IDecimalItem && k2 instanceof IDecimalItem) {
      retval = ((IDecimalItem) k1).asDecimal().equals(((IDecimalItem) k2).asDecimal());
    } else {
      retval = k1.deepEquals(k2, dynamicContext);
    }
    return retval;
  }
}
