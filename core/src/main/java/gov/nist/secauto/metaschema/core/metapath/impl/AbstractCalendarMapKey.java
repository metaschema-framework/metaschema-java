/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.ICalendarMapKey;

public abstract class AbstractCalendarMapKey
    extends AbstractMapKey
    implements ICalendarMapKey {

  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof ICalendarMapKey
            && getKey().equals(((ICalendarMapKey) obj).getKey());
  }

  @Override
  public int hashCode() {
    return getKey().hashCode();
  }
}
