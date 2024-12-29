/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.DateTimeAdapter;
import gov.nist.secauto.metaschema.core.datatype.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a date/time data value
 * that may not have an explicit timezone.
 * <p>
 * For example, when parsing dates from data sources that don't specify timezone
 * information, such as "2024-01-01" as compared to "2024-01-01Z" or
 * "2024-01-01+05:00".
 */
public class DateTimeWithoutTimeZoneItemImpl
    extends AbstractDateTimeItem<AmbiguousDateTime> {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public DateTimeWithoutTimeZoneItemImpl(@NonNull AmbiguousDateTime value) {
    super(value);
  }

  @Override
  public boolean hasTimezone() {
    return getJavaTypeAdapter().toValue(getValue()).hasTimeZone();
  }

  @Override
  public ZonedDateTime asZonedDateTime() {
    return getValue().getValue();
  }

  @Override
  public DateTimeAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DATE_TIME;
  }
}
