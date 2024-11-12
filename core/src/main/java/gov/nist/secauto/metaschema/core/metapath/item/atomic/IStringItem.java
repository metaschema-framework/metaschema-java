/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.StringItemImpl;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath atomic item containing a text data value.
 */
public interface IStringItem extends IAnyAtomicItem {
  /**
   * Construct a new item using the provided string {@code value}.
   *
   * @param value
   *          a string value
   * @return the new item
   */
  @NonNull
  static IStringItem valueOf(@NonNull String value) {
    try {
      return new StringItemImpl(MetaschemaDataTypeProvider.STRING.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("The value '%s' is not a valid IPv4 address. %s",
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
