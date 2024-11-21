/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.DateTimeWithTZAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeWithTimeZoneItem;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a date/time data value
 * that has a required timezone.
 */
public class DateTimeWithTimeZoneItemImpl
    extends AbstractDateTimeItem<ZonedDateTime>
    implements IDateTimeWithTimeZoneItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public DateTimeWithTimeZoneItemImpl(@NonNull ZonedDateTime value) {
    super(value);
  }

  @Override
  public ZonedDateTime asZonedDateTime() {
    return getValue();
  }

  @Override
  public DateTimeWithTZAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DATE_TIME_WITH_TZ;
  }
}
