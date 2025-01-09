/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dateTime;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnAdjustDateTimeToTimezoneTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            dateTime("2002-03-07T10:00:00-05:00"),
            true,
            "fn:adjust-dateTime-to-timezone(meta:date-time('2002-03-07T10:00:00'))"),
        Arguments.of(
            dateTime("2002-03-07T12:00:00-05:00"),
            true,
            "fn:adjust-dateTime-to-timezone(meta:date-time('2002-03-07T10:00:00-07:00'))"),
        Arguments.of(
            dateTime("2002-03-07T10:00:00-10:00"),
            true,
            "fn:adjust-dateTime-to-timezone(meta:date-time('2002-03-07T10:00:00'), $tz-10)"),
        Arguments.of(
            dateTime("2002-03-07T07:00:00-10:00"),
            true,
            "fn:adjust-dateTime-to-timezone(meta:date-time('2002-03-07T10:00:00-07:00'), $tz-10)"),
        Arguments.of(
            dateTime("2002-03-08T03:00:00+10:00"),
            true,
            "fn:adjust-dateTime-to-timezone("
                + "meta:date-time('2002-03-07T10:00:00-07:00'),"
                + " meta:day-time-duration('PT10H'))"),
        Arguments.of(
            dateTime("2002-03-06T15:00:00-08:00"),
            true,
            "fn:adjust-dateTime-to-timezone("
                + "meta:date-time('2002-03-07T00:00:00+01:00'),"
                + " meta:day-time-duration('-PT8H'))"),
        Arguments.of(
            dateTime("2002-03-07T10:00:00"),
            false,
            "fn:adjust-dateTime-to-timezone(meta:date-time('2002-03-07T10:00:00'), ())"),
        Arguments.of(
            dateTime("2002-03-07T10:00:00"),
            false,
            "fn:adjust-dateTime-to-timezone(meta:date-time('2002-03-07T10:00:00-07:00'), ())"),
        Arguments.of(
            null,
            false,
            "fn:adjust-dateTime-to-timezone((), ())"),
        Arguments.of(
            null,
            false,
            "fn:adjust-dateTime-to-timezone((), $tz-10)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable IDateTimeItem expected, boolean hasExpectedTimezone, @NonNull String metapath) {
    DynamicContext dynamicContext = newDynamicContext();
    dynamicContext.setImplicitTimeZone(dayTimeDuration("-PT5H"));
    dynamicContext.bindVariableValue(IEnhancedQName.of("tz-10"), ISequence.of(dayTimeDuration("-PT10H")));

    IDateTimeItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(
            null,
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    assertAll(
        () -> assertEquals(expected, result),
        () -> assertEquals(hasExpectedTimezone, result == null ? false : result.hasTimezone()));
  }
}
