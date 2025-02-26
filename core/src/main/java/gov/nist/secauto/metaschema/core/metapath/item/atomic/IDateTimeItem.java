/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateTimeWithoutTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An atomic Metapath item representing a date/time value in the Metapath
 * system.
 * <p>
 * This interface provides functionality for handling date/time values with and
 * without time zone information, supporting parsing, casting, and comparison
 * operations. It works in conjunction with {@link AmbiguousDateTime} to
 * properly handle time zone ambiguity.
 */
public interface IDateTimeItem extends ICalendarTemporalItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IDateTimeItem> type() {
    return MetaschemaDataTypeProvider.DATE_TIME.getItemType();
  }

  @Override
  default IAtomicOrUnionType<? extends IDateTimeItem> getType() {
    return type();
  }

  /**
   * Construct a new date/time item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a date/time
   * @return the new item
   */
  @NonNull
  static IDateTimeItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DATE_TIME.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid date/time value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Get a date/time item based on the provided date and time item values.
   *
   * @param date
   *          the date portion of the date/time
   * @param time
   *          the time portion of the date/time
   * @return the date/time item
   */
  @NonNull
  static IDateTimeItem valueOf(@NonNull IDateItem date, @NonNull ITimeItem time) {
    ZonedDateTime zdtDate = ObjectUtils.notNull(date.asZonedDateTime());
    ZoneId tzDate = date.hasTimezone() ? zdtDate.getZone() : null;
    OffsetTime zdtTime = ObjectUtils.notNull(time.asOffsetTime());
    ZoneId tzTime = time.hasTimezone() ? zdtTime.getOffset() : null;

    if (tzDate != null && tzTime != null && !tzDate.equals(tzTime)) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("The date and time values do not have the same timezone value. date='%s', time='%s'",
              tzDate.toString(),
              tzTime.toString()));
    }

    // either both have the same timezone, both are null, or only one has a timezone
    ZoneId zone = tzDate == null
        ? tzTime == null ? null : tzTime
        : tzDate;

    return valueOf(
        ObjectUtils.notNull(ZonedDateTime.of(
            zdtDate.toLocalDate(),
            zdtTime.toLocalTime(),
            zone == null ? ZoneOffset.UTC : zone)),
        zone != null);
  }

  /**
   * Get the provided item as a date/time item.
   *
   * @param item
   *          the item to convert to a date/time
   * @return the provided value as a date/time
   */
  @NonNull
  static IDateTimeItem valueOf(@NonNull ICalendarTemporalItem item) {
    return item instanceof IDateTimeItem
        ? (IDateTimeItem) item
        : valueOf(item.asZonedDateTime(), item.hasTimezone());
  }

  /**
   * Construct a new date/time item using the provided {@code value}.
   * <p>
   * This method handles recording if an explicit timezone was provided using the
   * {@code hasTimeZone} parameter. The {@link AmbiguousDateTime#hasTimeZone()}
   * method can be called to determine if timezone information is present.
   *
   * @param value
   *          a date/time, without time zone information
   * @param hasTimeZone
   *          {@code true} if the date/time is intended to have an associated time
   *          zone or {@code false} otherwise
   * @return the new item
   * @see AmbiguousDateTime for more details on timezone handling
   */
  @NonNull
  static IDateTimeItem valueOf(@NonNull ZonedDateTime value, boolean hasTimeZone) {
    return hasTimeZone
        ? IDateTimeWithTimeZoneItem.valueOf(value)
        : valueOf(new AmbiguousDateTime(value, false));
  }

  /**
   * Construct a new date/time item using the provided local time {@code value}.
   * <p>
   * The timezone is marked as ambiguous, meaning the
   * {@link AmbiguousDateTime#hasTimeZone()} method will return a result of
   * {@code false}.
   *
   * @param value
   *          the local time value to use
   * @return the new item
   * @see AmbiguousDateTime for more details on timezone handling
   */
  @NonNull
  static IDateTimeItem valueOf(@NonNull LocalDateTime value) {
    return valueOf(new AmbiguousDateTime(ObjectUtils.notNull(value.atZone(ZoneOffset.UTC)), false));
  }

  /**
   * Construct a new date/time item using the provided {@code value}.
   * <p>
   * This method handles recording if an explicit timezone was provided using the
   * {@link AmbiguousDateTime}. The {@link AmbiguousDateTime#hasTimeZone()} method
   * can be called to determine if timezone information is present.
   *
   * @param value
   *          a date/time, without time zone information
   * @return the new item
   * @see AmbiguousDateTime for more details on timezone handling
   */
  @NonNull
  static IDateTimeItem valueOf(@NonNull AmbiguousDateTime value) {
    return value.hasTimeZone()
        ? IDateTimeWithTimeZoneItem.valueOf(value.getValue())
        : new DateTimeWithoutTimeZoneItemImpl(value);
  }

  @Override
  default boolean hasDate() {
    return true;
  }

  @Override
  default boolean hasTime() {
    return true;
  }

  @Override
  default int getYear() {
    return asZonedDateTime().getYear();
  }

  @Override
  default int getMonth() {
    return asZonedDateTime().getMonthValue();
  }

  @Override
  default int getDay() {
    return asZonedDateTime().getDayOfMonth();
  }

  @Override
  default int getHour() {
    return asZonedDateTime().getHour();
  }

  @Override
  default int getMinute() {
    return asZonedDateTime().getMinute();
  }

  @Override
  default int getSecond() {
    return asZonedDateTime().getSecond();
  }

  @Override
  default int getNano() {
    return asZonedDateTime().getNano();
  }

  /**
   * Get the date as a {@link LocalDate}.
   *
   * @return the date
   */
  @NonNull
  default LocalDateTime asLocalDateTime() {
    return ObjectUtils.notNull(asZonedDateTime().toLocalDateTime());
  }

  /**
   * Get the date as a {@link LocalDate}.
   *
   * @return the date
   */
  @NonNull
  default LocalDate asLocalDate() {
    return ObjectUtils.notNull(asZonedDateTime().toLocalDate());
  }

  /**
   * Get the date/time as a {@link LocalTime}.
   *
   * @return the time
   */
  @NonNull
  default LocalTime asLocalTime() {
    return ObjectUtils.notNull(asZonedDateTime().toLocalTime());
  }

  /**
   * Get the date/time as an {@link OffsetTime}.
   *
   * @return the time
   */
  @NonNull
  default OffsetTime asOffsetTime() {
    return ObjectUtils.notNull(asZonedDateTime().toOffsetDateTime().toOffsetTime());
  }

  /**
   * Get the date/time as a date item.
   *
   * @return the date portion of this date/time
   */
  @NonNull
  default IDateItem asDate() {
    return IDateItem.valueOf(asZonedDateTime(), hasTimezone());
  }

  /**
   * Get the date/time as a time item.
   *
   * @return the time portion of this date/time
   */
  @NonNull
  default ITimeItem asTime() {
    return ITimeItem.valueOf(asOffsetTime(), hasTimezone());
  }

  /**
   * Get a date/time that has an explicit timezone.
   * <p>
   * If this date/time has a timezone, then this timezone is used. Otherwise, the
   * implicit timezone is used from the dynamic context to create a new date/time.
   *
   * @param dynamicContext
   *          the dynamic context used to get the implicit timezone
   *
   * @return the date/time with the timezone normalized using UTC-based timezone
   */
  @NonNull
  default IDateTimeItem normalize(@NonNull DynamicContext dynamicContext) {
    IDateTimeItem retval = hasTimezone()
        ? this
        : replaceTimezone(dynamicContext.getImplicitTimeZoneAsDayTimeDuration());
    return valueOf(
        ObjectUtils.notNull(retval.asZonedDateTime().withZoneSameInstant(ZoneOffset.UTC)),
        true);
  }

  /**
   * Get this date/time in the UTC timezone.
   *
   * @return the date/time in UTC
   */
  default IDateTimeItem asDateTimeZ() {
    return ZoneOffset.UTC.equals(getZoneOffset())
        ? this
        : valueOf(ObjectUtils.notNull(asZonedDateTime().withZoneSameLocal(ZoneOffset.UTC)), hasTimezone());
  }

  /**
   * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at
   * all.
   * <p>
   * This method does one of the following things based on the arguments.
   * <ol>
   * <li>If the provided offset is {@code null} and the provided date/time value
   * has a timezone, the timezone is maked absent.
   * <li>If the provided offset is {@code null} and the provided date/time value
   * has an absent timezone, the date/time value is returned.
   * <li>If the provided offset is not {@code null} and the provided date/time
   * value has an absent timezone, the date/time value is returned with the new
   * timezone applied.
   * <li>Otherwise, the provided timezone is applied to the date/time value
   * adjusting the time instant.
   * </ol>
   * <p>
   * Implements the XPath 3.1 <a
   * href="https://www.w3.org/TR/xpath-functions-31/#func-adjust-dateTime-to-timezone>fn:adjust-dateTime-to-timezone</a>
   * function.
   *
   * @param offset
   *          the timezone offset to use or {@code null}
   * @return the adjusted date/time value
   * @throws DateTimeFunctionException
   *           with code
   *           {@link DateTimeFunctionException#INVALID_TIME_ZONE_VALUE_ERROR} if
   *           the offset is < -PT14H or > PT14H
   */
  @Override
  default IDateTimeItem replaceTimezone(@Nullable IDayTimeDurationItem offset) {
    return offset == null
        ? hasTimezone()
            ? valueOf(ObjectUtils.notNull(asZonedDateTime().withZoneSameLocal(ZoneOffset.UTC)), false)
            : this
        : hasTimezone()
            ? valueOf(
                ObjectUtils.notNull(asZonedDateTime().withZoneSameInstant(offset.asZoneOffset())),
                true)
            : valueOf(
                ObjectUtils.notNull(asZonedDateTime().withZoneSameLocal(offset.asZoneOffset())),
                true);
  }

  /**
   * Cast the provided type to this item type.
   *
   * @param item
   *          the item to cast
   * @return the original item if it is already this type, otherwise a new item
   *         cast to this type
   * @throws InvalidValueForCastFunctionException
   *           if the provided {@code item} cannot be cast to this type
   */
  @NonNull
  static IDateTimeItem cast(@NonNull IAnyAtomicItem item) {
    IDateTimeItem retval;
    if (item instanceof IDateTimeItem) {
      retval = (IDateTimeItem) item;
    } else if (item instanceof IDateItem) {
      IDateItem date = (IDateItem) item;
      retval = valueOf(date.asZonedDateTime(), date.hasTimezone());
    } else if (item instanceof IStringItem || item instanceof IUntypedAtomicItem) {
      try {
        retval = valueOf(item.asString());
      } catch (IllegalStateException | InvalidTypeMetapathException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }
    } else {
      throw new InvalidValueForCastFunctionException(
          String.format("unsupported item type '%s'", item.getClass().getName()));
    }
    return retval;
  }

  @Override
  default IDateTimeItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
