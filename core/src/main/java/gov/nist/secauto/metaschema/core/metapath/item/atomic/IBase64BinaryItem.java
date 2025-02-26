/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.Base64BinaryItemImpl;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.IBinaryItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import java.nio.ByteBuffer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a Base64 encoded data value.
 */
public interface IBase64BinaryItem extends IBinaryItem {
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
   * Base64 encode the provided string.
   * <p>
   * The provided string is first encoded as a stream of UTF8 bytes.
   *
   * @param text
   *          the string to encode
   * @return a base64 item representing the encoded data
   */
  static IBase64BinaryItem encode(@NonNull IStringItem text) {
    return encode(text.asString());
  }

  /**
   * Base64 encode the provided string.
   * <p>
   * The provided string is first encoded as a stream of UTF8 bytes.
   *
   * @param text
   *          the string to encode
   * @return a base64 item representing the encoded data
   */
  static IBase64BinaryItem encode(@NonNull String text) {
    return valueOf(MetaschemaDataTypeProvider.BASE64.encodeToByteBuffer(text));
  }

  /**
   * Base64 encode the provided bytes.
   *
   * @param bytes
   *          the bytes to encode
   * @return a base64 item representing the encoded data
   */
  @NonNull
  static IBase64BinaryItem encode(@NonNull byte[] bytes) {
    return valueOf(MetaschemaDataTypeProvider.BASE64.encodeToByteBuffer(bytes));
  }

  /**
   * Base64 encode the bytes from the provided buffer.
   *
   * @param buffer
   *          the bytes to encode
   * @return a base64 item representing the encoded data
   */
  @NonNull
  static IBase64BinaryItem encode(@NonNull ByteBuffer buffer) {
    return valueOf(MetaschemaDataTypeProvider.BASE64.encodeToByteBuffer(buffer));
  }

  /**
   * Base64 decode this item as a new hex binary item.
   *
   * @return a new hex binary item containing the decoded bytes
   */
  default IHexBinaryItem decode() {
    return IHexBinaryItem.valueOf(MetaschemaDataTypeProvider.BASE64.decode(asByteBuffer()));
  }

  /**
   * Base64 decode this item as a string.
   *
   * @return a new string item containing the decoded text
   */
  default IStringItem decodeAsString() {
    return IStringItem.valueOf(MetaschemaDataTypeProvider.BASE64.decodeToString(asBytes()));
  }

  /**
   * Construct a new base64 byte sequence item using the provided base64 encoded
   * string {@code value}.
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
   * <p>
   * The provided buffer will be managed by this instance. Make a copy of the
   * buffer to ensure that the position, limit, and mark of the original are not
   * affect by this.
   *
   * @param buffer
   *          a byte buffer
   * @return the new item
   */
  @NonNull
  static IBase64BinaryItem valueOf(@NonNull ByteBuffer buffer) {
    return new Base64BinaryItemImpl(buffer);
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
