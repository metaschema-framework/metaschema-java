/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.YearMonthDurationItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Period;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a duration data value in years, months,
 * and days.
 */
public interface IYearMonthDurationItem extends IDurationItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IYearMonthDurationItem> type() {
    return MetaschemaDataTypeProvider.YEAR_MONTH_DURATION.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IYearMonthDurationItem> getType() {
    return type();
  }

  /**
   * Construct a new year month day duration item using the provided string
   * {@code value}.
   *
   * @param value
   *          a string representing a year month day duration
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the provided string value is not a day/time duration value
   *           according to ISO 8601
   */
  @NonNull
  static IYearMonthDurationItem valueOf(@NonNull String value) {
    try {
      Period period = ObjectUtils.notNull(MetaschemaDataTypeProvider.YEAR_MONTH_DURATION.parse(value).withDays(0));
      return valueOf(period);
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid year/month duration value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
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
  @NonNull
  static IYearMonthDurationItem valueOf(int years, int months) {
    return valueOf(ObjectUtils.notNull(Period.of(years, months, 0)));
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
    try {
      return item instanceof IYearMonthDurationItem
          ? (IYearMonthDurationItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
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
