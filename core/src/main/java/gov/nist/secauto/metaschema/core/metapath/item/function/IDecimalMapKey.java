/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;

import java.math.BigDecimal;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An {@link IMapItem} key based on an {@link IDecimalItem}.
 */
public interface IDecimalMapKey extends IMapKey {
  @Override
  IDecimalItem getKey();

  /**
   * Get this key's value as a decimal.
   *
   * @return the equivalent decimal value
   */
  @NonNull
  BigDecimal asDecimal();

  @Override
  default boolean isSameKey(IMapKey other) {
    return other instanceof IDecimalMapKey
        && asDecimal().compareTo(((IDecimalMapKey) other).asDecimal()) == 0;
  }
}
