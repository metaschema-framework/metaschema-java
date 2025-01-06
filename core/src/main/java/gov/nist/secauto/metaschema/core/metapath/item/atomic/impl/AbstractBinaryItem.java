/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.AbstractAnyAtomicItem;

import java.nio.ByteBuffer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of a Metapath atomic item containing a binary data value.
 */
public abstract class AbstractBinaryItem
    extends AbstractAnyAtomicItem<ByteBuffer>
    implements IBinaryItem {

  /**
   * Construct a new item with the provided {@code value}.
   *
   * @param value
   *          the value to wrap
   */
  public AbstractBinaryItem(@NonNull ByteBuffer value) {
    super(value);
  }

  @Override
  public ByteBuffer asByteBuffer() {
    return getValue();
  }

  @Override
  protected String getValueSignature() {
    return "'" + asString() + "'";
  }
}
