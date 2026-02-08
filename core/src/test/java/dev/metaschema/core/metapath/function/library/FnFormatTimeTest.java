/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.MetapathException;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for the fn:format-time function.
 */
class FnFormatTimeTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        // W3C spec examples (section 9.8.5)
        Arguments.of(
            string("3:58 PM"),
            "format-time(meta:time('15:58:45.762+02:00'), '[h]:[m01] [PN]', 'en', (), ())"),
        Arguments.of(
            string("3:58:45 pm"),
            "format-time(meta:time('15:58:45.762+02:00'), '[h]:[m01]:[s01] [Pn]', 'en', (), ())"),
        Arguments.of(
            string("15:58"),
            "format-time(meta:time('15:58:45.762+02:00'), '[H01]:[m01]')"),
        Arguments.of(
            string("15:58:45.762"),
            "format-time(meta:time('15:58:45.762+02:00'), '[H01]:[m01]:[s01].[f001]')"),
        // 12-hour with midnight
        Arguments.of(
            string("12:00 AM"),
            "format-time(meta:time('00:00:00+00:00'), '[h]:[m01] [PN]', 'en', (), ())"),
        // 12-hour with noon
        Arguments.of(
            string("12:00 PM"),
            "format-time(meta:time('12:00:00+00:00'), '[h]:[m01] [PN]', 'en', (), ())"),
        // Timezone formatting
        Arguments.of(
            string("+02:00"),
            "format-time(meta:time('15:58:45+02:00'), '[Z]')"));
  }

  @Test
  void testEmptySequenceReturnsEmptySequence() {
    // Spec: "If $value is the empty sequence, the function returns the empty
    // sequence."
    assertNull(
        IMetapathExpression.compile("format-time((), '[H01]')")
            .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext()));
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
  void testDateMarkerRejectsForTime() {
    // format-time should reject date-only markers (Y, M, D, d, F, W, w)
    assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("format-time(meta:time('15:58:45+02:00'), '[Y0001]')")
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    });
  }

  @Test
  void testEraMarkerRejectsForTime() {
    // Era (E) is "a baseline for the numbering of years" per W3C spec,
    // so it is not available for xs:time values (FOFD1350)
    assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("format-time(meta:time('15:58:45+02:00'), '[En]')")
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    });
  }
}
