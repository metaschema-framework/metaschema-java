/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IOpaqueMapKey;

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
        || obj instanceof IOpaqueMapKey && getKey().deepEquals(((IOpaqueMapKey) obj).getKey());
  }
}
