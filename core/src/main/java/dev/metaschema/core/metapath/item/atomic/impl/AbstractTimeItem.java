/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic.impl;

import java.time.ZoneOffset;

import dev.metaschema.core.metapath.impl.AbstractMapKey;
import dev.metaschema.core.metapath.item.atomic.ITimeItem;
import dev.metaschema.core.metapath.item.function.IMapKey;
import dev.metaschema.core.metapath.item.function.ITemporalMapKey;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract implementation of a Metapath atomic item containing a date/time
 * data value.
 *
 * @param <TYPE>
 *          the Java type of the wrapped value
 */
public abstract class AbstractTimeItem<TYPE>
    extends AbstractTemporalItem<TYPE>
    implements ITimeItem {
  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  protected AbstractTimeItem(@NonNull TYPE value) {
    super(value);
  }

  @Override
  public int hashCode() {
    return asOffsetTime().withOffsetSameInstant(ZoneOffset.UTC).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof ITimeItem) {
      ITimeItem that = (ITimeItem) obj;
      return hasTimezone() == that.hasTimezone() && deepEquals(that);
    }
    return false;
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  private final class MapKey
      extends AbstractMapKey
      implements ITemporalMapKey {

    @Override
    public ITimeItem getKey() {
      return AbstractTimeItem.this;
    }
  }
}
