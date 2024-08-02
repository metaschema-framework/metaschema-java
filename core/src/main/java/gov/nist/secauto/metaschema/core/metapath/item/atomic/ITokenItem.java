/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface ITokenItem extends IStringItem {
  /**
   * Construct a new item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a token value
   * @return the new item
   */
  @NonNull
  static ITokenItem valueOf(@NonNull String value) {
    try {
      return new TokenItemImpl(MetaschemaDataTypeProvider.TOKEN.parse(value));
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
  static ITokenItem cast(@NonNull IAnyAtomicItem item) {
    return MetaschemaDataTypeProvider.TOKEN.cast(item);
  }

  @Override
  default ITokenItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
