/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic.impl;

import gov.nist.secauto.metaschema.core.datatype.adapter.AbstractBinaryAdapter;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import java.nio.ByteBuffer;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Represents a binary string of bytes.
 */
public interface IBinaryItem extends IAnyAtomicItem {

  /**
   * Get the "wrapped" byte buffer value.
   *
   * @return the underlying byte buffer value
   */
  @NonNull
  ByteBuffer asByteBuffer();

  /**
   * Get the underlying bytes for this item.
   *
   * @return the array of bytes
   */
  @NonNull
  default byte[] asBytes() {
    return AbstractBinaryAdapter.bufferToBytes(asByteBuffer(), true);
  }
}
