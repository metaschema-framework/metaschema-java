/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An abstract implementation of a Metapath atomic item containing a date data
 * value.
 *
 * @param <TYPE>
 *          the Java type of the wrapped value
 */
public abstract class AbstractDateItem<TYPE>
    extends AbstractTemporalItem<TYPE>
    implements IDateItem {
  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  protected AbstractDateItem(@NonNull TYPE value) {
    super(value);
  }

  @Override
  public boolean hasTimezone() {
    return true;
  }

  @Override
  public int hashCode() {
    return asZonedDateTime().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof IDateItem && compareTo((IDateItem) obj) == 0;
  }
}
