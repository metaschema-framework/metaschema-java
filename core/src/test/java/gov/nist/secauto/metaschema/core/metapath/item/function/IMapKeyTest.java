/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.uri;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class IMapKeyTest {
  private static Stream<Arguments> stringKeyValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            true,
            string("https://example.com/resource"),
            string("https://example.com/resource")),
        Arguments.of(
            true,
            string("https://example.com/resource"),
            uri("https://example.com/resource")),
        Arguments.of(
            false,
            string("https://example.com/resource"),
            uri("https://example.com/other-resource")));
  }

  @ParameterizedTest
  @MethodSource("stringKeyValues")
  void testStringKeys(boolean expectedEquality, @NonNull IAnyAtomicItem item1, @NonNull IAnyAtomicItem item2) {
    IMapKey key1 = item1.asMapKey();
    IMapKey key2 = item2.asMapKey();

    assertAll(
        () -> assertEquals(expectedEquality, key1.equals(key2)),
        () -> assertEquals(expectedEquality, Objects.equals(key1.hashCode(), key2.hashCode())));
  }
}
