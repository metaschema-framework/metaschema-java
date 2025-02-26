/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IOpaqueMapKey;

/**
 * Represents a map key with no special handling based on the key value's data
 * type. In this way the key value is essentially "opaque".
 */
public abstract class AbstractOpaqueMapKey
    extends AbstractMapKey
    implements IOpaqueMapKey {
  @Override
  public int hashCode() {
    return getKey().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof IOpaqueMapKey
            && getKey().deepEquals(((IOpaqueMapKey) obj).getKey());
  }
}
