/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a temporal data value.
 */
public interface ITemporalItem extends IAnyAtomicItem {
  /**
   * Determine if the temporal item has a timezone.
   *
   * @return {@code true} if the temporal item has a timezone or {@code false}
   *         otherwise
   */
  boolean hasTimezone();

  /**
   * Get the "wrapped" date/time value.
   *
   * @return the underlying date value
   */
  @NonNull
  ZonedDateTime asZonedDateTime();

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(@NonNull ITemporalItem item) {
    return asZonedDateTime().compareTo(item.asZonedDateTime());
  }
}
