/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.HexBinaryAdapter;
import gov.nist.secauto.metaschema.core.datatype.adapter.MetaschemaDataTypeProvider;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IHexBinaryItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IMapKey;

import java.nio.ByteBuffer;

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

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    return this == obj
        || obj instanceof IHexBinaryItem && compareTo((IHexBinaryItem) obj) == 0;
  }

  private final class MapKey implements IMapKey {
    @Override
    public IHexBinaryItem getKey() {
      return HexBinaryItem.this;
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
