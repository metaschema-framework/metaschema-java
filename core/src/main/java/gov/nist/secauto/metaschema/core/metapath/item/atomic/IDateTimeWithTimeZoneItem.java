/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateTimeWithTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item representing a date/time value in the Metapath system
 * with an explicit time zone.
 * <p>
 * This interface provides functionality for handling date/time values with time
 * zone information, supporting parsing, casting, and comparison operations. It
 * works in conjunction with {@link ZonedDateTime} to eliminate time zone
 * ambiguity.
 */
public interface IDateTimeWithTimeZoneItem extends IDateTimeItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IDateTimeWithTimeZoneItem> type() {
    return MetaschemaDataTypeProvider.DATE_TIME_WITH_TZ.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IDateTimeWithTimeZoneItem> getType() {
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
  static IDateTimeWithTimeZoneItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DATE_TIME_WITH_TZ.parse(value));
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
   * This method handles dates with explicit timezone information using
   * ZonedDateTime. The timezone is preserved as specified in the input and is
   * significant for date/time operations and comparisons.
   *
   * @param value
   *          a date/time, with time zone information
   * @return the new item
   */
  @NonNull
  static IDateTimeWithTimeZoneItem valueOf(@NonNull ZonedDateTime value) {
    return new DateTimeWithTimeZoneItemImpl(value);
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
  static IDateTimeWithTimeZoneItem cast(@NonNull IAnyAtomicItem item) {
    IDateTimeWithTimeZoneItem retval;
    if (item instanceof IDateTimeWithTimeZoneItem) {
      retval = (IDateTimeWithTimeZoneItem) item;
    } else if (item instanceof ITemporalItem) {
      retval = fromTemporal((ITemporalItem) item);
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

  @NonNull
  private static IDateTimeWithTimeZoneItem fromTemporal(@NonNull ITemporalItem temporal) {
    if (!temporal.hasDate()) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(
          String.format("Unable to cast the temporal value '%s', since it lacks date information.",
              temporal.asString()));
    }
    if (!temporal.hasTimezone()) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(
          String.format("Unable to cast the temporal value '%s', since it lacks timezone information.",
              temporal.asString()));
    }

    // get the time at midnight
    return valueOf(ObjectUtils.notNull(ZonedDateTime.of(
        temporal.getYear(),
        temporal.getMonth(),
        temporal.getDay(),
        temporal.getHour(),
        temporal.getMinute(),
        temporal.getSecond(),
        temporal.getNano(),
        temporal.getZoneOffset())));
  }

  @Override
  default IDateTimeWithTimeZoneItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
