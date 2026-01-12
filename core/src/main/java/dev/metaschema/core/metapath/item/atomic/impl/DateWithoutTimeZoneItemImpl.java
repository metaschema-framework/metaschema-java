/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic.impl;

import java.time.ZonedDateTime;

import dev.metaschema.core.datatype.adapter.DateAdapter;
import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.datatype.object.AmbiguousDate;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a date data value that
 * may not have an explicit timezone.
 */
public class DateWithoutTimeZoneItemImpl
    extends AbstractDateItem<AmbiguousDate> {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public DateWithoutTimeZoneItemImpl(@NonNull AmbiguousDate value) {
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
  public DateAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DATE;
  }
}
