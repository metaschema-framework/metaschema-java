/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.HostnameItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a hostname data value.
 */
public interface IHostnameItem extends IStringItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IHostnameItem> type() {
    return MetaschemaDataTypeProvider.HOSTNAME.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IHostnameItem> getType() {
    return type();
  }

  /**
   * Construct a new host name item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a host name value
   * @return the new item
   */
  @NonNull
  static IHostnameItem valueOf(@NonNull String value) {
    try {
      return new HostnameItemImpl(MetaschemaDataTypeProvider.HOSTNAME.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid hostname value '%s'. %s",
              value,
              ex.getLocalizedMessage()),
          ex);
    }
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
  static IHostnameItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IHostnameItem
          ? (IHostnameItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IHostnameItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
