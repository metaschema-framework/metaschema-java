/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateTimeWithoutTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item representing a date/time value in the Metapath system
 * with an ambiguous time zone.
 * <p>
 * This interface provides functionality for handling date/time values without
 * time zone information, supporting parsing, casting, and comparison
 * operations. It works in conjunction with {@link AmbiguousDateTime} to
 * properly handle time zone ambiguity.
 */
public interface IDateTimeWithoutTimeZoneItem extends IDateTimeItem {
  @NonNull
  static IAtomicOrUnionType type() {
    return MetaschemaDataTypeProvider.DATE_TIME.getItemType();
  }

  /**
   * Construct a new date/time item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a date/time
   * @return the new item
   */
  @NonNull
  static IDateTimeWithoutTimeZoneItem valueOf(@NonNull String value) {
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
  static IDateTimeWithoutTimeZoneItem valueOf(@NonNull AmbiguousDateTime value) {
    return new DateTimeWithoutTimeZoneItemImpl(value);
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
  static IDateTimeWithoutTimeZoneItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IDateTimeWithoutTimeZoneItem
          ? (IDateTimeWithoutTimeZoneItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IDateTimeWithoutTimeZoneItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
