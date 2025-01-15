/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.date;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dateTime;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.time;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.uri;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.uuid;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class IMapKeyTest {
  private static Stream<Arguments> keyValues() {
    return Stream.of(
        // =================
        // binary-based keys
        // =================
        // TODO: generate some test cases

        // ===================
        // temporal-based keys
        // ===================
        // same keys
        Arguments.of(
            true,
            date("2025-01-13Z"),
            date("2025-01-13Z")),
        Arguments.of(
            true,
            date("2025-01-13"),
            date("2025-01-13")),
        Arguments.of(
            true,
            IDateTimeItem.valueOf(date("2025-01-13Z")),
            dateTime("2025-01-13T00:00:00Z")),
        Arguments.of(
            true,
            dateTime("2025-01-12T23:00:00-01:00"),
            dateTime("2025-01-13T00:00:00Z")),
        Arguments.of(
            true,
            dateTime("2025-01-12T23:00:00"),
            dateTime("2025-01-12T23:00:00")),
        Arguments.of(
            true,
            time("23:00:00"),
            time("23:00:00")),
        Arguments.of(
            true,
            time("24:00:00"),
            time("24:00:00")),
        Arguments.of(
            true,
            time("00:00:00"),
            time("00:00:00")),
        // different keys
        Arguments.of(
            false,
            date("2025-01-13Z"),
            date("2025-01-13")),
        Arguments.of(
            false,
            date("2025-01-13Z"),
            dateTime("2025-01-13T00:00:00Z")),
        Arguments.of(
            false,
            date("2025-01-13Z"),
            time("00:00:00Z")),
        Arguments.of(
            false,
            time("00:00:00Z"),
            dateTime("2025-01-13T00:00:00Z")),
        // ==================
        // decimal-based keys
        // ==================
        Arguments.of(
            true,
            decimal("0.0"),
            decimal("0.00")),
        Arguments.of(
            true,
            decimal("10.0"),
            decimal("10.00")),
        Arguments.of(
            true,
            decimal("-1.0"),
            decimal("-1.00")),
        // string-based keys
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
            uri("https://example.com/other-resource")),
        Arguments.of(
            true,
            string("8eb2fb0f-47ac-40be-adb0-1a70fe7fad76"),
            uuid("8eb2fb0f-47ac-40be-adb0-1a70fe7fad76")),
        Arguments.of(
            false,
            uuid("0889bbb3-e3d3-41d3-bc98-07c18534462a"),
            uuid("8eb2fb0f-47ac-40be-adb0-1a70fe7fad76")));
  }

  @ParameterizedTest
  @MethodSource("keyValues")
  void testStringKeys(boolean expectedEquality, @NonNull IAnyAtomicItem item1, @NonNull IAnyAtomicItem item2) {
    IMapKey key1 = item1.asMapKey();
    IMapKey key2 = item2.asMapKey();

    if (expectedEquality) {
      assertAll(
          () -> assertEquals(key1, key2, "keys are equivalent"),
          () -> assertEquals(key2, key1, "keys are reflexively equivalent"),
          () -> assertEquals(key1.hashCode(), key2.hashCode(), "keys have the same hash code"));
    } else {
      assertAll(
          () -> assertNotEquals(key1, key2, "keys are different"),
          () -> assertNotEquals(key2, key1, "keys are reflexively different"),
          () -> assertNotEquals(key1.hashCode(), key2.hashCode(), "keys have different hash codes"));
    }
  }
}
