/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.IPv6AddressAdapter;
import gov.nist.secauto.metaschema.core.datatype.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.atomic.IIPv6AddressItem;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.ipv6.IPv6Address;

/**
 * An implementation of a Metapath atomic item containing an IPv6 address data
 * value.
 */
public class IPv6AddressItemImpl
    extends AbstractIPAddressItem<IPv6Address>
    implements IIPv6AddressItem {
  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public IPv6AddressItemImpl(@NonNull IPv6Address value) {
    super(value);
  }

  @Override
  public IPv6AddressAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.IP_V6_ADDRESS;
  }

  @Override
  public IPv6Address asIpAddress() {
    return getValue();
  }
}
