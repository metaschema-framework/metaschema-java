/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.AbstractDurationAdapter;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.type.impl.TypeConstants;

import java.time.temporal.TemporalAmount;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item representing a duration data value.
 * <p>
 * This interface supports both day-time and year-month duration formats
 * following the ISO 8601 standard. Examples of valid durations include:
 * <ul>
 * <li>P1Y2M (1 year, 2 months)
 * <li>P3DT4H5M (3 days, 4 hours, 5 minutes)
 * </ul>
 *
 * @see IDayTimeDurationItem
 * @see IYearMonthDurationItem
 */
public interface IDurationItem extends IAnyAtomicItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IDurationItem> type() {
    return TypeConstants.DURATION_TYPE;
  }

  /**
   * Construct a new duration item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a day time duration
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the provided string value is not a year/month or day/time
   *           duration value according to ISO 8601
   */
  @NonNull
  static IDurationItem valueOf(@NonNull String value) {
    try {
      return AbstractDurationAdapter.parseDuration(value);
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid year/month or day/time duration value '%s'.",
              value),
          ex);
    }
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
  static IDurationItem cast(@NonNull IAnyAtomicItem item) {
    IDurationItem retval;
    if (item instanceof IDurationItem) {
      retval = (IDurationItem) item;
    } else {
      String value;
      try {
        value = item.asString();
      } catch (IllegalStateException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }

      try {
        retval = valueOf(value);
      } catch (InvalidTypeMetapathException ex) {
        throw new InvalidValueForCastFunctionException(
            String.format("Value '%s' cannot be parsed as either a day-time or year-month duration", value),
            ex);
      }
    }
    return retval;
  }

  @Override
  TemporalAmount getValue();

  @Override
  default IDurationItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  int compareTo(@NonNull IDurationItem item);
}
