/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.impl;

import gov.nist.secauto.metaschema.core.metapath.item.function.IDecimalMapKey;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

/**
 * An implementation of a {@link IMapKey} that uses a string-based value.
 */
public abstract class AbstractDecimalMapKey
    extends AbstractMapKey
    implements IDecimalMapKey {

  /**
   * {@inheritDoc}
   * <p>
   * The hash code is based on the least precision to ensure this matches the
   * logic in {@link #isSameKey(IMapKey)}.
   */
  @Override
  protected int generateHashCode() {
    return asDecimal().stripTrailingZeros().hashCode();
  }
}
