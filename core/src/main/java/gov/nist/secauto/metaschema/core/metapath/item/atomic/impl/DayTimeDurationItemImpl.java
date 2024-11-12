/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.DayTimeAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.AbstractAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import java.time.Duration;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a duration data value
 * in days, hours, and seconds.
 */
public class DayTimeDurationItemImpl
    extends AbstractAnyAtomicItem<Duration>
    implements IDayTimeDurationItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public DayTimeDurationItemImpl(@NonNull Duration value) {
    super(value);
  }

  @Override
  public Duration asDuration() {
    return getValue();
  }

  @Override
  public DayTimeAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.DAY_TIME_DURATION;
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  @Override
  public int hashCode() {
    return asDuration().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof IDayTimeDurationItem && compareTo((IDayTimeDurationItem) obj) == 0;
  }

  private final class MapKey implements IMapKey {
    @Override
    public IDayTimeDurationItem getKey() {
      return DayTimeDurationItemImpl.this;
    }

    @Override
    public int hashCode() {
      return getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj ||
          obj instanceof MapKey
              && getKey().equals(((MapKey) obj).getKey());
    }
  }
}
