/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import gov.nist.secauto.metaschema.core.qname.EQNameFactory;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Maintains a byte buffer backed representation of a byte stream parsed from a base64 encoded
 * string.
 * <p>
 * Provides support for the Metaschema
 * <a href= "https://pages.nist.gov/metaschema/specification/datatypes/#base64">base64</a> data
 * type.
 */
public class Base64Adapter
    extends AbstractDataTypeAdapter<ByteBuffer, IBase64BinaryItem> {
  @NonNull
  private static final List<IEnhancedQName> NAMES = ObjectUtils.notNull(
      List.of(
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "base64"),
          // for backwards compatibility with original type name
          EQNameFactory.instance().newQName(MetapathConstants.NS_METAPATH, "base64Binary")));

  Base64Adapter() {
    super(ByteBuffer.class, IBase64BinaryItem.class, IBase64BinaryItem::cast);
  }

  @Override
  public List<IEnhancedQName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @Override
  public ByteBuffer parse(String value) {
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] result = decoder.decode(value);
    return ObjectUtils.notNull(ByteBuffer.wrap(result));
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
   * Get the wrapped value as a base64 encoded string.
   *
   * @return the base64 encoded value
   */
  @Override
  public String asString(Object value) {
    byte[] array = bufferToBytes((ByteBuffer) value, false);
    return ObjectUtils.notNull(Base64.getEncoder().encodeToString(array));
  }

  @Override
  public IBase64BinaryItem newItem(Object value) {
    ByteBuffer item = toValue(value);
    return IBase64BinaryItem.valueOf(item);
  }

  @NonNull
  public static ByteBuffer encode(@NonNull ByteBuffer decodedBuffer) {
    return Base64.getEncoder().encode(decodedBuffer);
  }

  @NonNull
  public static ByteBuffer encodeToByteBuffer(@NonNull String decodedString) {
    return encodeToByteBuffer(decodedString.getBytes());
  }

  @NonNull
  public static ByteBuffer encodeToByteBuffer(@NonNull byte[] bytes) {
    byte[] encodedBytes = Base64.getEncoder().encode(bytes);
    return ByteBuffer.wrap(encodedBytes);
  }

  @NonNull
  public static ByteBuffer decode(@NonNull ByteBuffer encodedBuffer) {
    return Base64.getDecoder().decode(encodedBuffer);
  }

  @NonNull
  public static String decodeToString(@NonNull ByteBuffer encodedBuffer) {
    ByteBuffer decodedBuffer = decode(encodedBuffer);
    byte[] decodedBytes = bufferToBytes(decodedBuffer, true);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  @NonNull
  public static String decodeToString(@NonNull String encodedString) {
    byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  @NonNull
  public ByteBuffer stringToByteBuffer(@NonNull String text) {
    return ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));
  }

  @NonNull
  public String byteBufferToString(@NonNull ByteBuffer buffer) {
    byte[] bytes = bufferToBytes(buffer, false);
    return new String(bytes, StandardCharsets.UTF_8);
  }

  private static byte[] bufferToBytes(@NonNull ByteBuffer buffer, boolean copy) {
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
    return array;
  }
}
