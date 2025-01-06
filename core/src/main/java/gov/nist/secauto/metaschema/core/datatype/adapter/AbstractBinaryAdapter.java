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

  /**
   * Get the binary decoder to use to decode encoded data.
   *
   * @return the decoder
   */
  @NonNull
  protected abstract BinaryDecoder getDecoder();

  /**
   * Get the binary encoder to use to encode data.
   *
   * @return the encoder
   */
  @NonNull
  protected abstract BinaryEncoder getEncoder();

  /**
   * Get the raw bytes, encoded as UTF8, for the provided text string.
   *
   * @param text
   *          the text string to get the bytes for
   * @return the UTF8 encoded bytes for the text string
   */
  @NonNull
  private static byte[] stringToBytes(@NonNull String text) {
    return ObjectUtils.notNull(text.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Get a text string based on the provided raw bytes, encoded as UTF8.
   *
   * @param bytes
   *          a byte array encoded as UTF8
   * @return the decoded text string
   */
  @NonNull
  private static String bytesToString(@NonNull byte[] bytes) {
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private static String elide(@NonNull String text, int length) {
    return text.length() <= length ? text : text.substring(0, length) + "…";
  }

  /**
   * Encode the provided bytes using the encoding supported by this class.
   *
   * @param decodedBytes
   *          the bytes to encode
   * @return the encoded bytes
   * @see #getEncoder()
   */
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

  /**
   * Encode the provided bytes using the encoding supported by this class.
   *
   * @param decodedBuffer
   *          a buffer containing the bytes to encode
   * @return a buffer containing the encoded bytes
   * @see #getEncoder()
   */
  @NonNull
  public ByteBuffer encodeToByteBuffer(@NonNull ByteBuffer decodedBuffer) {
    byte[] decodedBytes = bufferToBytes(decodedBuffer, false);
    return encodeToByteBuffer(decodedBytes);
  }

  /**
   * Encode the provided string using the encoding supported by this class.
   * <p>
   * The provided string is first encoded as a stream of UTF8 bytes.
   *
   * @param decodedText
   *          the string to encode
   * @return a buffer containing the encoded bytes
   * @see #getEncoder()
   */
  @NonNull
  public ByteBuffer encodeToByteBuffer(@NonNull String decodedText) {
    byte[] decodedBytes = stringToBytes(decodedText);
    return encodeToByteBuffer(decodedBytes);
  }

  /**
   * Encode the provided bytes using the encoding supported by this class.
   *
   * @param decodedBytes
   *          the bytes to encode
   * @return a buffer containing the encoded bytes
   * @see #getEncoder()
   */
  @NonNull
  public ByteBuffer encodeToByteBuffer(@NonNull byte[] decodedBytes) {
    byte[] encodedBytes = encode(decodedBytes);
    return ObjectUtils.notNull(ByteBuffer.wrap(encodedBytes));
  }

  /**
   * Decode the provided bytes using the encoding supported by this class.
   *
   * @param enodedBytes
   *          the bytes to decode
   * @return the decoded bytes
   * @see #getDecoder()
   */
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

  /**
   * Decode the provided bytes using the encoding supported by this class.
   *
   * @param encodedBuffer
   *          a buffer containing the the bytes to decode
   * @return a buffer containing the decoded bytes
   * @see #getDecoder()
   */
  @NonNull
  public ByteBuffer decode(@NonNull ByteBuffer encodedBuffer) {
    byte[] encodedBytes = bufferToBytes(encodedBuffer, false);
    byte[] decodedBytes = decode(encodedBytes);
    return ObjectUtils.notNull(ByteBuffer.wrap(decodedBytes));
  }

  /**
   * Decode the provided bytes using the encoding supported by this class.
   * <p>
   * The decoded bytes are decoded as a stream of UTF8 bytes to produce the
   * string.
   *
   * @param encodedBytes
   *          the bytes to decode
   * @return the decoded string
   * @see #getDecoder()
   */
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

  /**
   * Get the array of bytes stored in the buffer.
   *
   * @param buffer
   *          the buffer
   * @param copy
   *          if {@code true} ensure the resulting array is a copy
   * @return the array of bytes
   */
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
