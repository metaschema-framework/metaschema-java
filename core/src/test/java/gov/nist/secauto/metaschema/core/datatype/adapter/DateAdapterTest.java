/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Arguments.of("2018-01-01", true, ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of("2020-06-23Z", false, ZonedDateTime.of(2020, 6, 23, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of("2020-06-23-04:00", false, ZonedDateTime.of(2020, 6, 23, 0, 0, 0, 0, ZoneOffset.of("-04:00"))),
        Arguments.of("2020-01-01", true, ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of("2018-01-01", true, ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testSimpleDate(@NonNull String actual, boolean ambiguous, @NonNull ZonedDateTime expected) {
    AmbiguousDate date = ADAPTER.parse(actual);
    assertAll(
        () -> assertEquals(ambiguous, !date.hasTimeZone()),
        () -> assertEquals(expected, date.getValue()));
  }
}
