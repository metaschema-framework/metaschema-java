/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ICollectionValue {
  /**
   * Get the collection value as a sequence.
   * <p>
   * If the collection value is a sequence, then the sequence is returned.
   *
   * @return the resulting sequence
   */
  // TODO: rename to toSequence and resolve conflicting methods?
  @NonNull
  ISequence<?> asSequence();

  /**
   * Get the stream of items for the collection value.
   * <p>
   * If the collection value is a sequence, then the items in the collection are
   * returned.
   *
   * @param value
   *          the collection value
   * @return the sequence of related items
   */
  @NonNull
  static Stream<? extends IItem> normalizeAsItems(@NonNull ICollectionValue value) {
    return value instanceof IItem
        ? ObjectUtils.notNull(Stream.of((IItem) value))
        : value.asSequence().stream();
  }

  /**
   * Get the stream of items for the collection value.
   * <p>
   * If the collection value contains items, then these items are returned.
   *
   * @return a stream of related items
   */
  @NonNull
  Stream<? extends IItem> flatten();
}
