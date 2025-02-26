/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.ComparisonFunctions;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.ICollectionValue;
import gov.nist.secauto.metaschema.core.metapath.item.IItemVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The interface shared by all atomic items, representing indivisible data
 * values that serve as the fundamental building blocks for complex data
 * structures in the Metaschema framework.
 */
public interface IAnyAtomicItem extends IAtomicValuedItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<?> type() {
    return IItemType.anyAtomic();
  }

  @Override
  @NonNull
  default IAnyAtomicItem toAtomicItem() {
    return this;
  }

  /**
   * Get the "wrapped" value represented by this item.
   *
   * @return the value
   */
  @Override
  @NonNull
  Object getValue();

  /**
   * Converts this atomic item to a string item representation.
   *
   * @return a new {@link IStringItem} containing the string representation of
   *         this item
   * @see #asString()
   */
  @NonNull
  default IStringItem asStringItem() {
    return IStringItem.valueOf(asString());
  }

  /**
   * Get the item's string value.
   *
   * @return the string value value of the item
   */
  @NonNull
  String asString();

  @Override
  default Stream<IAnyAtomicItem> atomize() {
    return ObjectUtils.notNull(Stream.of(this));
  }

  /**
   * Get the atomic item value as a map key for use with an {@link IMapItem}.
   *
   * @return the map key
   */
  @NonNull
  IMapKey asMapKey();

  /**
   * Get the item's type adapter.
   *
   * @return the type adapter for the item
   */
  @NonNull
  IDataTypeAdapter<?> getJavaTypeAdapter();

  /**
   * Cast the provided type to this item type.
   * <p>
   * This method simply returns the provided item, since it is already the same
   * type.
   *
   * @param item
   *          the item to cast
   * @return the provided item
   */
  static IAnyAtomicItem cast(@NonNull IAnyAtomicItem item) {
    return item;
  }

  /**
   * Cast the provided {@code item} to be the same type as this item.
   *
   * @param item
   *          the item to cast
   * @return an atomic item of this type
   * @throws InvalidValueForCastFunctionException
   *           if the provided item type cannot be cast to this item type
   */
  @NonNull
  IAnyAtomicItem castAsType(@NonNull IAnyAtomicItem item);

  @Override
  default boolean deepEquals(ICollectionValue other, DynamicContext dynamicContext) {
    boolean retval;
    try {
      retval = other instanceof IAnyAtomicItem
          && ComparisonFunctions.valueCompairison(
              this,
              ComparisonFunctions.Operator.EQ,
              (IAnyAtomicItem) other,
              dynamicContext)
              .toBoolean();
    } catch (@SuppressWarnings("unused") InvalidTypeMetapathException ex) {
      // incompatible types are a non-match
      retval = false;
    }
    return retval;
  }

  @Override
  default void accept(IItemVisitor visitor) {
    visitor.visit(this);
  }
}
