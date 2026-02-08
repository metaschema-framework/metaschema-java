/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

class FnFormatIntegerTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        // Decimal digit patterns
        Arguments.of(
            string("123"),
            "format-integer(123, '1')"),
        Arguments.of(
            string("0123"),
            "format-integer(123, '0000')"),
        Arguments.of(
            string("123"),
            "format-integer(123, '01')"),
        Arguments.of(
            string("0"),
            "format-integer(0, '1')"),
        Arguments.of(
            string("-123"),
            "format-integer(-123, '1')"),
        // Alphabetic sequences
        Arguments.of(
            string("a"),
            "format-integer(1, 'a')"),
        Arguments.of(
            string("z"),
            "format-integer(26, 'a')"),
        Arguments.of(
            string("aa"),
            "format-integer(27, 'a')"),
        Arguments.of(
            string("A"),
            "format-integer(1, 'A')"),
        Arguments.of(
            string("g"),
            "format-integer(7, 'a')"),
        // Roman numerals
        Arguments.of(
            string("i"),
            "format-integer(1, 'i')"),
        Arguments.of(
            string("iv"),
            "format-integer(4, 'i')"),
        Arguments.of(
            string("LVII"),
            "format-integer(57, 'I')"),
        Arguments.of(
            string("MCMXCIX"),
            "format-integer(1999, 'I')"),
        // Words
        Arguments.of(
            string("one hundred twenty-three"),
            "format-integer(123, 'w')"),
        Arguments.of(
            string("ONE"),
            "format-integer(1, 'W')"),
        Arguments.of(
            string("Twenty-One"),
            "format-integer(21, 'Ww')"),
        // Ordinal modifier (3-arg form)
        Arguments.of(
            string("1st"),
            "format-integer(1, '1;o', 'en')"),
        Arguments.of(
            string("2nd"),
            "format-integer(2, '1;o', 'en')"),
        Arguments.of(
            string("3rd"),
            "format-integer(3, '1;o', 'en')"),
        Arguments.of(
            string("4th"),
            "format-integer(4, '1;o', 'en')"),
        Arguments.of(
            string("11th"),
            "format-integer(11, '1;o', 'en')"),
        Arguments.of(
            string("12th"),
            "format-integer(12, '1;o', 'en')"),
        Arguments.of(
            string("13th"),
            "format-integer(13, '1;o', 'en')"),
        Arguments.of(
            string("21st"),
            "format-integer(21, '1;o', 'en')"),
        Arguments.of(
            string("111th"),
            "format-integer(111, '1;o', 'en')"),
        Arguments.of(
            string("112th"),
            "format-integer(112, '1;o', 'en')"),
        // Grouping separators
        Arguments.of(
            string("1,000,000"),
            "format-integer(1000000, '#,##0')"),
        Arguments.of(
            string("15"),
            "format-integer(15, '#,##0')"),
        Arguments.of(
            string("1;234"),
            "format-integer(1234, '#;##0;')"),
        // Empty sequence
        Arguments.of(
            string(""),
            "format-integer((), '1')"));
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
  void testOrdinalWithWordFormatFallsBackToCardinal() {
    // Spec: "If ordinal numbering is not supported for the combination of the
    // format token, the language, and the string appearing in parentheses, the
    // request is ignored and cardinal numbers are generated instead."
    // Our implementation does not support word ordinals (first, second, etc.),
    // so it must fall back to cardinal (one, two, etc.)
    assertEquals(
        string("one"),
        IMetapathExpression.compile("format-integer(1, 'w;o', 'en')")
            .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext()));
  }

  @Test
  void testInvalidFormatToken() {
    assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("format-integer(1, '')")
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    });
  }
}
