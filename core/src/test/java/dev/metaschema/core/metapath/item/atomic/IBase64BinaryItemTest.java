/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.item.atomic;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class IBase64BinaryItemTest {
  private static final long MIN_LONG = -9_223_372_036_854_775_808L;
  private static final long MAX_LONG = 9_223_372_036_854_775_807L;
  private static final String BASE_64 = "gAAAAAAAAAB//////////w==";

  @Test
  void testValueOf() {
    IBase64BinaryItem item = IBase64BinaryItem.encode(ObjectUtils.notNull(
        ByteBuffer.allocate(16)
            .putLong(MIN_LONG)
            .putLong(MAX_LONG)));
    assertEquals(BASE_64, item.asString());
  }

  @Test
  void testCastSame() {
    ByteBuffer buf = ObjectUtils.notNull(ByteBuffer.allocate(16)
        .putLong(MIN_LONG)
        .putLong(MAX_LONG));
    IBase64BinaryItem item = IBase64BinaryItem.encode(buf);
    assertEquals(IBase64BinaryItem.cast(item), item);
  }

  @Test
  void testCastString() {
    ByteBuffer buf
        = ObjectUtils.notNull(ByteBuffer.allocate(16).putLong(MIN_LONG).putLong(MAX_LONG));
    IBase64BinaryItem expected = IBase64BinaryItem.encode(buf);
    IBase64BinaryItem actual = IBase64BinaryItem.cast(IStringItem.valueOf(BASE_64));
    Assertions.assertAll(
        // TODO: use equals method?
        () -> assertArrayEquals(actual.asByteBuffer().array(), expected.asByteBuffer().array()),
        () -> assertEquals(actual.asString(), expected.asString()));
  }

  private static Stream<Arguments> provideValuesForEncodeDecode() {
    return Stream.of(
        Arguments.of("The quick brown fox jumps over the lazy dog",
            "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZw=="),
        Arguments.of("Mr. Watson, come here, I need you", "TXIuIFdhdHNvbiwgY29tZSBoZXJlLCBJIG5lZWQgeW91"),
        Arguments.of(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eu luctus lorem. Aliquam malesuada lorem"
                + " nisi, ut tincidunt neque feugiat vitae. Fusce pretium nunc ac sapien feugiat accumsan. Proin eget"
                + " ligula non turpis laoreet fermentum. Aliquam mi justo, gravida id vulputate id, venenatis eu felis."
                + " Vestibulum commodo, magna quis sollicitudin consectetur, eros erat elementum libero, nec euismod"
                + " elit arcu non diam. Sed iaculis dui lacus, vitae placerat velit iaculis quis. Sed in ligula in eros"
                + " luctus porttitor. Nullam in laoreet leo. Cras sed nisl eget turpis sollicitudin molestie et eget"
                + " tellus. Vivamus aliquam odio et dui mattis, in rhoncus mauris hendrerit. Nam viverra mattis risus"
                + " non tristique.",
            "TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdC4gRXRpYW0gZXUgbHVjdHVzIGxvcmVtLi"
                + "BBbGlxdWFtIG1hbGVzdWFkYSBsb3JlbSBuaXNpLCB1dCB0aW5jaWR1bnQgbmVxdWUgZmV1Z2lhdCB2aXRhZS4gRnVzY2UgcHJldG"
                + "l1bSBudW5jIGFjIHNhcGllbiBmZXVnaWF0IGFjY3Vtc2FuLiBQcm9pbiBlZ2V0IGxpZ3VsYSBub24gdHVycGlzIGxhb3JlZXQgZm"
                + "VybWVudHVtLiBBbGlxdWFtIG1pIGp1c3RvLCBncmF2aWRhIGlkIHZ1bHB1dGF0ZSBpZCwgdmVuZW5hdGlzIGV1IGZlbGlzLiBWZX"
                + "N0aWJ1bHVtIGNvbW1vZG8sIG1hZ25hIHF1aXMgc29sbGljaXR1ZGluIGNvbnNlY3RldHVyLCBlcm9zIGVyYXQgZWxlbWVudHVtIG"
                + "xpYmVybywgbmVjIGV1aXNtb2QgZWxpdCBhcmN1IG5vbiBkaWFtLiBTZWQgaWFjdWxpcyBkdWkgbGFjdXMsIHZpdGFlIHBsYWNlcm"
                + "F0IHZlbGl0IGlhY3VsaXMgcXVpcy4gU2VkIGluIGxpZ3VsYSBpbiBlcm9zIGx1Y3R1cyBwb3J0dGl0b3IuIE51bGxhbSBpbiBsYW"
                + "9yZWV0IGxlby4gQ3JhcyBzZWQgbmlzbCBlZ2V0IHR1cnBpcyBzb2xsaWNpdHVkaW4gbW9sZXN0aWUgZXQgZWdldCB0ZWxsdXMuIF"
                + "ZpdmFtdXMgYWxpcXVhbSBvZGlvIGV0IGR1aSBtYXR0aXMsIGluIHJob25jdXMgbWF1cmlzIGhlbmRyZXJpdC4gTmFtIHZpdmVycm"
                + "EgbWF0dGlzIHJpc3VzIG5vbiB0cmlzdGlxdWUu"));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForEncodeDecode")
  void testEncodeDecodeEncode(@NonNull String expectedDecodedString, @NonNull String expectedEncodedString) {
    IStringItem decodedText = IStringItem.valueOf(expectedDecodedString);

    // test encode
    IBase64BinaryItem encodedText = IBase64BinaryItem.encode(decodedText);
    assertAll(
        () -> assertEquals(expectedDecodedString, decodedText.asString(), "Expected same initial decoded text"),
        () -> assertEquals(expectedEncodedString, encodedText.asString(), "Expected same encoded text"),
        // test decode
        () -> assertEquals(expectedDecodedString, encodedText.decodeAsString().asString(),
            "Expected same decoded text on first attempt"),
        // test decode #2
        () -> assertEquals(expectedDecodedString, encodedText.decodeAsString().asString(),
            "Expected same decoded text on second attempt"));
  }
}
