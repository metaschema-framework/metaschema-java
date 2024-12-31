/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.date;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dateTime;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
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

public class SubtractionTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(
            dayTimeDuration("P337D"),
            "meta:date('2000-10-30Z') - meta:date('1999-11-28Z')"),
        Arguments.of(
            dayTimeDuration("P336DT19H"),
            "meta:date('2000-10-30+05:00') - meta:date('1999-11-28Z')"),
        Arguments.of(
            date("1999-08-30"),
            "meta:date('2000-10-30') - meta:year-month-duration('P1Y2M')"),
        Arguments.of(
            date("1999-02-28Z"),
            "meta:date('2000-02-29Z') - meta:year-month-duration('P1Y')"),
        Arguments.of(
            date("1999-09-30-05:00"),
            "meta:date('2000-10-31-05:00') - meta:year-month-duration('P1Y1M')"),
        Arguments.of(
            date("2000-10-26"),
            "meta:date('2000-10-30') - meta:day-time-duration('P3DT1H15M')"),
        Arguments.of(
            dayTimeDuration("P337DT2H12M"),
            "meta:date-time('2000-10-30T06:12:00-05:00') - meta:date-time('1999-11-28T09:00:00Z')"),
        Arguments.of(
            dateTime("1999-08-30T11:12:00"),
            "meta:date-time('2000-10-30T11:12:00') - meta:year-month-duration('P1Y2M')"),
        Arguments.of(
            dateTime("2000-10-27T09:57:00"),
            "meta:date-time('2000-10-30T11:12:00') - meta:day-time-duration('P3DT1H15M')"),
        Arguments.of(
            dayTimeDuration("PT2H12M"),
            "meta:time('11:12:00Z') - meta:time('04:00:00-05:00')"),
        Arguments.of(
            dayTimeDuration("PT0S"),
            "meta:time('11:00:00-05:00') - meta:time('21:30:00+05:30')"),
        Arguments.of(
            dayTimeDuration("P1D"),
            "meta:time('17:00:00-06:00') - meta:time('08:00:00+09:00')"),
        Arguments.of(
            dayTimeDuration("-PT23H59M59S"),
            "meta:time('24:00:00-05:00') - meta:time('23:59:59-05:00')"),
        Arguments.of(
            time("09:57:00"),
            "meta:time('11:12:00') - meta:day-time-duration('P3DT1H15M')"),
        Arguments.of(
            time("22:10:00-05:00"),
            "meta:time('08:20:00-05:00') - meta:day-time-duration('P23DT10H10M')"),
        Arguments.of(
            yearMonthDuration("-P4M"),
            "meta:year-month-duration('P2Y11M') - meta:year-month-duration('P3Y3M')"),
        Arguments.of(
            dayTimeDuration("P1DT1H30M"),
            "meta:day-time-duration('P2DT12H') - meta:day-time-duration('P1DT10H30M')"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IAnyAtomicItem expected, @NonNull String metapath) {
    IAnyAtomicItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
