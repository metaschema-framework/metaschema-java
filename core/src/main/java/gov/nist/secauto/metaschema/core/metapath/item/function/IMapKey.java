/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IMapKey {

  /**
   * Get the atomic item used as the key.
   *
   * @return the atomic item
   */
  @NonNull
  IAnyAtomicItem getKey();
}
