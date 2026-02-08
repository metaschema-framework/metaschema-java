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
 * Tests for the fn:format-date function.
 */
class FnFormatDateTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        // W3C spec examples (section 9.8.5)
        Arguments.of(
            string("2002-12-31"),
            "format-date(meta:date('2002-12-31'), '[Y0001]-[M01]-[D01]')"),
        Arguments.of(
            string("31 XII 2002"),
            "format-date(meta:date('2002-12-31'), '[D1] [MI] [Y]')"),
        Arguments.of(
            string("31st December, 2002"),
            "format-date(meta:date('2002-12-31'), '[D1o] [MNn], [Y]', 'en', (), ())"),
        Arguments.of(
            string("31 DEC 2002"),
            "format-date(meta:date('2002-12-31'), '[D01] [MN,*-3] [Y0001]', 'en', (), ())"),
        Arguments.of(
            string("December 31, 2002"),
            "format-date(meta:date('2002-12-31'), '[MNn] [D], [Y]', 'en', (), ())"),
        // Escaped brackets
        Arguments.of(
            string("[2002-12-31]"),
            "format-date(meta:date('2002-12-31'), '[[[Y0001]-[M01]-[D01]]]')"),
        // Day of week
        Arguments.of(
            string("Tuesday"),
            "format-date(meta:date('2002-12-31'), '[FNn]', 'en', (), ())"),
        // Month name
        Arguments.of(
            string("March"),
            "format-date(meta:date('2002-03-15'), '[MNn]')"),
        // Roman numerals for month
        Arguments.of(
            string("III"),
            "format-date(meta:date('2002-03-15'), '[MI]')"),
        // 2-digit year (modulo rule)
        Arguments.of(
            string("02"),
            "format-date(meta:date('2002-12-31'), '[Y01]')"));
  }

  @Test
  void testEmptySequenceReturnsEmptySequence() {
    // Spec: "If $value is the empty sequence, the function returns the empty
    // sequence."
    assertNull(
        IMetapathExpression.compile("format-date((), '[Y0001]')")
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
  void testTimeMarkerRejectsForDate() {
    // format-date should reject time-only markers (H, h, P, m, s, f)
    assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("format-date(meta:date('2002-12-31'), '[H01]')")
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    });
  }
}
