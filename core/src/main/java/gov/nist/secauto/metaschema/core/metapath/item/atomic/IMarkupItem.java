/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.markup.IMarkupString;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupDataTypeProvider;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupLine;
import gov.nist.secauto.metaschema.core.datatype.markup.MarkupMultiline;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.MarkupLineItemImpl;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.MarkupMultiLineItemImpl;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item representing a Markup data value.
 */
public interface IMarkupItem extends IUntypedAtomicItem {
  /**
   * Construct a new item using the provided {@code value}.
   *
   * @param value
   *          a line of markup
   * @return the new item
   */
  @SuppressWarnings("PMD.AvoidCatchingGenericException")
  @NonNull
  static IMarkupItem valueOf(@NonNull String value) {
    try {
      return valueOf(MarkupDataTypeProvider.MARKUP_LINE.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

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
    try {
      return item instanceof IMarkupItem
          ? (IMarkupItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
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
