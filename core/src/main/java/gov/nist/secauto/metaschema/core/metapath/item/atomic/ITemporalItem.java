/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Comparator;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An atomic Metapath item containing a temporal data value.
 */
public interface ITemporalItem extends IAnyAtomicItem {

  int getYear();

  int getMonth();

  int getDay();

  int getHour();

  int getMinute();

  int getSecond();

  int getNano();

  @Nullable
  ZoneOffset getZoneOffset();

  @Nullable
  default IDayTimeDurationItem getOffset() {
    ZoneOffset offset = getZoneOffset();
    return offset == null
        ? null
        : IDayTimeDurationItem.valueOf(ObjectUtils.notNull(Duration.ofSeconds(offset.getTotalSeconds())));
  }

  // /**
  // * Cast the provided type to this item type.
  // *
  // * @param item
  // * the item to cast
  // * @return the original item if it is already this type, otherwise a new item cast to this type
  // * @throws InvalidValueForCastFunctionException
  // * if the provided {@code item} cannot be cast to this type
  // */
  // @NonNull
  // static ITemporalItem cast(@NonNull IAnyAtomicItem item) {
  // ITemporalItem retval;
  // if (item instanceof ITemporalItem) {
  // retval = (ITemporalItem) item;
  // } else {
  // String value;
  // try {
  // value = item.asString();
  // } catch (IllegalStateException ex) {
  // // asString can throw IllegalStateException exception
  // throw new InvalidValueForCastFunctionException(ex);
  // }
  //
  // try {
  // retval = IDateTimeItem.valueOf(value);
  // } catch (IllegalStateException ex) {
  // try {
  // retval = IDateItem.valueOf(value);
  // } catch (IllegalStateException ex2) {
  // InvalidValueForCastFunctionException newEx = new InvalidValueForCastFunctionException(
  // String.format("Value '%s' cannot be parsed as either a date or date/time value", value),
  // ex2);
  // newEx.addSuppressed(ex);
  // throw newEx; // NOPMD context as suppressed
  // }
  // }
  // }
  // return retval;
  // }

  /**
   * Determine if the temporal item has a timezone.
   *
   * @return {@code true} if the temporal item has a timezone or {@code false} otherwise
   */
  boolean hasTimezone();

  boolean hasDate();

  boolean hasTime();

  /**
   * Adjusts a temporal item value to a specific timezone, or to no timezone at all.
   *
   * @param offset
   *          the timezone offset to use or {@code null}
   * @return the adjusted temporal value
   * @throws DateTimeFunctionException
   *           with code {@link DateTimeFunctionException#INVALID_TIME_ZONE_VALUE_ERROR} if the offset
   *           is < -PT14H or > PT14H
   */
  @NonNull
  ITemporalItem replaceTimezone(@Nullable IDayTimeDurationItem offset);

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less than, equal to, or
   *         greater than the {@code item}.
   */
  default int compareTo(@NonNull ITemporalItem item) {
    return Comparator.comparing(ITemporalItem::getYear)
        .thenComparing(ITemporalItem::getMonth)
        .thenComparing(ITemporalItem::getDay)
        .thenComparing(ITemporalItem::getHour)
        .thenComparing(ITemporalItem::getMinute)
        .thenComparing(ITemporalItem::getSecond)
        .thenComparing(ITemporalItem::getNano)
        .thenComparing(ITemporalItem::getZoneOffset, Comparator.nullsFirst(Comparator.naturalOrder()))
        .compare(this, item);
  }
}
