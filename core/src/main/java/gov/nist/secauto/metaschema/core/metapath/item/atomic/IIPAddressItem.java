/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.IPAddress;

/**
 * A Metapath atomic item representing an IP address data value.
 */
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

  /**
   * Cast the provided type to this item type.
   *
   * @param item
   *          the item to cast
   * @return the original item if it is already this type, otherwise a new item
   *         cast to this type
   * @throws InvalidValueForCastFunctionException
   *           if the provided {@code item} cannot be cast to this type
   */
  @NonNull
  static IIPAddressItem cast(@NonNull IAnyAtomicItem item) {
    if (!(item instanceof IIPAddressItem)) {
      String value = null;
      try {
        value = item.asString();
      } catch (IllegalStateException ex) {
        // do nothing
      }

      throw new InvalidValueForCastFunctionException(
          String.format("The value '%s' of type '%s' is not an internet protocol address.",
              value,
              item.getJavaTypeAdapter().getPreferredName()));
    }
    return (IIPv4AddressItem) item;
  }
}
