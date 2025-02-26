/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateWithTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a date data value that has an explicit
 * timezone.
 */
public interface IDateWithTimeZoneItem extends IDateItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IDateWithTimeZoneItem> type() {
    return MetaschemaDataTypeProvider.DATE_WITH_TZ.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IDateWithTimeZoneItem> getType() {
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
  static IDateWithTimeZoneItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.DATE_WITH_TZ.parse(value));
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
   *
   * @param value
   *          a date, with time zone information
   * @return the new item
   */
  @NonNull
  static IDateWithTimeZoneItem valueOf(@NonNull ZonedDateTime value) {
    return new DateWithTimeZoneItemImpl(
        // ignore time
        ObjectUtils.notNull(value.truncatedTo(ChronoUnit.DAYS)));
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
  static IDateWithTimeZoneItem cast(@NonNull IAnyAtomicItem item) {
    IDateWithTimeZoneItem retval;
    if (item instanceof IDateWithTimeZoneItem) {
      retval = (IDateWithTimeZoneItem) item;
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
  private static IDateWithTimeZoneItem fromTemporal(@NonNull ITemporalItem temporal) {
    if (!temporal.hasTimezone()) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(
          String.format("Unable to cast the temporal value '%s', since it lacks timezone information.",
              temporal.asString()));
    }
    if (!temporal.hasDate()) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(
          String.format("Unable to cast the temporal value '%s', since it lacks date information.",
              temporal.asString()));
    }
    // get the time at midnight
    return new DateWithTimeZoneItemImpl(ObjectUtils.notNull(ZonedDateTime.of(
        temporal.getYear(),
        temporal.getMonth(),
        temporal.getDay(),
        0,
        0,
        0,
        0,
        temporal.getZoneOffset())));
  }

  @Override
  default IDateWithTimeZoneItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
