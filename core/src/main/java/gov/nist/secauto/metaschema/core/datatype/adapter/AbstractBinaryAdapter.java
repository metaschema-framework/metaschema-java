/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.type.AbstractAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Maintains a byte buffer representation of a byte stream.
 * <p>
 * The maintained byte stream is kept in a decoded form.
 *
 * @param <ITEM_TYPE>
 *          the metapath item type supported by the adapter
 */
public abstract class AbstractBinaryAdapter<ITEM_TYPE extends IAnyAtomicItem>
    extends AbstractDataTypeAdapter<ByteBuffer, ITEM_TYPE> {

  /**
   * Construct a new Java type adapter for a provided class.
   *
   * @param itemClass
   *          the Java type of the Matepath item this adapter supports
   * @param castExecutor
   *          the method to call to cast an item to an item based on this type
   */
  protected AbstractBinaryAdapter(
      @NonNull Class<ITEM_TYPE> itemClass,
      @NonNull AbstractAtomicOrUnionType.ICastExecutor<ITEM_TYPE> castExecutor) {
    super(ByteBuffer.class, itemClass, castExecutor);
  }

  @NonNull
  protected abstract BinaryDecoder getDecoder();

  @NonNull
  protected abstract BinaryEncoder getEncoder();

  @NonNull
  private static byte[] stringToBytes(@NonNull String text) {
    return text.getBytes(StandardCharsets.UTF_8);
  }

  @NonNull
  private static String bytesToString(@NonNull byte[] bytes) {
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private static String elide(@NonNull String text, int length) {
    return text.length() <= length ? text : text.substring(0, length) + "…";
  }

  @NonNull
  public byte[] encode(@NonNull byte[] decodedBytes) {
    try {
      return ObjectUtils.notNull(getEncoder().encode(decodedBytes));
    } catch (EncoderException ex) {
      throw new IllegalArgumentException(
          String.format("unable to encode text '%s'", elide(bytesToString(decodedBytes), 64)),
          ex);
    }
  }

  @NonNull
  public ByteBuffer encodeToByteBuffer(@NonNull ByteBuffer decodedBuffer) {
    byte[] decodedBytes = bufferToBytes(decodedBuffer, false);
    return encodeToByteBuffer(decodedBytes);
  }

  @NonNull
  public ByteBuffer encodeToByteBuffer(String decodedText) {
    byte[] decodedBytes = stringToBytes(decodedText);
    return encodeToByteBuffer(decodedBytes);
  }

  @NonNull
  public ByteBuffer encodeToByteBuffer(byte[] decodedBytes) {
    byte[] encodedBytes = encode(decodedBytes);
    return ObjectUtils.notNull(ByteBuffer.wrap(encodedBytes));
  }

  @NonNull
  public byte[] decode(@NonNull byte[] enodedBytes) {
    try {
      return ObjectUtils.notNull(getDecoder().decode(enodedBytes));
    } catch (DecoderException ex) {
      throw new IllegalArgumentException(
          String.format("unable to decode text '%s'", elide(bytesToString(enodedBytes), 64)),
          ex);
    }
  }

  @NonNull
  public ByteBuffer decode(@NonNull ByteBuffer encodedBuffer) {
    byte[] encodedBytes = bufferToBytes(encodedBuffer, false);
    byte[] decodedBytes = decode(encodedBytes);
    return ObjectUtils.notNull(ByteBuffer.wrap(decodedBytes));
  }

  @NonNull
  public String decodeToString(@NonNull byte[] encodedBytes) {
    byte[] decodedBytes = decode(encodedBytes);
    return bytesToString(decodedBytes);
  }

  /**
   * Decodes the provided string.
   *
   * @return a buffer containing the decoded bytes
   */
  @Override
  public ByteBuffer parse(String encodedString) {
    byte[] encodedBytes = stringToBytes(encodedString);
    // byte[] decodedBytes = decode(encodedBytes);
    // return ObjectUtils.notNull(ByteBuffer.wrap(decodedBytes));
    return ObjectUtils.notNull(ByteBuffer.wrap(encodedBytes));
  }

  /**
   * Get the wrapped value as a base64 encoded string.
   * <p>
   * Encodes the wrapped value to produce a string.
   *
   * @return the base64 encoded value
   */
  @Override
  public String asString(Object encodedBuffer) {
    byte[] encodedBytes = bufferToBytes((ByteBuffer) encodedBuffer, false);
    return new String(encodedBytes, StandardCharsets.UTF_8);
    // return bytesToString(encodedBytes);

    // byte[] decodedBytes = bufferToBytes((ByteBuffer) decodedBuffer, false);
    // byte[] encodedBytes = encode(decodedBytes);
    // return bytesToString(encodedBytes);
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Override
  public ByteBuffer copy(Object obj) {
    ByteBuffer buffer = (ByteBuffer) obj;
    ByteBuffer clone = buffer.isDirect()
        ? ByteBuffer.allocateDirect(buffer.capacity())
        : ByteBuffer.allocate(buffer.capacity());
    ByteBuffer readOnlyCopy = buffer.asReadOnlyBuffer();
    readOnlyCopy.flip();
    clone.put(readOnlyCopy);
    return clone;
  }

  @NonNull
  public static byte[] bufferToBytes(@NonNull ByteBuffer buffer, boolean copy) {
    byte[] array;
    if (buffer.hasArray()) {
      array = buffer.array();
      if (copy) {
        array = Arrays.copyOf(array, array.length);
      }
    } else {
      // Handle direct buffers
      array = new byte[buffer.remaining()];
      buffer.mark();
      try {
        buffer.get(array);
      } finally {
        buffer.reset();
      }
    }
    return ObjectUtils.notNull(array);
  }
}
