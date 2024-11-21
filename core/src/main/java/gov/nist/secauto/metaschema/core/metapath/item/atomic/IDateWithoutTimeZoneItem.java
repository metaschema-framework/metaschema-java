/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDate;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateWithoutTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a date data value that may have an
 * ambiguous timezone.
 */
public interface IDateWithoutTimeZoneItem extends IDateItem {

  @NonNull
  static IAtomicOrUnionType type() {
    return MetaschemaDataTypeProvider.DATE.getItemType();
  }

  /**
   * Construct a new date item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a date
   * @return the new item
   */
  @NonNull
  static IDateWithoutTimeZoneItem valueOf(@NonNull String value) {
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
    return hasTimeZone
        ? IDateWithTimeZoneItem.valueOf(value)
        : valueOf(new AmbiguousDate(value, false));
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
  static IDateWithoutTimeZoneItem valueOf(@NonNull AmbiguousDate value) {
    return new DateWithoutTimeZoneItemImpl(value);
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
  static IDateWithoutTimeZoneItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IDateWithoutTimeZoneItem
          ? (IDateWithoutTimeZoneItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IDateWithoutTimeZoneItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
