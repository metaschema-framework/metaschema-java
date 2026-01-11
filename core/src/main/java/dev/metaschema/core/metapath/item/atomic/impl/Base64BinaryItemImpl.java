/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic.impl;

import java.nio.ByteBuffer;

import dev.metaschema.core.datatype.adapter.Base64Adapter;
import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.metapath.impl.AbstractMapKey;
import dev.metaschema.core.metapath.item.atomic.IBase64BinaryItem;
import dev.metaschema.core.metapath.item.function.IMapKey;
import dev.metaschema.core.metapath.item.function.IOpaqueMapKey;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a Base64 encoded data
 * value.
 */
public class Base64BinaryItemImpl
    extends AbstractBinaryItem
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
  public IMapKey asMapKey() {
    return new MapKey();
  }

  @Override
  public int hashCode() {
    return asByteBuffer().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof IBase64BinaryItem && compareTo((IBase64BinaryItem) obj) == 0;
  }

  private final class MapKey
      extends AbstractMapKey
      implements IOpaqueMapKey {
    @Override
    public IBase64BinaryItem getKey() {
      return Base64BinaryItemImpl.this;
    }
  }
}
