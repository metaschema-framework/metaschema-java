/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
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

public class MultiplicationTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(
            dayTimeDuration("PT4H33M"),
            "meta:day-time-duration('PT2H10M') * 2.1"),
        Arguments.of(
            dayTimeDuration("PT4H33M"),
            "2.1 * meta:day-time-duration('PT2H10M')"),
        Arguments.of(
            yearMonthDuration("P6Y9M"),
            "meta:year-month-duration('P2Y11M') * 2.3"),
        Arguments.of(
            yearMonthDuration("P6Y9M"),
            "2.3 * meta:year-month-duration('P2Y11M')"),
        Arguments.of(integer(0), "1 * 0"),
        Arguments.of(integer(1), "1 * 1"),
        Arguments.of(integer(2), "1 * 2"),
        Arguments.of(decimal("1.0"), "1.0 * 1"),
        Arguments.of(decimal("1.0"), "1 * 1.0"),
        Arguments.of(decimal("1.0"), "1.0 * 1.0"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IAnyAtomicItem expected, @NonNull String metapath) {
    IAnyAtomicItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
