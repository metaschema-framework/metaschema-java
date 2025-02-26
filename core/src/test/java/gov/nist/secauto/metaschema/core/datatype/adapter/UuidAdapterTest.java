/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class UuidAdapterTest {
  private static final UuidAdapter ADAPTER = new UuidAdapter();

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(
            "f0b23719-2687-407c-b22f-f4b7ee832571"),
        Arguments.of(
            "8eb2fb0f-47ac-40be-adb0-1a70fe7fad76"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testParseUUID(@NonNull String expected) {
    UUID uuid = ADAPTER.parse(expected);
    assertAll(
        () -> assertEquals(expected, uuid.toString()),
        () -> assertEquals(expected, ADAPTER.asString(uuid)));
  }
}
