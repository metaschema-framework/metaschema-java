/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.TokenItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a text token data value.
 */
public interface ITokenItem extends IStringItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<ITokenItem> type() {
    return MetaschemaDataTypeProvider.TOKEN.getItemType();
  }

  @Override
  default IAtomicOrUnionType<ITokenItem> getType() {
    return type();
  }

  /**
   * Construct a new item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a token value
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the provided value is not a token
   */
  @NonNull
  static ITokenItem valueOf(@NonNull String value) {
    try {
      return new TokenItemImpl(MetaschemaDataTypeProvider.TOKEN.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid token value '%s'. %s",
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
  static ITokenItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof ITokenItem
          ? (ITokenItem) item
          : valueOf(item.asString());
    } catch (InvalidTypeMetapathException ex) {
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default ITokenItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }
}
