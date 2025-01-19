/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.time;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.ITimeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnAdjustTimeToTimezoneTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            time("10:00:00-05:00"),
            true,
            "fn:adjust-time-to-timezone(meta:time('10:00:00'))"),
        Arguments.of(
            time("12:00:00-05:00"),
            true,
            "fn:adjust-time-to-timezone(meta:time('10:00:00-07:00'))"),
        Arguments.of(
            time("10:00:00-10:00"),
            true,
            "fn:adjust-time-to-timezone(meta:time('10:00:00'), $tz-10)"),
        Arguments.of(
            time("07:00:00-10:00"),
            true,
            "fn:adjust-time-to-timezone(meta:time('10:00:00-07:00'), $tz-10)"),
        Arguments.of(
            time("10:00:00"),
            false,
            "fn:adjust-time-to-timezone(meta:time('10:00:00'), ())"),
        Arguments.of(
            time("10:00:00"),
            false,
            "fn:adjust-time-to-timezone(meta:time('10:00:00-07:00'), ())"),
        Arguments.of(
            null,
            false,
            "fn:adjust-time-to-timezone((), ())"),
        Arguments.of(
            null,
            false,
            "fn:adjust-time-to-timezone((), $tz-10)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(
      @Nullable ITimeItem expected,
      boolean hasExpectedTimezone,
      @NonNull String metapath) {
    DynamicContext dynamicContext = newDynamicContext();
    dynamicContext.setImplicitTimeZone(dayTimeDuration("-PT5H"));
    dynamicContext.bindVariableValue(IEnhancedQName.of("tz-10"), ISequence.of(dayTimeDuration("-PT10H")));

    ITimeItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(
            null,
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    assertAll(
        () -> assertEquals(expected, result),
        () -> assertEquals(hasExpectedTimezone, result == null ? false : result.hasTimezone()));
  }
}
