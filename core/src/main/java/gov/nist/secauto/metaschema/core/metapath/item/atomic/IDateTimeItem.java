/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;
import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateTimeWithoutTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An atomic Metapath item representing a date/time value in the Metapath system.
 * <p>
 * This interface provides functionality for handling date/time values with and without time zone
 * information, supporting parsing, casting, and comparison operations. It works in conjunction with
 * {@link AmbiguousDateTime} to properly handle time zone ambiguity.
 */
public interface IDateTimeItem extends ITemporalItem {
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
   * Construct a new date/time item using the provided {@code value}.
   * <p>
   * This method handles recording if an explicit timezone was provided using the {@code hasTimeZone}
   * parameter. The {@link AmbiguousDateTime#hasTimeZone()} method can be called to determine if
   * timezone information is present.
   *
   * @param value
   *          a date/time, without time zone information
   * @param hasTimeZone
   *          {@code true} if the date/time is intended to have an associated time zone or
   *          {@code false} otherwise
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
   * Construct a new date/time item using the provided {@code value}.
   * <p>
   * This method handles recording if an explicit timezone was provided using the
   * {@link AmbiguousDateTime}. The {@link AmbiguousDateTime#hasTimeZone()} method can be called to
   * determine if timezone information is present.
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

  /**
   * Adjusts an xs:dateTime value to a specific timezone, or to no timezone at all.
   * <p>
   * This method does one of the following things based on the arguments.
   * <ol>
   * <li>If the provided offset is {@code null} and the provided date/time value has a timezone, the
   * timezone is maked absent.
   * <li>If the provided offset is {@code null} and the provided date/time value has an absent
   * timezone, the date/time value is returned.
   * <li>If the provided offset is not {@code null} and the provided date/time value has an absent
   * timezone, the date/time value is returned with the new timezone applied.
   * <li>Otherwise, the provided timezone is applied to the date/time value adjusting the time
   * instant.
   * </ol>
   * <p>
   * Implements the XPath 3.1 <a
   * href="https://www.w3.org/TR/xpath-functions-31/#func-adjust-dateTime-to-timezone>fn:adjust-dateTime-to-timezone</a>
   * function.
   *
   * @param offset
   *          the timezone offset to use
   * @return the adjusted date/time value
   * @throws DateTimeFunctionException
   *           with code {@link DateTimeFunctionException#INVALID_TIME_ZONE_VALUE_ERROR} if the offset
   *           is < -PT14H or > PT14H
   */
  default IDateTimeItem replaceTimezone(@Nullable IDayTimeDurationItem offset) {
    return offset == null
        ? hasTimezone()
            ? IDateTimeItem.valueOf(ObjectUtils.notNull(asZonedDateTime().withZoneSameLocal(ZoneOffset.UTC)), false)
            : this
        : hasTimezone()
            ? IDateTimeItem.valueOf(
                ObjectUtils.notNull(asZonedDateTime().withZoneSameInstant(offset.asZoneOffset())),
                true)
            : IDateTimeItem.valueOf(
                ObjectUtils.notNull(asZonedDateTime().withZoneSameLocal(offset.asZoneOffset())),
                true);
  }

  /**
   * Cast the provided type to this item type.
   *
   * @param item
   *          the item to cast
   * @return the original item if it is already this type, otherwise a new item cast to this type
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
      // get the time at midnight
      ZonedDateTime zdt = ObjectUtils.notNull(date.asZonedDateTime().truncatedTo(ChronoUnit.DAYS));
      // pass on the timezone ambiguity
      retval = valueOf(zdt, date.hasTimezone());
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

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }
}
