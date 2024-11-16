/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.datatype.adapter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.datatype.object.AmbiguousDateTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class DateTimeAdapterTest {
  private static final DateTimeAdapter ADAPTER = new DateTimeAdapter();

  /**
   * Provides test cases for date-time parsing.
   * <p>
   * Each argument contains:
   * <ul>
   * <li>input string to parse</li>
   * <li>boolean indicating if the datetime is ambiguous (no timezone)</li>
   * <li>expected ZonedDateTime result</li>
   * </ul>
   *
   * @return Stream of test cases
   */
  private static Stream<Arguments> provideValues() {
    return Stream.of(
        // Cases without timezone (ambiguous)
        Arguments.of(
            "2018-01-01T00:00:00",
            true,
            ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)),
        Arguments.of(
            "2019-09-28T23:20:50.5200",
            true,
            ZonedDateTime.of(2019, 9, 28, 23, 20, 50, toNanos(0.5200), ZoneOffset.UTC)),
        Arguments.of(
            "2019-12-02T16:39:57",
            true,
            ZonedDateTime.of(2019, 12, 2, 16, 39, 57, 0, ZoneOffset.UTC)),
        Arguments.of(
            "2019-12-31T23:59:59",
            true,
            ZonedDateTime.of(2019, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)),
        // Cases with explicit timezone
        Arguments.of(
            "2019-09-28T23:20:50.52Z",
            false,
            ZonedDateTime.of(2019, 9, 28, 23, 20, 50, toNanos(0.52), ZoneOffset.UTC)),
        Arguments.of(
            "2019-09-28T23:20:50.0Z",
            false,
            ZonedDateTime.of(2019, 9, 28, 23, 20, 50, 0, ZoneOffset.UTC)),
        Arguments.of(
            "2019-12-02T16:39:57-08:00",
            false,
            ZonedDateTime.of(2019, 12, 2, 16, 39, 57, 0, ZoneOffset.of("-08:00"))),
        Arguments.of(
            "2019-12-02T16:39:57.100-08:00",
            false,
            ZonedDateTime.of(2019, 12, 2, 16, 39, 57, toNanos(0.100), ZoneOffset.of("-08:00"))),
        Arguments.of(
            "2019-12-31T23:59:59Z",
            false,
            ZonedDateTime.of(2019, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)));
  }

  /**
   * Converts a fractional second to nanoseconds.
   *
   * @param fraction
   *          the fractional part of a second (0.0 to 0.999...)
   * @return the equivalent nanoseconds
   * @throws IllegalArgumentException
   *           if fraction is negative or >= 1
   */
  private static int toNanos(double fraction) {
    if (fraction < 0.0 || fraction >= 1.0) {
      throw new IllegalArgumentException(String.format("Fraction '%.3f' must be between 0.0 and 0.999...", fraction));
    }
    return (int) Math.round(TimeUnit.SECONDS.toNanos(1) * fraction);
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testSimpleDateTime(@NonNull String actual, boolean ambiguous, @NonNull ZonedDateTime expected) {
    AmbiguousDateTime date = ADAPTER.parse(actual);
    assertAll(
        () -> assertEquals(ambiguous, !date.hasTimeZone()),
        () -> assertEquals(expected, date.getValue()));
  }
}
