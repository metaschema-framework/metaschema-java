/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.Base64Adapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.AbstractAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import java.nio.ByteBuffer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a Base64 encoded data
 * value.
 */
public class Base64BinaryItemImpl
    extends AbstractAnyAtomicItem<ByteBuffer>
    implements IBase64BinaryItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public Base64BinaryItemImpl(@NonNull ByteBuffer value) {
    super(value);
  }

  @Override
  public Base64Adapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.BASE64;
  }

  @Override
  public ByteBuffer asByteBuffer() {
    return getValue();
  }

  @Override
  protected String getValueSignature() {
    return "'" + asString() + "'";
  }

  @Override
  public IMapKey asMapKey() {
    return new MapKey();
  }

  @Override
  public int hashCode() {
    return asByteBuffer().hashCode();
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof IBase64BinaryItem && compareTo((IBase64BinaryItem) obj) == 0;
  }

  private final class MapKey implements IMapKey {
    @Override
    public IBase64BinaryItem getKey() {
      return Base64BinaryItemImpl.this;
    }

    @Override
    public int hashCode() {
      return getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj ||
          obj instanceof MapKey
              && getKey().equals(((MapKey) obj).getKey());
    }
  }
}
