/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.impl.AbstractMapKey;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;
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

  @SuppressWarnings("PMD.OnlyOneReturn")
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

  protected final class MapKey
      extends AbstractMapKey {

    @Override
    public ITimeItem getKey() {
      return AbstractTimeItem.this;
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj
          || obj instanceof AbstractTimeItem<?>.MapKey
              && getKey().equals(((AbstractTimeItem<?>.MapKey) obj).getKey());
    }

    @Override
    public int hashCode() {
      return getKey().hashCode();
    }
  }
}
