/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.IPrintable;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.IItemVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The interface shared by all atomic items, representing indivisible data
 * values that serve as the fundamental building blocks for complex data
 * structures in the Metaschema framework.
 */
public interface IAnyAtomicItem extends IAtomicValuedItem, IPrintable {

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

  @Override
  @NonNull
  String toString();

  /**
   * Get the item's string value.
   *
   * @return the string value value of the item
   */
  @Override
  @NonNull
  String asString();

  /**
   * Get the atomic item value as a map key for use with an {@link IMapItem}.
   *
   * @return the map key
   */
  @NonNull
  IMapKey asMapKey();

  /**
   * Get a new {@link IStringItem} based on the the textual value of the item's
   * "wrapped" value.
   *
   * @return a new string item
   */
  @NonNull
  default IStringItem asStringItem() {
    return IStringItem.valueOf(asString());
  }

  /**
   * Get the item's type adapter.
   *
   * @return the type adapter for the item
   */
  @NonNull
  IDataTypeAdapter<?> getJavaTypeAdapter();
  //
  // <T extends IValuedItem> T cast(IValuedItem item);

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

  /**
   * Compares this value with the argument. Ordering is item type dependent.
   *
   * @param other
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  int compareTo(@NonNull IAnyAtomicItem other);

  @Override
  default void accept(IItemVisitor visitor) {
    visitor.visit(this);
  }
}
