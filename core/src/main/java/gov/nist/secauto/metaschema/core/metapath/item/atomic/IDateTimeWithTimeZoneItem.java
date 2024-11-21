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
  @NonNull
  static IAtomicOrUnionType type() {
    return MetaschemaDataTypeProvider.DATE_TIME_WITH_TZ.getItemType();
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
  @SuppressWarnings({ "PMD.OnlyOneReturn", "PMD.CyclomaticComplexity" })
  @NonNull
  static IDateTimeWithTimeZoneItem cast(@NonNull IAnyAtomicItem item) {
    if (item instanceof IDateTimeWithTimeZoneItem) {
      return (IDateTimeWithTimeZoneItem) item;
    }

    if (item instanceof ITemporalItem) {
      ITemporalItem temporal = (ITemporalItem) item;
      if (!temporal.hasTimezone()) {
        throw new InvalidValueForCastFunctionException(
            String.format("The temporal value '%s' does not have timezone information.", temporal.asString()));
      }
      return valueOf(temporal.asZonedDateTime());
    }

    if (item instanceof IStringItem || item instanceof IUntypedAtomicItem) {
      try {
        return valueOf(item.asString());
      } catch (IllegalStateException | InvalidTypeMetapathException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }
    }
    throw new InvalidValueForCastFunctionException(
        String.format("unsupported item type '%s'", item.getClass().getName()));
  }

  @Override
  default IDateTimeWithTimeZoneItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
