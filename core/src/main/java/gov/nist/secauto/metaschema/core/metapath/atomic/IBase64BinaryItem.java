/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.atomic;

import gov.nist.secauto.metaschema.core.datatype.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.atomic.impl.Base64BinaryItemImpl;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import java.nio.ByteBuffer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a Base64 encoded data value.
 */
public interface IBase64BinaryItem extends IAnyAtomicItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IBase64BinaryItem> type() {
    return MetaschemaDataTypeProvider.BASE64.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IBase64BinaryItem> getType() {
    return type();
  }

  /**
   * Construct a new base64 encoded byte sequence item using the provided string
   * {@code value}.
   *
   * @param value
   *          a string representing base64 encoded data
   * @return the new item
   * @throws InvalidTypeMetapathException
   *           if the provided string is not a valid Base64 character sequence
   */
  @NonNull
  static IBase64BinaryItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.BASE64.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("The value starting with '%s' is not a valid Base64 character sequence. %s",
              value.substring(0, Math.min(value.length(), 200)),
              ex.getLocalizedMessage()),
          ex);
    }
  }

  /**
   * Construct a new URI base64 encoded byte sequence using the provided
   * {@link ByteBuffer} {@code value}.
   *
   * @param value
   *          a byte buffer
   * @return the new item
   */
  @NonNull
  static IBase64BinaryItem valueOf(@NonNull ByteBuffer value) {
    return new Base64BinaryItemImpl(value);
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
  static IBase64BinaryItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IBase64BinaryItem
          ? (IBase64BinaryItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IBase64BinaryItem castAsType(IAnyAtomicItem item) {
    return cast(item);
  }

  /**
   * Get the "wrapped" byte buffer value.
   *
   * @return the underlying byte buffer value
   */
  @NonNull
  ByteBuffer asByteBuffer();

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
  default int compareTo(@NonNull IBase64BinaryItem item) {
    return asByteBuffer().compareTo(item.asByteBuffer());
  }
}
