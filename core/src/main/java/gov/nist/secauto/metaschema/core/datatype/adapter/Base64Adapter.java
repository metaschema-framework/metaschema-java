/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;

import gov.nist.secauto.metaschema.core.datatype.AbstractDataTypeAdapter;
import gov.nist.secauto.metaschema.core.metapath.MetapathConstants;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Base64Adapter
    extends AbstractDataTypeAdapter<ByteBuffer, IBase64BinaryItem> {
  @NonNull
  private static final List<QName> NAMES = ObjectUtils.notNull(
      List.of(
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "base64"),
          // for backwards compatibility with original type name
          new QName(MetapathConstants.NS_METAPATH.toASCIIString(), "base64Binary")));

  Base64Adapter() {
    super(ByteBuffer.class);
  }

  @Override
  public List<QName> getNames() {
    return NAMES;
  }

  @Override
  public JsonFormatTypes getJsonRawType() {
    return JsonFormatTypes.STRING;
  }

  @SuppressWarnings("null")
  @Override
  public ByteBuffer parse(String value) {
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] result = decoder.decode(value);
    return ByteBuffer.wrap(result);
  }

  @Override
  public ByteBuffer copy(Object obj) {
    ByteBuffer buffer = (ByteBuffer) obj;
    final ByteBuffer clone
        = buffer.isDirect() ? ByteBuffer.allocateDirect(buffer.capacity()) : ByteBuffer.allocate(buffer.capacity());
    final ByteBuffer readOnlyCopy = buffer.asReadOnlyBuffer();
    readOnlyCopy.flip();
    clone.put(readOnlyCopy);
    return clone;
  }

  @SuppressWarnings("null")
  @Override
  public String asString(Object value) {
    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(((ByteBuffer) value).array());
  }

  @Override
  public Class<IBase64BinaryItem> getItemClass() {
    return IBase64BinaryItem.class;
  }

  @Override
  public IBase64BinaryItem newItem(Object value) {
    ByteBuffer item = toValue(value);
    return IBase64BinaryItem.valueOf(item);
  }

}
