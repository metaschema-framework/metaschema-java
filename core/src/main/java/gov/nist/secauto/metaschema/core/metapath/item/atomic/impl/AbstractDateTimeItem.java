/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.impl.AbstractMapKey;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.ICalendarMapKey;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import java.time.ZoneOffset;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract implementation of a Metapath atomic item containing a date/time
 * data value.
 *
 * @param <TYPE>
 *          the Java type of the wrapped value
 */
public abstract class AbstractDateTimeItem<TYPE>
    extends AbstractTemporalItem<TYPE>
    implements IDateTimeItem {
  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  protected AbstractDateTimeItem(@NonNull TYPE value) {
    super(value);
  }

  @Override
  public int hashCode() {
    int result = asZonedDateTime().withZoneSameInstant(ZoneOffset.UTC).hashCode();
    result = hasTimezone() ? 31 * result * Boolean.hashCode(hasTimezone()) : result;
    return 31 * result * getClass().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof IDateTimeItem) {
      IDateTimeItem that = (IDateTimeItem) obj;
      return hasTimezone() == that.hasTimezone()
          && deepEquals(that);
    }
    return false;
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  private final class MapKey
      extends AbstractMapKey
      implements ICalendarMapKey {
    @Override
    public IDateTimeItem getKey() {
      return AbstractDateTimeItem.this;
    }
  }
}
