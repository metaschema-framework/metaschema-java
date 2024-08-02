/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.IPv4AddressAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.ipv4.IPv4Address;

class IPv4AddressItemImpl
    extends AbstractUntypedAtomicItem<IPv4Address>
    implements IIPv4AddressItem {

  public IPv4AddressItemImpl(@NonNull IPv4Address value) {
    super(value);
  }

  @Override
  public IPv4AddressAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.IP_V4_ADDRESS;
  }

  @Override
  public IPv4Address asIpAddress() {
    return getValue();
  }
}
