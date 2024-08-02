/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.IPv6AddressAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.ipv6.IPv6Address;

class IPv6AddressItemImpl
    extends AbstractUntypedAtomicItem<IPv6Address>
    implements IIPv6AddressItem {

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
