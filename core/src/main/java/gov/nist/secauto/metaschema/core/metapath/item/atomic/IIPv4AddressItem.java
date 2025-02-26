/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.IPv4AddressItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.ipv4.IPv4Address;

/**
 * An atomic Metapath item containing an IPv4 address data value.
 */
public interface IIPv4AddressItem extends IIPAddressItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IIPv4AddressItem> type() {
    return MetaschemaDataTypeProvider.IP_V4_ADDRESS.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IIPv4AddressItem> getType() {
    return type();
  }

  /**
   * Construct a new IPv4 item using the provided {@code value}.
   *
   * @param value
   *          an IPv4 value
   * @return the new item
   */
  @NonNull
  static IIPv4AddressItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.IP_V4_ADDRESS.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid IPv4 address value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Construct a new IPv4 item using the provided {@code value}.
   *
   * @param value
   *          an IPv4 value
   * @return the new item
   */
  @NonNull
  static IIPv4AddressItem valueOf(@NonNull IPv4Address value) {
    return new IPv4AddressItemImpl(value);
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
  static IIPv4AddressItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IIPv4AddressItem
          ? (IIPv4AddressItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  IPv4Address asIpAddress();

  @Override
  default IIPv4AddressItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
