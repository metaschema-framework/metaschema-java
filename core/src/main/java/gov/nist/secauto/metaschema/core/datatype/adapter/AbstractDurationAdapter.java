
package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import gov.nist.secauto.metaschema.core.metapath.type.AbstractAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Period;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public abstract class AbstractDurationAdapter<TYPE, ITEM_TYPE extends IDurationItem>
    extends AbstractDataTypeAdapter<TYPE, ITEM_TYPE> {
  private static final Pattern DURATION_PATTERN = Pattern.compile(
      "^(?<sign>-)?"
          + "P"
          + "(?:(?<year>[0-9]+)Y)?"
          + "(?:(?<month>[0-9]+)M)?"
          + "(?:(?<day>[0-9]+)D)?"
          + "(?:T"
          + "(?:(?<hour>[0-9]+)H)?"
          + "(?:(?<minute>[0-9]+)M)?"
          + "(?:(?<second2>[0-9]+(?:\\.[0-9]+)?)S)?"
          + ")?$");

  @NonNull
  public static IDurationItem parseDuration(@NonNull String value) {
    Matcher matcher = DURATION_PATTERN.matcher(value);

    // yearMonth; null is zero months
    Period period = null;
    // dayTime; null is zero seconds
    Duration duration = null;
    if (matcher.matches()) {
      boolean negative = matcher.group(1) != null;

      Period parsedPeriod = parsePeriod(
          negative,
          matcher.group(2),
          matcher.group(3));
      if (!Period.ZERO.equals(parsedPeriod)) {
        period = parsedPeriod;
      }

      Duration parsedDuration = parseDuration(
          negative,
          matcher.group(4),
          matcher.group(5),
          matcher.group(6),
          matcher.group(7));
      if (!Duration.ZERO.equals(parsedDuration)) {
        duration = parsedDuration;
      }
    }

    return validate(value, period, duration);
  }

  @NonNull
  private static IDurationItem validate(
      @NonNull String value,
      @Nullable Period period,
      @Nullable Duration duration) {

    if (period != null && duration != null) {
      throw new IllegalArgumentException(
          String.format("Invalid year/month or day/time duration value '%s'.",
              value));
    }

    IDurationItem retval;
    if (period != null && duration == null) {
      retval = IYearMonthDurationItem.valueOf(period);
    } else if (period == null && duration != null) {
      retval = IDayTimeDurationItem.valueOf(duration);
    } else { // both are null or zero
      retval = IDayTimeDurationItem.valueOf(ObjectUtils.notNull(Duration.ZERO));
    }
    return retval;
  }

  @NonNull
  protected static Period parsePeriod(
      boolean negative,
      @Nullable String yearsFragment,
      @Nullable String monthsFragment) {
    int years = parseIntegerValue(yearsFragment);
    int months = parseIntegerValue(monthsFragment);

    Period period = Period.of(years, months, 0);
    return ObjectUtils.notNull(negative
        // negate for '-' sign
        ? period.negated()
        : period);
  }

  @NonNull
  protected static Duration parseDuration(
      boolean negative,
      @Nullable String daysFragment,
      @Nullable String hoursFragment,
      @Nullable String minutesFragment,
      @Nullable String secondsFragment) {
    int days = parseIntegerValue(daysFragment);
    int hours = parseIntegerValue(hoursFragment);
    int minutes = parseIntegerValue(minutesFragment);

    BigDecimal bigSeconds = parseDecimalValue(secondsFragment);
    long totalSeconds = toWholeSeconds(bigSeconds);

    try {
      totalSeconds = Math.addExact(totalSeconds, Math.multiplyExact(days, 86_400));
    } catch (ArithmeticException ex) {
      throw ex;
    }

    try {
      totalSeconds = Math.addExact(totalSeconds, Math.multiplyExact(hours, 3_600));
    } catch (ArithmeticException ex) {
      throw ex;
    }
    try {
      totalSeconds = Math.addExact(totalSeconds, Math.multiplyExact(minutes, 60));
    } catch (ArithmeticException ex) {
      throw ex;
    }

    Duration duration = Duration.ofSeconds(
        totalSeconds,
        toFractionalNanoSeconds(bigSeconds));
    return ObjectUtils.notNull(negative
        // negate for '-' sign
        ? duration.negated()
        : duration);
  }

  /**
   * Construct a new Java type adapter for a provided class.
   *
   * @param valueClass
   *          the Java value object type this adapter supports
   * @param itemClass
   *          the Java type of the Matepath item this adapter supports
   * @param castExecutor
   *          the method to call to cast an item to an item based on this type
   */
  protected AbstractDurationAdapter(
      @NonNull Class<TYPE> valueClass,
      @NonNull Class<ITEM_TYPE> itemClass,
      @NonNull AbstractAtomicOrUnionType.ICastExecutor<ITEM_TYPE> castExecutor) {
    super(valueClass, itemClass, castExecutor);
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Nullable
  protected static String parseGroup(@NonNull Matcher matcher, @NonNull List<String> fragNames) {
    String frag = null;
    for (String name : fragNames) {
      frag = matcher.group(name);
      if (frag != null) {
        break;
      }
    }
    return frag;
  }

  protected static int parseIntegerValue(@Nullable String value) {
    return value == null ? 0 : Integer.parseInt(value);
  }

  @NonNull
  protected static BigDecimal parseDecimalValue(@Nullable String value) {
    return value == null ? ObjectUtils.notNull(BigDecimal.ZERO) : new BigDecimal(value, DecimalAdapter.MATH_CONTEXT);
  }

  protected static long toWholeSeconds(@NonNull BigDecimal bigSeconds) {
    try {
      return bigSeconds.toBigInteger().longValueExact();
    } catch (ArithmeticException ex) {
      ArithmeticException ex2 = new ArithmeticException(
          String.format("Whole seconds '%s' is out of range for a long.", bigSeconds.toPlainString()));
      ex2.addSuppressed(ex);
      throw ex2;
    }
  }

  protected static long toFractionalNanoSeconds(@NonNull BigDecimal seconds) {
    BigDecimal remainder = seconds.remainder(BigDecimal.ONE, DecimalAdapter.MATH_CONTEXT);

    BigDecimal result = remainder.multiply(new BigDecimal("1e9", DecimalAdapter.MATH_CONTEXT))
        .setScale(0, RoundingMode.HALF_EVEN);
    try {
      return result.longValueExact();
    } catch (ArithmeticException ex) {
      ArithmeticException ex2 = new ArithmeticException(
          String.format("Nano seconds '%s' is out of range for a long.", result.toPlainString()));
      ex2.addSuppressed(ex);
      throw ex2;
    }
  }
}
