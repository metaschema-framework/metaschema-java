/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An {@link IMapItem} key based on a text value.
 */
public interface IStringMapKey extends IMapKey {
  /**
   * Get the item's string value.
   *
   * @return the string value value of the item
   */
  @NonNull
  String asString();

  @Override
  default boolean isSameKey(IMapKey other) {
    // TODO: implement fn:codepoint-equal per spec
    return other instanceof IStringMapKey
        && asString().equals(((IStringMapKey) other).asString());
  }
}
