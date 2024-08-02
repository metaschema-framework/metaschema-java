/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

class IBase64BinaryItemTest {
  private static final long MIN_LONG = -9_223_372_036_854_775_808L;
  private static final long MAX_LONG = 9_223_372_036_854_775_807L;
  private static final String BASE_64 = "gAAAAAAAAAB//////////w==";

  @Test
  void testValueOf() {
    IBase64BinaryItem item = IBase64BinaryItem.valueOf(ObjectUtils.notNull(
        ByteBuffer.allocate(16).putLong(MIN_LONG).putLong(MAX_LONG)));
    assertEquals(BASE_64, item.asString());
  }

  @Test
  void testCastSame() {
    ByteBuffer buf
        = ObjectUtils.notNull(ByteBuffer.allocate(16).putLong(MIN_LONG).putLong(MAX_LONG));
    IBase64BinaryItem item = IBase64BinaryItem.valueOf(buf);
    assertEquals(IBase64BinaryItem.cast(item), item);
  }

  @Test
  void testCastString() {
    ByteBuffer buf
        = ObjectUtils.notNull(ByteBuffer.allocate(16).putLong(MIN_LONG).putLong(MAX_LONG));
    IBase64BinaryItem expected = IBase64BinaryItem.valueOf(buf);
    IBase64BinaryItem actual = IBase64BinaryItem.cast(IStringItem.valueOf(BASE_64));
    Assertions.assertAll(
        // TODO: use equals method?
        () -> assertArrayEquals(actual.asByteBuffer().array(), expected.asByteBuffer().array()),
        () -> assertEquals(actual.asString(), expected.asString()));
  }
}
