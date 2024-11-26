/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.StringItemImpl;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a text data value.
 */
public interface IStringItem extends IAnyAtomicItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IStringItem> type() {
    return MetaschemaDataTypeProvider.STRING.getItemType();
  }

  @Override
  default IAtomicOrUnionType<? extends IStringItem> getType() {
    return type();
  }

  /**
   * Construct a new item using the provided string {@code value}.
   *
   * @param value
   *          a string value that must conform to Metaschema string validation
   *          rules
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the value fails string validation
   */
  @NonNull
  static IStringItem valueOf(@NonNull String value) {
    try {
      return new StringItemImpl(MetaschemaDataTypeProvider.STRING.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("Invalid string value '%s'. %s",
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
  static IStringItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IStringItem
          ? (IStringItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IStringItem asStringItem() {
    return this;
  }

  @Override
  default IStringItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Compares this value with the argument. Ordering is in lexical dictionary
   * order.
   *
   * @param other
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(@NonNull IStringItem other) {
    return asString().compareTo(other.asString());
  }

  @Override
  default int compareTo(IAnyAtomicItem other) {
    return compareTo(other.asStringItem());
  }

  /**
   * An implementation of <a href=
   * "https://www.w3.org/TR/xpath-functions-31/#func-normalize-space">fn::normalize-space</a>.
   *
   * @return the normalized string value for this string
   */
  @NonNull
  IStringItem normalizeSpace();

  /**
   * Get the length of the string.
   *
   * @return the length
   */
  default int length() {
    return asString().length();
  }
}
