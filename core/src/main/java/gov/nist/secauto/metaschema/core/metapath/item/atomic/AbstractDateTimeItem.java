/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import edu.umd.cs.findbugs.annotations.NonNull;

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
        || (obj instanceof IDateTimeItem && compareTo((IDateTimeItem) obj) == 0);
  }
}
