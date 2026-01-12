/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic.impl;

import java.nio.ByteBuffer;

import dev.metaschema.core.datatype.adapter.HexBinaryAdapter;
import dev.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import dev.metaschema.core.metapath.impl.AbstractMapKey;
import dev.metaschema.core.metapath.item.atomic.IHexBinaryItem;
import dev.metaschema.core.metapath.item.function.IMapKey;
import dev.metaschema.core.metapath.item.function.IOpaqueMapKey;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a Base64 encoded data
 * value.
 */
public class HexBinaryItem
    extends AbstractBinaryItem
    implements IHexBinaryItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public HexBinaryItem(@NonNull ByteBuffer value) {
    super(value);
  }

  @Override
  public HexBinaryAdapter getJavaTypeAdapter() {
    return MetaschemaDataTypeProvider.HEX_BINARY;
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
        || obj instanceof IHexBinaryItem && compareTo((IHexBinaryItem) obj) == 0;
  }

  private final class MapKey
      extends AbstractMapKey
      implements IOpaqueMapKey {
    @Override
    public IHexBinaryItem getKey() {
      return HexBinaryItem.this;
    }
  }
}
