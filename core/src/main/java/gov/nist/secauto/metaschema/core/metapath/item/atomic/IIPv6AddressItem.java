/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.ipv6.IPv6Address;

public interface IIPv6AddressItem extends IIPAddressItem {

  /**
   * Construct a new IPv6 item using the provided {@code value}.
   *
   * @param value
   *          an IPv6 value
   * @return the new item
   */
  @NonNull
  static IIPv6AddressItem valueOf(@NonNull IPv6Address value) {
    return new IPv6AddressItemImpl(value);
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
  static IIPv6AddressItem cast(@NonNull IAnyAtomicItem item) {
    return MetaschemaDataTypeProvider.IP_V6_ADDRESS.cast(item);
  }

  @Override
  IPv6Address getValue();

  @Override
  default IIPv6AddressItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }
}
