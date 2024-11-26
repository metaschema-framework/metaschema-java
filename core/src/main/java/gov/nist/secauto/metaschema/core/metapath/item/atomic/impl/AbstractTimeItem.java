/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.AbstractAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract implementation of a Metapath atomic item containing a date/time
 * data value.
 *
 * @param <TYPE>
 *          the Java type of the wrapped value
 */
public abstract class AbstractTimeItem<TYPE>
    extends AbstractAnyAtomicItem<TYPE>
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
    return asOffsetTime().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof ITimeItem && compareTo((ITimeItem) obj) == 0;
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  private final class MapKey
      implements IMapKey {

    @Override
    public AbstractTimeItem<TYPE> getKey() {
      return AbstractTimeItem.this;
    }

    @Override
    public int hashCode() {
      return getKey().hashCode();
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (!(obj instanceof AbstractTimeItem.MapKey)) {
        return false;
      }

      AbstractTimeItem<?>.MapKey other = (AbstractTimeItem<?>.MapKey) obj;
      return getKey().compareTo(other.getKey()) == 0;
    }
  }
}
