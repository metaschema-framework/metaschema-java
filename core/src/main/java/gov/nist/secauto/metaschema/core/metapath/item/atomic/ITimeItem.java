/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousTime;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.TimeWithoutTimeZoneItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import java.time.OffsetTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item representing a time value in the Metapath system.
 * <p>
 * This interface provides functionality for handling time values with and
 * without time zone information, supporting parsing, casting, and comparison
 * operations. It works in conjunction with {@link AmbiguousTime} to properly
 * handle time zone ambiguity.
 */
public interface ITimeItem extends IAnyAtomicItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<ITimeItem> type() {
    return MetaschemaDataTypeProvider.TIME.getItemType();
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

  /**
   * Determine if the temporal item has a timezone.
   *
   * @return {@code true} if the temporal item has a timezone or {@code false}
   *         otherwise
   */
  boolean hasTimezone();

  /**
   * Get the underlying time value.
   *
   * @return the time value
   */
  @NonNull
  OffsetTime asOffsetTime();

  @Override
  default ITimeItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return asOffsetTime().compareTo(cast(item).asOffsetTime());
  }
}
