/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

/**
 * An implementation of a {@link IMapKey} that uses a string-based value.
 */
public abstract class AbstractStringMapKey
    implements IMapKey {

  @Override
  public int hashCode() {
    return getKey().asStringItem().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj ||
        obj instanceof AbstractStringMapKey
            && getKey().asStringItem().equals(((AbstractStringMapKey) obj).getKey().asStringItem());
  }
}
