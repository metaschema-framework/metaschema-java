/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.object;

import java.time.ZonedDateTime;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a date/time value which may not have a timezone making it
 * ambiguous as a point in time.
 */
public class AmbiguousDateTime
    extends AbstractAmbiguousTemporal<AmbiguousDateTime, ZonedDateTime> {

  /**
   * Construct a new date/time object. This type supports ambiguous dates/times
   * that were provided without a time zone.
   * <p>
   * The date/time value will be ambiguous if the {@code hasTimeZone} is
   * {@code false}.
   *
   * @param value
   *          the date/time value
   * @param hasTimeZone
   *          {@code true} if the date/time is intended to have an associated time
   *          zone or {@code false} otherwise
   */
  public AmbiguousDateTime(@NonNull ZonedDateTime value, boolean hasTimeZone) {
    super(value, hasTimeZone);
  }

  @Override
  public AmbiguousDateTime copy() {
    return new AmbiguousDateTime(getValue(), hasTimeZone());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getValue(), hasTimeZone());
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AmbiguousDateTime)) {
      return false;
    }
    AmbiguousDateTime other = (AmbiguousDateTime) obj;
    return hasTimeZone() == other.hasTimeZone() && getValue().equals(other.getValue());
  }
}
