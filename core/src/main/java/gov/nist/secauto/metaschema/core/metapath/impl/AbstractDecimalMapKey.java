/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IDecimalMapKey;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import nl.talsmasoftware.lazy4j.Lazy;

/**
 * An implementation of a {@link IMapKey} that uses a string-based value.
 */
public abstract class AbstractDecimalMapKey
    extends AbstractMapKey
    implements IDecimalMapKey {
  private final Lazy<Integer> hashCode = Lazy.lazy(() -> asDecimal().stripTrailingZeros().hashCode());

  @Override
  public int hashCode() {
    return hashCode.get();
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj
        // TODO: implement fn:codepoint-equal per spec
        || obj instanceof IDecimalMapKey && asDecimal().compareTo(((IDecimalMapKey) obj).asDecimal()) == 0;
  }
}
