/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.HexBinaryItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.impl.IBinaryItem;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;

import java.nio.ByteBuffer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An atomic Metapath item containing a Base64 encoded data value.
 */
public interface IHexBinaryItem extends IBinaryItem {
  /**
   * Get the type information for this item.
   *
   * @return the type information
   */
  @NonNull
  static IAtomicOrUnionType<IHexBinaryItem> type() {
    return MetaschemaDataTypeProvider.HEX_BINARY.getItemType();
  }

  @Override
  default IAtomicOrUnionType<IHexBinaryItem> getType() {
    return type();
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
  static IHexBinaryItem valueOf(@NonNull String value) {
    try {
      return valueOf(MetaschemaDataTypeProvider.BASE64.parse(value));
    } catch (IllegalArgumentException ex) {
      throw new InvalidTypeMetapathException(
          null,
          String.format("The value starting with '%s' is not a valid hex encoded character sequence. %s",
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
  static IHexBinaryItem valueOf(@NonNull ByteBuffer buffer) {
    return new HexBinaryItem(buffer);
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
  static IHexBinaryItem cast(@NonNull IAnyAtomicItem item) {
    try {
      return item instanceof IHexBinaryItem
          ? (IHexBinaryItem) item
          : valueOf(item.asString());
    } catch (IllegalStateException | InvalidTypeMetapathException ex) {
      // asString can throw IllegalStateException exception
      throw new InvalidValueForCastFunctionException(ex);
    }
  }

  @Override
  default IHexBinaryItem castAsType(IAnyAtomicItem item) {
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
  default int compareTo(@NonNull IHexBinaryItem item) {
    return asByteBuffer().compareTo(item.asByteBuffer());
  }
}
