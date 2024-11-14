/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDate;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateWithTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateWithoutTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a date data value.
 */
public interface IDateItem extends ITemporalItem {

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
   *
   * @param value
   *          a date, without time zone information
   * @return the new item
   */
  @NonNull
  static IDateItem valueOf(@NonNull AmbiguousDate value) {
    return new DateWithoutTimeZoneItemImpl(value);
  }

  /**
   * Construct a new date item using the provided {@code value}.
   *
   * @param value
   *          a date, with time zone information
   * @return the new item
   */
  @NonNull
  static IDateItem valueOf(@NonNull ZonedDateTime value) {
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
  static IDateItem cast(@NonNull IAnyAtomicItem item) {
    IDateItem retval;
    if (item instanceof IDateItem) {
      retval = (IDateItem) item;
    } else if (item instanceof IDateTimeItem) {
      ZonedDateTime value = ((IDateTimeItem) item).asZonedDateTime();
      retval = valueOf(value);
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
  IDateItem castAsType(IAnyAtomicItem item);

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }
}
