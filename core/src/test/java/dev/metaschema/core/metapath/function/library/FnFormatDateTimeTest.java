/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for the fn:format-dateTime function.
 */
class FnFormatDateTimeTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        // 2-arg form (basic formatting)
        Arguments.of(
            string("2002-12-31"),
            "format-dateTime(meta:date-time('2002-12-31T15:58:45+02:00'), '[Y0001]-[M01]-[D01]')"),
        Arguments.of(
            string("12/31/2002 at 15:58:45"),
            "format-dateTime(meta:date-time('2002-12-31T15:58:45+02:00'), '[M01]/[D01]/[Y0001] at [H01]:[m01]:[s01]')"),
        Arguments.of(
            string("15:58"),
            "format-dateTime(meta:date-time('2002-12-31T15:58:45+02:00'), '[H01]:[m01]')"),
        // 5-arg form with language
        Arguments.of(
            string("31st December, 2002"),
            "format-dateTime(meta:date-time('2002-12-31T15:58:45+02:00'), '[D1o] [MNn], [Y]', 'en', (), ())"),
        Arguments.of(
            string("December 31, 2002"),
            "format-dateTime(meta:date-time('2002-12-31T15:58:45+02:00'), '[MNn] [D], [Y]', 'en', (), ())"),
        // Escaped brackets
        Arguments.of(
            string("[2002-12-31]"),
            "format-dateTime(meta:date-time('2002-12-31T15:58:45+02:00'), '[[[Y0001]-[M01]-[D01]]]')"),
        // Time components in dateTime
        Arguments.of(
            string("3:58 PM"),
            "format-dateTime(meta:date-time('2002-12-31T15:58:45+02:00'), '[h]:[m01] [PN]', 'en', (), ())"),
        // Timezone
        Arguments.of(
            string("+02:00"),
            "format-dateTime(meta:date-time('2002-12-31T15:58:45+02:00'), '[Z]')"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IStringItem expected, @NonNull String metapath) {
    assertEquals(
        expected,
        IMetapathExpression.compile(metapath)
            .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext()));
  }

  @Test
  void testEmptySequenceReturnsEmptySequence() {
    // Spec: "If $value is the empty sequence, the function returns the empty
    // sequence."
    assertNull(
        IMetapathExpression.compile("format-dateTime((), '[Y0001]')")
            .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext()));
  }
}
