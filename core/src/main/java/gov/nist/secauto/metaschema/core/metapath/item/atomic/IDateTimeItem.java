/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.DateTime;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateTimeWithTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.DateTimeWithoutTimeZoneItemImpl;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath atomic item containing a date/time data value.
 */
public interface IDateTimeItem extends ITemporalItem {
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
          String.format("The value '%s' is not a valid date/time. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Construct a new date/time item using the provided {@code value}.
   *
   * @param value
   *          a date/time, without time zone information
   * @return the new item
   */
  @NonNull
  static IDateTimeItem valueOf(@NonNull DateTime value) {
    return new DateTimeWithoutTimeZoneItemImpl(value);
  }

  /**
   * Construct a new date/time item using the provided {@code value}.
   *
   * @param value
   *          a date/time, with time zone information
   * @return the new item
   */
  @NonNull
  static IDateTimeItem valueOf(@NonNull ZonedDateTime value) {
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
  static IDateTimeItem cast(@NonNull IAnyAtomicItem item) {
    IDateTimeItem retval;
    if (item instanceof IDateTimeItem) {
      retval = (IDateTimeItem) item;
    } else {
      String itemString = item.asString();
      try {
        retval = valueOf(itemString);
      } catch (IllegalStateException | InvalidTypeMetapathException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(
            String.format("The value '%s' is not compatible with the type '%s'",
                item.getValue(),
                item.getClass().getName()),
            ex);
      }
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
