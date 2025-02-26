/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A temporal valued item related to a {@link ZonedDateTime}.
 * <p>
 * This may represent a specific point in time or a 24 hour period in time.
 */
public interface ICalendarTemporalItem extends ITemporalItem {

  /**
   * Get the temporal value as a {@link ZonedDateTime}.
   *
   * @return the date/time value
   */
  @NonNull
  ZonedDateTime asZonedDateTime();

  @Override
  default ZoneOffset getZoneOffset() {
    return hasTimezone() ? asZonedDateTime().getOffset() : null;
  }
}
