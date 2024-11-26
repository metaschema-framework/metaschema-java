/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.TimeAdapter;
import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousTime;

import java.time.OffsetTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a time data value that
 * may not have an explicit timezone.
 * <p>
 * For example, when parsing dates from data sources that don't specify timezone
 * information, such as "2024-01-01" as compared to "2024-01-01Z" or
 * "2024-01-01+05:00".
 */
public class TimeWithoutTimeZoneItemImpl
    extends AbstractTimeItem<AmbiguousTime> {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public TimeWithoutTimeZoneItemImpl(@NonNull AmbiguousTime value) {
    super(value);
  }

  @Override
  public boolean hasTimezone() {
    return getJavaTypeAdapter().toValue(getValue()).hasTimeZone();
  }

  @Override
  public TimeAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.TIME;
  }

  @Override
  public OffsetTime asOffsetTime() {
    return getValue().getValue();
  }
}
