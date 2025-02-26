/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDate;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;
import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateWithoutTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An atomic Metapath item representing a date value in the Metapath system with
 * or without an explicit time zone.
 * <p>
 * This interface provides functionality for handling date/time values with or
 * without time zone information, supporting parsing, casting, and comparison
 * operations. It works in conjunction with {@link ZonedDateTime} to handle time
 * zone ambiguity.
 */
public interface IDateItem extends ICalendarTemporalItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IDateItem> type() {
    return MetaschemaDataTypeProvider.DATE.getItemType();
  }

  @Override
  default IAtomicOrUnionType<? extends IDateItem> getType() {
    return type();
  }

  /**
   * Construct a new date item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a date
   * @return the new item
   */
  @NonNull
  static IDateItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DATE.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid date value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Construct a new date item using the provided {@code value}.
   * <p>
   * This method handles recording if an explicit timezone was provided using the
   * {@code hasTimeZone} parameter. The {@link AmbiguousDate#hasTimeZone()} method
   * can be called to determine if timezone information is present.
   *
   * @param value
   *          a date, without time zone information
   * @param hasTimeZone
   *          {@code true} if the date/time is intended to have an associated time
   *          zone or {@code false} otherwise
   * @return the new item
   * @see AmbiguousDateTime for more details on timezone handling
   */
  @NonNull
  static IDateItem valueOf(@NonNull ZonedDateTime value, boolean hasTimeZone) {
    ZonedDateTime truncated = ObjectUtils.notNull(value.truncatedTo(ChronoUnit.DAYS));
    return hasTimeZone
        ? IDateWithTimeZoneItem.valueOf(truncated)
        : valueOf(new AmbiguousDate(truncated, false));
  }

  /**
   * Construct a new date item using the provided {@code value}.
   * <p>
   * This method handles recording that the timezone is implicit.
   *
   * @param value
   *          a date, without time zone information
   * @return the new item
   * @see AmbiguousDateTime for more details on timezone handling
   */
  @NonNull
  static IDateItem valueOf(@NonNull LocalDate value) {
    return valueOf(ObjectUtils.notNull(value.atStartOfDay(ZoneOffset.UTC)), false);
  }

  /**
   * Construct a new date item using the provided {@code value}.
   *
   * @param value
   *          an ambiguous date with time zone information already identified
   * @return the new item
   */
  @NonNull
  static IDateItem valueOf(@NonNull AmbiguousDate value) {
    return value.hasTimeZone()
        ? IDateWithTimeZoneItem.valueOf(value.getValue())
        : new DateWithoutTimeZoneItemImpl(value);
  }

  @Override
  default boolean hasDate() {
    return true;
  }

  @Override
  default boolean hasTime() {
    return false;
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
    return 0;
  }

  @Override
  default int getMinute() {
    return 0;
  }

  @Override
  default int getSecond() {
    return 0;
  }

  @Override
  default int getNano() {
    return 0;
  }

  /**
   * Get this date as a date/time value at the start of the day.
   *
   * @return the date time value
   */
  @NonNull
  default IDateTimeItem asDateTime() {
    return IDateTimeItem.valueOf(this);
  }

  @Override
  @NonNull
  ZonedDateTime asZonedDateTime();

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
  default IDateItem replaceTimezone(@Nullable IDayTimeDurationItem offset) {
    return offset == null
        ? hasTimezone()
            ? valueOf(ObjectUtils.notNull(asZonedDateTime().withZoneSameLocal(ZoneOffset.UTC)), false)
            : this
        : hasTimezone()
            ? valueOf(IDateTimeItem.valueOf(this).replaceTimezone(offset).asZonedDateTime(),
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
  static IDateItem cast(@NonNull IAnyAtomicItem item) {
    IDateItem retval;
    if (item instanceof IDateItem) {
      retval = (IDateItem) item;
    } else if (item instanceof IDateTimeItem) {
      retval = ((IDateTimeItem) item).asDate();
    } else if (item instanceof IStringItem || item instanceof IUntypedAtomicItem) {
      try {
        retval = valueOf(item.asString());
      } catch (IllegalStateException | InvalidTypeMetapathException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }
    } else {
      throw new InvalidValueForCastFunctionException(
          String.format("Unsupported item type '%s'", item.getClass().getName()));
    }
    return retval;
  }

  @Override
  default IDateItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
