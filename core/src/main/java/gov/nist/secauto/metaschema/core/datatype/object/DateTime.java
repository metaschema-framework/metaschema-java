/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.object;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DateTime
    extends AbstractAmbiguousTemporal<DateTime> {

  /**
   * Construct a new date/time object. This type supports ambiguous dates/times
   * that were provided without a time zone.
   *
   * @param value
   *          the date/time value
   * @param hasTimeZone
   *          {@code true} if the date/time is intended to have an associated time
   *          zone or {@code false} otherwise
   */
  public DateTime(@NonNull ZonedDateTime value, boolean hasTimeZone) {
    super(value, hasTimeZone);
  }

  @Override
  public DateTime copy() {
    return new DateTime(getValue(), hasTimeZone());
  }
}
