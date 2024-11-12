/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIPv6AddressItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import inet.ipaddr.IPAddressStringParameters;
import inet.ipaddr.IncompatibleAddressException;
import inet.ipaddr.ipv6.IPv6Address;

/**
 * Support for the Metaschema <a href=
 * "https://pages.nist.gov/metaschema/specification/datatypes/#ip-v6-address">ip-v6-address</a>
 * data type.
 */
public class IPv6AddressAdapter
    extends AbstractDataTypeAdapter<IPv6Address, IIPv6AddressItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "ip-v6-address")));
  private static final IPAddressStringParameters IP_V_6;

  static {
    IP_V_6 = new IPAddressStringParameters.Builder().allowIPv4(false).allowEmpty(false).allowSingleSegment(false)
        .allowWildcardedSeparator(false).getIPv6AddressParametersBuilder().allowBinary(false)
        .allowPrefixesBeyondAddressSize(false).getParentBuilder().toParams();
  }

  IPv6AddressAdapter() {
    super(IPv6Address.class);
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @SuppressWarnings("null")
  @Override
  public IPv6Address parse(String value) {
    try {
      return (IPv6Address) new IPAddressString(value, IP_V_6).toAddress();
    } catch (AddressStringException | IncompatibleAddressException ex) {
      throw new IllegalArgumentException(ex.getLocalizedMessage(), ex);
    }
  }

  @Override
  public IPv6Address copy(Object obj) {
    // value is immutable
    return (IPv6Address) obj;
  }

  @Override
  public Class<IIPv6AddressItem> getItemClass() {
    return IIPv6AddressItem.class;
  }

  @Override
  public IIPv6AddressItem newItem(Object value) {
    IPv6Address item = toValue(value);
    return IIPv6AddressItem.valueOf(item);
  }
}
