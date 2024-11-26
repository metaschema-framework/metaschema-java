/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.type.impl.TypeConstants;

import edu.umd.cs.findbugs.annotations.NonNull;
import inet.ipaddr.IPAddress;

/**
 * An atomic Metapath item representing an IP address data value.
 */
public interface IIPAddressItem extends IAnyAtomicItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IIPAddressItem> type() {
    return TypeConstants.IP_ADDRESS_TYPE;
  }

  @Override
  default IAtomicOrUnionType<? extends IIPAddressItem> getType() {
    return type();
  }

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
    IIPAddressItem retval;
    if (item instanceof IIPAddressItem) {
      retval = (IIPAddressItem) item;
    } else {
      String value;
      try {
        value = item.asString();
      } catch (IllegalStateException ex) {
        // asString can throw IllegalStateException exception
        throw new InvalidValueForCastFunctionException(ex);
      }

      try {
        // try a v6 address
        retval = IIPv6AddressItem.valueOf(value);
      } catch (InvalidTypeMetapathException ex) {
        // try a v4 address
        try {
          retval = IIPv4AddressItem.valueOf(value);
        } catch (InvalidTypeMetapathException ex2) {
          InvalidValueForCastFunctionException newEx = new InvalidValueForCastFunctionException(
              String.format("The value '%s' of type '%s' is not an internet protocol address.",
                  value,
                  item.getJavaTypeAdapter().getPreferredName()),
              ex2);
          newEx.addSuppressed(ex);
          throw newEx;
        }
      }
    }
    return retval;
  }
}
