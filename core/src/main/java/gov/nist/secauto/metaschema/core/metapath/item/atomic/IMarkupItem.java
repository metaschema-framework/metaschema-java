/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.markup.IMarkupString;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import edu.umd.cs.findbugs.annotations.NonNull;

public interface IMarkupItem extends IUntypedAtomicItem {
  /**
   * Construct a new item using the provided {@code value}.
   *
   * @param value
   *          a line of markup
   * @return the new item
   */
  @NonNull
  static IMarkupItem valueOf(@NonNull MarkupLine value) {
    return new MarkupLineItemImpl(value);
  }

  /**
   * Construct a new item using the provided {@code value}.
   *
   * @param value
   *          multiple lines of markup
   * @return the new item
   */
  @NonNull
  static IMarkupItem valueOf(@NonNull MarkupMultiline value) {
    return new MarkupMultiLineItemImpl(value);
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
  static IMarkupItem cast(@NonNull IAnyAtomicItem item) {
    return MarkupDataTypeProvider.MARKUP_MULTILINE.cast(item);
  }

  /**
   * Get the "wrapped" markup value.
   *
   * @return the underlying markup value
   */
  @NonNull
  IMarkupString<?> asMarkup();

  @Override
  default IMarkupItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  @Override
  default int compareTo(IAnyAtomicItem item) {
    return compareTo(cast(item));
  }

  /**
   * Compares this value with the argument.
   *
   * @param item
   *          the item to compare with this value
   * @return a negative integer, zero, or a positive integer if this value is less
   *         than, equal to, or greater than the {@code item}.
   */
  default int compareTo(@NonNull IMarkupItem item) {
    return asString().compareTo(item.asString());
  }
}
