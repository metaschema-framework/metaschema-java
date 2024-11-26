/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.TimeWithTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.OffsetTime;
import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item representing a time value in the Metapath system with
 * an explicit time zone.
 * <p>
 * This interface provides functionality for handling time values with time zone
 * information, supporting parsing, casting, and comparison operations. It works
 * in conjunction with {@link ZonedDateTime} to eliminate time zone ambiguity.
 */
public interface ITimeWithTimeZoneItem extends ITimeItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<ITimeWithTimeZoneItem> type() {
    return MetaschemaDataTypeProvider.TIME_WITH_TZ.getItemType();
  }

  /**
   * Construct a new time item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a time
   * @return the new item
   */
  @NonNull
  static ITimeWithTimeZoneItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.TIME_WITH_TZ.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid time value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Construct a new time item using the provided {@code value}.
   * <p>
   * This method handles dates with explicit timezone information using
   * ZonedDateTime. The timezone is preserved as specified in the input and is
   * significant for time operations and comparisons.
   *
   * @param value
   *          a time, with time zone information
   * @return the new item
   */
  @NonNull
  static ITimeWithTimeZoneItem valueOf(@NonNull ZonedDateTime value) {
    return new TimeWithTimeZoneItemImpl(ObjectUtils.notNull(value.toOffsetDateTime().toOffsetTime()));
  }

  /**
   * Construct a new time item using the provided {@code value}.
   * <p>
   * This method handles dates with explicit timezone information using
   * ZonedDateTime. The timezone is preserved as specified in the input and is
   * significant for time operations and comparisons.
   *
   * @param value
   *          a time, with time zone information
   * @return the new item
   */
  @NonNull
  static ITimeWithTimeZoneItem valueOf(@NonNull OffsetTime value) {
    return new TimeWithTimeZoneItemImpl(value);
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
  static ITimeWithTimeZoneItem cast(@NonNull IAnyAtomicItem item) {
    ITimeWithTimeZoneItem retval;
    if (item instanceof ITimeWithTimeZoneItem) {
      retval = (ITimeWithTimeZoneItem) item;
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
  private static ITimeWithTimeZoneItem fromTemporal(@NonNull ITemporalItem temporal) {
    if (!temporal.hasTimezone()) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(
          String.format("Unable to cast the temporal value '%s', since it lacks timezone information.",
              temporal.asString()));
    }
    // get the time
    return valueOf(ObjectUtils.notNull(temporal.asZonedDateTime().toOffsetDateTime().toOffsetTime()));
  }

  @Override
  default ITimeWithTimeZoneItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
