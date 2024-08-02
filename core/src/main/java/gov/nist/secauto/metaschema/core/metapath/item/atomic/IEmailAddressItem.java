/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IEmailAddressItem extends IStringItem {
  /**
   * Construct a new email address item using the provided string {@code value}.
   *
   * @param value
   *          a string representing an email address value
   * @return the new item
   */
  @NonNull
  static IEmailAddressItem valueOf(@NonNull String value) {
    try {
      return new EmailAddressItemImpl(MetaschemaDataTypeProvider.EMAIL_ADDRESS.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidValueForCastFunctionException(String.format("Unable to parse string value '%s'", value),
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
    return MetaschemaDataTypeProvider.EMAIL_ADDRESS.cast(item);
  }

  @Override
  default IEmailAddressItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
