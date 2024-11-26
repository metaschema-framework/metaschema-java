/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.DateWithTZAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateWithTimeZoneItem;

import java.time.ZonedDateTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a date data value that
 * has a required timezone.
 */
public class DateWithTimeZoneItemImpl
    extends AbstractDateItem<ZonedDateTime>
    implements IDateWithTimeZoneItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public DateWithTimeZoneItemImpl(@NonNull ZonedDateTime value) {
    super(value);
  }

  @Override
  public boolean hasTimezone() {
    return true;
  }

  @Override
  public ZonedDateTime asZonedDateTime() {
    return getValue();
  }

  @Override
  public DateWithTZAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DATE_WITH_TZ;
  }
}
