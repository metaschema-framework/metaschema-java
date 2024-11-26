/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.adapter.TimeWithTZAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeWithTimeZoneItem;

import java.time.OffsetTime;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a date/time data value
 * that has a required timezone.
 */
public class TimeWithTimeZoneItemImpl
    extends AbstractTimeItem<OffsetTime>
    implements ITimeWithTimeZoneItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public TimeWithTimeZoneItemImpl(@NonNull OffsetTime value) {
    super(value);
  }

  @Override
  public boolean hasTimezone() {
    return true;
  }

  @Override
  public TimeWithTZAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.TIME_WITH_TZ;
  }

  @Override
  public OffsetTime asOffsetTime() {
    return getValue();
  }
}
