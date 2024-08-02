/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;

/**
 * This marker interface identifies a valued {@link IItem} type that has an
 * associated {@link IAnyAtomicItem} value.
 */
public interface IAtomicValuedItem extends IItem {
  /**
   * Get the atomic value for the item. This may be the same item if the item is
   * an instance of {@link IAnyAtomicItem}.
   *
   * @return the atomic value or {@code null} if the item has no available value
   */
  // TODO: review all implementations and uses for correct behavior related to
  // null
  IAnyAtomicItem toAtomicItem();
}
