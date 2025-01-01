/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.date;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dateTime;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.time;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.yearMonthDuration;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class AdditionTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(
            date("2001-12-30"),
            "meta:date('2000-10-30') + meta:year-month-duration('P1Y2M')"),
        Arguments.of(
            date("2004-11-01Z"),
            "meta:date('2004-10-30Z') + meta:day-time-duration('P2DT2H30M0S')"),
        Arguments.of(
            dateTime("2001-12-30T11:12:00"),
            "meta:date-time('2000-10-30T11:12:00') + meta:year-month-duration('P1Y2M')"),
        Arguments.of(
            dateTime("2000-11-02T12:27:00"),
            "meta:date-time('2000-10-30T11:12:00') + meta:day-time-duration('P3DT1H15M')"),
        Arguments.of(
            time("12:27:00"),
            "meta:time('11:12:00') + meta:day-time-duration('P3DT1H15M')"),
        Arguments.of(
            time("02:27:00+03:00"),
            "meta:time('23:12:00+03:00') + meta:day-time-duration('P1DT3H15M')"),
        Arguments.of(
            date("2001-12-30"),
            "meta:year-month-duration('P1Y2M') + meta:date('2000-10-30')"),
        Arguments.of(
            dateTime("2001-12-30T11:12:00"),
            "meta:year-month-duration('P1Y2M') + meta:date-time('2000-10-30T11:12:00')"),
        Arguments.of(
            yearMonthDuration("P6Y2M"),
            "meta:year-month-duration('P2Y11M') + meta:year-month-duration('P3Y3M')"),
        Arguments.of(
            date("2004-11-01Z"),
            "meta:day-time-duration('P2DT2H30M0S') + meta:date('2004-10-30Z')"),
        Arguments.of(
            dateTime("2000-11-02T12:27:00"),
            "meta:day-time-duration('P3DT1H15M') + meta:date-time('2000-10-30T11:12:00')"),
        Arguments.of(
            time("12:27:00"),
            "meta:day-time-duration('P3DT1H15M') + meta:time('11:12:00')"),
        Arguments.of(
            time("02:27:00+03:00"),
            "meta:day-time-duration('P1DT3H15M') + meta:time('23:12:00+03:00')"),
        Arguments.of(
            dayTimeDuration("P8DT5M"),
            "meta:day-time-duration('P2DT12H5M') + meta:day-time-duration('P5DT12H')"),
        Arguments.of(
            integer(2),
            "1 + 1"),
        Arguments.of(
            decimal(2),
            "1.0 + 1"),
        Arguments.of(
            decimal(2),
            "1 + 1.0"),
        Arguments.of(
            decimal(2),
            "1.0 + 1.0"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IAnyAtomicItem expected, @NonNull String metapath) {
    IAnyAtomicItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
