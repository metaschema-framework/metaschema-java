/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.object;

import gov.nist.secauto.metaschema.core.datatype.AbstractCustomJavaDataType;

import java.time.temporal.Temporal;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implementations of this class represent a temporal value which may not have a
 * timezone making it ambiguous as a point/window in time.
 * <p>
 * Metaschema has a need to represent dates and times that allow for an
 * ambiguous time zone. This is due to some models not requiring a time zone as
 * part of a date/time. An ambiguous dateTime allows a time zone to be inferred,
 * without change information in the source content.
 * <p>
 * This class wraps a ZonedDateTime object and tracks if a time zone was found
 * when parsing, which can be used to ensure that the assumed time zone is not
 * written back out in such cases.
 *
 * @param <TYPE>
 *          the bound object type that extends this class, used for proper type
 *          inheritance in implementing classes like {@code AmbiguousDate} or
 *          {@code AmbiguousDateTime}
 * @param <U>
 *          the Java type of the temporal value
 */
public abstract class AbstractAmbiguousTemporal<TYPE extends AbstractAmbiguousTemporal<TYPE, U>, U extends Temporal>
    extends AbstractCustomJavaDataType<TYPE, U> {
  private final boolean timeZone;

  /**
   * Construct a new object. This type supports ambiguous dates/times that were
   * provided without a time zone.
   *
   * @param value
   *          the date value
   * @param hasTimeZone
   *          {@code true} if the date is intended to have an associated time zone
   *          or {@code false} otherwise
   */
  public AbstractAmbiguousTemporal(@NonNull U value, boolean hasTimeZone) {
    super(value);
    this.timeZone = hasTimeZone;
  }

  /**
   * Indicate if a time zone is configured.
   *
   * @return {@code true} if the date is intended to have an associated time zone
   *         or {@code false} otherwise
   */
  public boolean hasTimeZone() {
    return timeZone;
  }

  @Override
  public String toString() {
    return getValue().toString() + (hasTimeZone() ? "" : "(abiguous)");
  }

}
