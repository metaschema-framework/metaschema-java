/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Period;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IYearMonthDurationItem extends IDurationItem {
  /**
   * Construct a new year month day duration item using the provided string
   * {@code value}.
   *
   * @param value
   *          a string representing a year month day duration
   * @return the new item
   */
  @NonNull
  static IYearMonthDurationItem valueOf(@NonNull String value) {
    try {
      Period period = ObjectUtils.notNull(MetaschemaDataTypeProvider.YEAR_MONTH_DURATION.parse(value).withDays(0));
      return valueOf(period);
    } catch (IllegalArgumentException ex) {
      throw new InvalidValueForCastFunctionException(String.format("Unable to parse string value '%s'", value),
          ex);
    }
  }

  /**
   * Construct a new year month day duration item using the provided
   * {@code value}.
   *
   * @param value
   *          a duration
   * @return the new item
   */
  @NonNull
  static IYearMonthDurationItem valueOf(@NonNull Period value) {
    return new YearMonthDurationItemImpl(ObjectUtils.notNull(value.withDays(0)));
  }

  /**
   * Construct a new year month day duration item using the provided values.
   *
   * @param years
   *          the number of years in the period
   * @param months
   *          the number of months in the period
   * @return the new item
   */
  @SuppressWarnings("null")
  @NonNull
  static IYearMonthDurationItem valueOf(int years, int months) {
    return valueOf(Period.of(years, months, 0));
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
  static IYearMonthDurationItem cast(@NonNull IAnyAtomicItem item) {
    return MetaschemaDataTypeProvider.YEAR_MONTH_DURATION.cast(item);
  }

  /**
   * Get the "wrapped" duration value.
   *
   * @return the underlying duration value
   */
  @NonNull
  Period asPeriod();

  @Override
  default IYearMonthDurationItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(IYearMonthDurationItem item) {
    Period thisPeriod = asPeriod().normalized();
    Period thatPeriod = item.asPeriod().normalized();

    int result = Integer.compare(thisPeriod.getYears(), thatPeriod.getYears());
    return result == 0 ? Integer.compare(thisPeriod.getMonths(), thatPeriod.getMonths()) : result;
  }
}
