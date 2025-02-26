/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.time.Duration;
import java.time.ZoneOffset;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An atomic Metapath item containing a temporal data value.
 */
public interface ITemporalItem extends IAnyAtomicItem {
  /**
   * Get the year value of this temporal.
   *
   * @return the year value
   */
  int getYear();

  /**
   * Get the month value of this temporal.
   *
   * @return the month value
   */
  int getMonth();

  /**
   * Get the day value of this temporal.
   *
   * @return the day value
   */
  int getDay();

  /**
   * Get the hour value of this temporal.
   *
   * @return the hour value
   */
  int getHour();

  /**
   * Get the minute value of this temporal.
   *
   * @return the minute value
   */
  int getMinute();

  /**
   * Get the whole second value of this temporal.
   *
   * @return the whole second value
   */
  int getSecond();

  /**
   * Get the partial nano second value of this temporal.
   *
   * @return the partial nano second value
   */
  int getNano();

  /**
   * Get the timezone offset for this temporal.
   *
   * @return the timezone offset if specified or {@code null} if the timezone is
   *         not known
   * @see ITemporalItem#hasTimezone()
   */
  @Nullable
  ZoneOffset getZoneOffset();

  /**
   * Get the timezone offset as a day/time duration for this temporal.
   *
   * @return the timezone offset if specified or {@code null} if the timezone is
   *         not known
   * @see ITemporalItem#hasTimezone()
   */
  @Nullable
  default IDayTimeDurationItem getOffset() {
    ZoneOffset offset = getZoneOffset();
    return offset == null
        ? null
        : IDayTimeDurationItem.valueOf(ObjectUtils.notNull(Duration.ofSeconds(offset.getTotalSeconds())));
  }

  /**
   * Determine if the temporal item has a timezone.
   *
   * @return {@code true} if the temporal item has a timezone or {@code false}
   *         otherwise
   */
  boolean hasTimezone();

  /**
   * Determine if the temporal has date information.
   *
   * @return {@code true} if the temporal item has date information or
   *         {@code false} otherwise
   */
  boolean hasDate();

  /**
   * Determine if the temporal has time information.
   *
   * @return {@code true} if the temporal item has time information or
   *         {@code false} otherwise
   */
  boolean hasTime();

  /**
   * Adjusts a temporal item value to a specific timezone, or to no timezone at
   * all.
   *
   * @param offset
   *          the timezone offset to use or {@code null}
   * @return the adjusted temporal value
   * @throws DateTimeFunctionException
   *           with code
   *           {@link DateTimeFunctionException#INVALID_TIME_ZONE_VALUE_ERROR} if
   *           the offset is &lt; -PT14H or &gt; PT14H
   */
  @NonNull
  ITemporalItem replaceTimezone(@Nullable IDayTimeDurationItem offset);
}
