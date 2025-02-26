/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousTime;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.TimeWithoutTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An atomic Metapath item representing a time value in the Metapath system.
 * <p>
 * This interface provides functionality for handling time values with and
 * without time zone information, supporting parsing, casting, and comparison
 * operations. It works in conjunction with {@link AmbiguousTime} to properly
 * handle time zone ambiguity.
 */
public interface ITimeItem extends ITemporalItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<ITimeItem> type() {
    return MetaschemaDataTypeProvider.TIME.getItemType();
  }

  @Override
  default IAtomicOrUnionType<? extends ITimeItem> getType() {
    return type();
  }

  /**
   * Construct a new date/time item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a time
   * @return the new item
   */
  @NonNull
  static ITimeItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.TIME.parse(value));
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
   * Construct a new time item using the provided {@code value}.
   * <p>
   * This method handles recording if an explicit timezone was provided using the
   * {@code hasTimeZone} parameter. The {@link AmbiguousTime#hasTimeZone()} method
   * can be called to determine if timezone information is present.
   *
   * @param value
   *          a time, without time zone information
   * @param hasTimeZone
   *          {@code true} if the date/time is intended to have an associated time
   *          zone or {@code false} otherwise
   * @return the new item
   * @see AmbiguousTime for more details on timezone handling
   */
  @NonNull
  static ITimeItem valueOf(@NonNull OffsetTime value, boolean hasTimeZone) {
    return hasTimeZone
        ? ITimeWithTimeZoneItem.valueOf(value)
        : valueOf(new AmbiguousTime(value, false));
  }

  /**
   * Construct a new time item using the provided {@code value}.
   * <p>
   * This method handles recording if an explicit timezone was provided using the
   * {@link AmbiguousTime}. The {@link AmbiguousTime#hasTimeZone()} method can be
   * called to determine if timezone information is present.
   *
   * @param value
   *          a time, without time zone information
   * @return the new item
   * @see AmbiguousTime for more details on timezone handling
   */
  @NonNull
  static ITimeItem valueOf(@NonNull AmbiguousTime value) {
    return value.hasTimeZone()
        ? ITimeWithTimeZoneItem.valueOf(value.getValue())
        : new TimeWithoutTimeZoneItemImpl(value);
  }

  @Override
  default boolean hasDate() {
    return false;
  }

  @Override
  default boolean hasTime() {
    return true;
  }

  @Override
  boolean hasTimezone();

  @Override
  default int getYear() {
    return 0;
  }

  @Override
  default int getMonth() {
    return 0;
  }

  @Override
  default int getDay() {
    return 0;
  }

  @Override
  default int getHour() {
    return asOffsetTime().getHour();
  }

  @Override
  default int getMinute() {
    return asOffsetTime().getMinute();
  }

  @Override
  default int getSecond() {
    return asOffsetTime().getSecond();
  }

  @Override
  default int getNano() {
    return asOffsetTime().getNano();
  }

  @Override
  @Nullable
  default ZoneOffset getZoneOffset() {
    return hasTimezone() ? asOffsetTime().getOffset() : null;
  }

  /**
   * Get the underlying time value.
   *
   * @return the time value
   */
  @NonNull
  OffsetTime asOffsetTime();

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
  default ITimeItem replaceTimezone(@Nullable IDayTimeDurationItem offset) {
    return offset == null
        ? hasTimezone()
            ? valueOf(ObjectUtils.notNull(asOffsetTime().withOffsetSameLocal(ZoneOffset.UTC)), false)
            : this
        : hasTimezone()
            ? valueOf(IDateTimeItem.valueOf(MetapathConstants.REFERENCE_DATE_ITEM, this)
                .replaceTimezone(offset).asOffsetTime(),
                true)
            : valueOf(
                ObjectUtils.notNull(asOffsetTime().withOffsetSameLocal(offset.asZoneOffset())),
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
  static ITimeItem cast(@NonNull IAnyAtomicItem item) {
    ITimeItem retval;
    if (item instanceof ITimeItem) {
      retval = (ITimeItem) item;
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
  default ITimeItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Get the time as a {@link LocalTime}.
   *
   * @return the date
   */
  @NonNull
  default LocalTime asLocalTime() {
    return ObjectUtils.notNull(asOffsetTime().toLocalTime());
  }
}
