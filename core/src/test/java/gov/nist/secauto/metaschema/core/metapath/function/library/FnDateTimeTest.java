/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dateTime;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnDateTimeTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            dateTime("1999-12-31T12:00:00"),
            false,
            "fn:dateTime(meta:date('1999-12-31'), meta:time('12:00:00'))"),
        Arguments.of(
            dateTime("1999-12-31T00:00:00"),
            false,
            "fn:dateTime(meta:date('1999-12-31'), meta:time('24:00:00'))"),
        Arguments.of(
            dateTime("1999-12-31T12:00:00-02:00"),
            true,
            "fn:dateTime(meta:date('1999-12-31-02:00'), meta:time('12:00:00-02:00'))"),
        Arguments.of(
            dateTime("1999-12-31T12:00:00-02:00"),
            true,
            "fn:dateTime(meta:date('1999-12-31-02:00'), meta:time('12:00:00'))"),
        Arguments.of(
            dateTime("1999-12-31T12:00:00-02:00"),
            true,
            "fn:dateTime(meta:date('1999-12-31'), meta:time('12:00:00-02:00'))"),
        Arguments.of(
            null,
            false,
            "fn:dateTime((), meta:time('12:00:00'))"),
        Arguments.of(
            null,
            false,
            "fn:dateTime(meta:date('1999-12-31'), ())"),
        Arguments.of(
            null,
            false,
            "fn:dateTime((), ())"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable IDateTimeItem expected, boolean hasExpectedTimezone, @NonNull String metapath) {
    IDateTimeItem result = IMetapathExpression.compile(metapath).evaluateAs(null, IMetapathExpression.ResultType.ITEM,
        newDynamicContext());
    assertAll(
        () -> assertEquals(expected, result),
        () -> assertEquals(hasExpectedTimezone, result == null ? false : result.hasTimezone()));
  }
}
