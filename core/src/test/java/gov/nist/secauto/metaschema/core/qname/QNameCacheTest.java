/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.qname;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class QNameCacheTest {
  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of("", "name1"),
        Arguments.of("https://example.com/ns", "name1"),
        Arguments.of("https://example.com/ns", "name2"),
        Arguments.of("", "name2"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull String namespace, @NonNull String localName) {
    QNameCache cache = QNameCache.instance();

    IEnhancedQName qname = cache.of(namespace, localName);
    IEnhancedQName lookup = cache.get(namespace, localName);

    assertAll(
        () -> assertNotNull(lookup),
        () -> assertEquals(qname, lookup, "Expected to retrieve the same QName record"),
        () -> assertEquals(namespace, lookup == null ? null : lookup.getNamespace()),
        () -> assertEquals(localName, lookup == null ? null : lookup.getLocalName()));
  }

}
