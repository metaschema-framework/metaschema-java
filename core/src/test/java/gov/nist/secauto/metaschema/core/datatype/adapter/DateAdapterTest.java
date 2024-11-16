/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class DateAdapterTest {
  private static final DateAdapter ADAPTER = new DateAdapter();

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        // Cases without timezone (ambiguous)
        Arguments.of("2018-01-01", true, ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of("2020-01-01", true, ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of("2018-01-01", true, ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of("2000-02-29", true, ZonedDateTime.of(2000, 2, 29, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of("2020-02-29", true, ZonedDateTime.of(2020, 2, 29, 0, 0, 0, 0, ZoneOffset.UTC)),
        // Cases with explicit timezone
        Arguments.of("2020-06-23Z", false, ZonedDateTime.of(2020, 6, 23, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of("2020-06-23-04:00", false, ZonedDateTime.of(2020, 6, 23, 0, 0, 0, 0, ZoneOffset.of("-04:00"))));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testSimpleDate(@NonNull String actual, boolean ambiguous, @NonNull ZonedDateTime expected) {
    AmbiguousDate date = ADAPTER.parse(actual);
    assertAll(
        () -> assertEquals(ambiguous, !date.hasTimeZone()),
        () -> assertEquals(expected, date.getValue()));
  }

  private static Stream<Arguments> provideInvalidValues() {
    return Stream.of(
        // Cases with invalid date values
        Arguments.of("2100-02-29"),
        Arguments.of("2023-02-30Z"),
        Arguments.of("2023-06-31-04:00"));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidValues")
  void testInvalidDates(@NonNull String actual) {
    assertThrows(IllegalArgumentException.class, () -> {
      ADAPTER.parse(actual);
    });
  }
}
