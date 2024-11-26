/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.object;

import java.time.OffsetTime;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a date value which may not have a timezone making it ambiguous as
 * a window in time.
 */
public class AmbiguousTime
    extends AbstractAmbiguousTemporal<AmbiguousTime, OffsetTime> {

  /**
   * Construct a new date object. This type supports ambiguous dates that were
   * provided without a time zone.
   * <p>
   * The date value will be ambiguous if the {@code hasTimeZone} is {@code false}.
   *
   * @param value
   *          the date value
   * @param hasTimeZone
   *          {@code true} if the date is intended to have an associated time zone
   *          or {@code false} otherwise
   */
  public AmbiguousTime(@NonNull OffsetTime value, boolean hasTimeZone) {
    super(value, hasTimeZone);
  }

  @Override
  public AmbiguousTime copy() {
    return new AmbiguousTime(getValue(), hasTimeZone());
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
    if (!(obj instanceof AmbiguousTime)) {
      return false;
    }
    AmbiguousTime other = (AmbiguousTime) obj;
    return hasTimeZone() == other.hasTimeZone() && getValue().equals(other.getValue());
  }
}
