/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic;

import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import dev.metaschema.core.metapath.item.atomic.impl.QNameItemImpl;
import dev.metaschema.core.metapath.type.IAtomicOrUnionType;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;
import dev.metaschema.core.qname.EQNameFactory;
import dev.metaschema.core.qname.IEnhancedQName;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item with a namespace qualified name value.
 */
public interface IQNameItem extends IAnyAtomicItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IQNameItem> type() {
    return MetaschemaDataTypeProvider.QNAME.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IQNameItem> getType() {
    return type();
  }

  /**
   * Construct a new QName item using the provided string {@code value}.
   *
   * @param value
   *          a string representing a QName value
   * @return the new item
   */
  // TODO: Review metaschema-framework/metaschema-java#396 and change accordingly.
  @NonNull
  static IQNameItem valueOf(@NonNull String value) {
    return valueOf(EQNameFactory.instance().parseUriQualifiedName(value));
  }

  /**
   * Construct a new QName item using the provided {@code value}.
   *
   * @param value
   *          a QName
   * @return the new item
   */
  @NonNull
  static IQNameItem valueOf(@NonNull IEnhancedQName value) {
    return new QNameItemImpl(value);
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
  static IQNameItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IQNameItem
          ? (IQNameItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IQNameItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Get the "wrapped" EnhancedQName value.
   *
   * @return the underlying QName value
   */
  @NonNull
  IEnhancedQName toEnhancedQName();

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(@NonNull IQNameItem item) {
    return toEnhancedQName().compareTo(item.toEnhancedQName());
  }
}
