/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
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

public class DivisionTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(
            dayTimeDuration("PT17H40M7S"),
            "meta:day-time-duration('P1DT2H30M10.5S') div 1.5"),
        Arguments.of(
            decimal("1.437834967320261"),
            "meta:day-time-duration('P2DT53M11S') div meta:day-time-duration('P1DT10H')"),
        Arguments.of(
            decimal("175991.0"),
            "meta:day-time-duration('P2DT53M11S') div meta:day-time-duration('PT1S')"),
        Arguments.of(
            yearMonthDuration("P1Y11M"),
            "meta:year-month-duration('P2Y11M') div 1.5"),
        Arguments.of(
            decimal("-2.5"),
            "meta:year-month-duration('P3Y4M') div meta:year-month-duration('-P1Y4M')"),
        Arguments.of(
            decimal("40"),
            "meta:year-month-duration('P3Y4M') div meta:year-month-duration('P1M')"),
        Arguments.of(decimal("0"), "0 div 1"),
        Arguments.of(decimal("1"), "1 div 1"),
        Arguments.of(decimal("0.5"), "1 div 2"),
        Arguments.of(decimal("1.0"), "1.0 div 1"),
        Arguments.of(decimal("1.0"), "1 div 1.0"),
        Arguments.of(decimal("1.0"), "1.0 div 1.0"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IAnyAtomicItem expected, @NonNull String metapath) {
    IAnyAtomicItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
