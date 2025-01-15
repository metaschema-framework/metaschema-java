/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;
import gov.nist.secauto.metaschema.core.metapath.item.function.IStringMapKey;

/**
 * An implementation of a {@link IMapKey} that uses a string-based value.
 */
public abstract class AbstractStringMapKey
    extends AbstractMapKey
    implements IStringMapKey {

  @Override
  public int hashCode() {
    return asString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj
        // TODO: implement fn:codepoint-equal per spec
        || obj instanceof IStringMapKey && asString().equals(((IStringMapKey) obj).asString());
  }
}
