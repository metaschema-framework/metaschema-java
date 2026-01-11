/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.function;

import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The key value used in an {@link IMapItem}.
 */
public interface IMapKey {

  /**
   * Get the atomic item used as the key.
   *
   * @return the atomic item
   */
  @NonNull
  IAnyAtomicItem getKey();

  @Override
  int hashCode();

  /**
   * Determine if this key is the same as another key.
   *
   * @param other
   *          the other key to compare
   * @return {@code true} if the keys are the same, or {@code false} otherwise
   */
  boolean isSameKey(@NonNull IMapKey other);
}
