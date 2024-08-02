/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.object;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Date // NOPMD - intentional
    extends AbstractAmbiguousTemporal<Date> {

  /**
   * Construct a new date object. This type supports ambiguous dates that were
   * provided without a time zone.
   *
   * @param value
   *          the date value
   * @param hasTimeZone
   *          {@code true} if the date is intended to have an associated time zone
   *          or {@code false} otherwise
   */
  public Date(@NonNull ZonedDateTime value, boolean hasTimeZone) {
    super(value, hasTimeZone);
  }

  @Override
  public Date copy() {
    return new Date(getValue(), hasTimeZone());
  }
}
