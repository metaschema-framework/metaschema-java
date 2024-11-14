/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.EmailAddressItemImpl;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing an email address data value.
 */
public interface IEmailAddressItem extends IStringItem {
  /**
   * Construct a new email address item using the provided string {@code value}.
   *
   * @param value
   *          a string representing an email address value
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the given string is not an email address value
   */
  @NonNull
  static IEmailAddressItem valueOf(@NonNull String value) {
    try {
      return new EmailAddressItemImpl(MetaschemaDataTypeProvider.EMAIL_ADDRESS.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid email address value '%s'. %s",
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
  static IEmailAddressItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IEmailAddressItem
          ? (IEmailAddressItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IEmailAddressItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
