/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITemporalItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.ITemporalMapKey;

import java.time.ZoneOffset;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractTemporalMapKey
    extends AbstractMapKey
    implements ITemporalMapKey {
  @Override
  public int hashCode() {
    ITemporalItem temporal = asTemporalItem();
    int hash = 7;
    hash = 31 * hash + temporal.getYear();
    hash = 31 * hash + temporal.getDay();
    hash = 31 * hash + temporal.getHour();
    hash = 31 * hash + temporal.getYear();
    hash = 31 * hash + temporal.getMinute();
    hash = 31 * hash + temporal.getSecond();
    hash = 31 * hash + temporal.getNano();

    ZoneOffset offset = temporal.getZoneOffset();
    return 31 * hash + (offset == null ? 0 : offset.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof ITemporalMapKey && equalsInternal((ITemporalMapKey) obj);
  }

  private boolean equalsInternal(@NonNull ITemporalMapKey other) {
    ITemporalItem focus = asTemporalItem();
    ITemporalItem that = other.asTemporalItem();
    return focus.hasTimezone() == that.hasTimezone() && focus.deepEquals(that);
  }
}
