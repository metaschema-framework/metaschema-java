/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.IPAddress;

public interface IIPAddressItem extends IUntypedAtomicItem {
  /**
   * Get the "wrapped" IP address value.
   *
   * @return the underlying IP address value
   */
  @NonNull
  IPAddress asIpAddress();

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(IIPAddressItem item) {
    return asIpAddress().compareTo(item.asIpAddress());
  }
}
