/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IStringMapKey extends IMapKey {
  /**
   * Get the item's string value.
   *
   * @return the string value value of the item
   */
  @NonNull
  String asString();
}
