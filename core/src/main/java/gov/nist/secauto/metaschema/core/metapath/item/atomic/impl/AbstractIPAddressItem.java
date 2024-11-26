/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.AbstractAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIPAddressItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.IPAddress;

/**
 * An abstract implementation of a Metapath atomic item representing an IP
 * address-based data value.
 *
 * @param <TYPE>
 *          the Java type of the data value
 */
public abstract class AbstractIPAddressItem<TYPE extends IPAddress>
    extends AbstractAnyAtomicItem<TYPE>
    implements IIPAddressItem {

  /**
   * Construct a new item.
   *
   * @param value
   *          the item's data value
   */
  protected AbstractIPAddressItem(@NonNull TYPE value) {
    super(value);
  }

  @Override
  public IPAddress asIpAddress() {
    return getValue();
  }

  @Override
  public int hashCode() {
    return asIpAddress().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof IIPAddressItem && compareTo((IIPAddressItem) obj) == 0;
  }

  @Override
  protected String getValueSignature() {
    return "'" + asString() + "'";
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  private final class MapKey implements IMapKey {
    @Override
    public IIPAddressItem getKey() {
      return AbstractIPAddressItem.this;
    }

    @Override
    public int hashCode() {
      return getKey().asIpAddress().hashCode();
    }

    @SuppressWarnings("PMD.OnlyOneReturn")
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (!(obj instanceof AbstractIPAddressItem.MapKey)) {
        return false;
      }

      AbstractIPAddressItem<?>.MapKey other = (AbstractIPAddressItem<?>.MapKey) obj;
      return getKey().compareTo(other.getKey()) == 0;
    }
  }
}
