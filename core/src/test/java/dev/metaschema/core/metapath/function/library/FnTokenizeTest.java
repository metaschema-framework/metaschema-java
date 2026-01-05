/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.sequence;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.function.regex.RegularExpressionMetapathException;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnTokenizeTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(string("red"), string("green"), string("blue")),
            "tokenize(\" red green blue \")"),
        Arguments.of(
            sequence(string("The"), string("cat"), string("sat"), string("on"), string("the"), string("mat")),
            "tokenize(\"The cat sat on the mat\", \"\\s+\")"),
        Arguments.of(
            sequence(string(""), string("red"), string("green"), string("blue"), string("")),
            "tokenize(\" red green blue \", \"\\s+\")"),
        Arguments.of(
            sequence(string("1"), string("15"), string("24"), string("50")),
            "tokenize(\"1, 15, 24, 50\", \",\\s*\")"),
        Arguments.of(
            sequence(string("1"), string("15"), string(""), string("24"), string("50"), string("")),
            "tokenize(\"1,15,,24,50,\", \",\")"),
        Arguments.of(
            sequence(string("Some unparsed"), string("HTML"), string("text")),
            "tokenize(\"Some unparsed <br> HTML <BR> text\", \"\\s*<br>\\s*\", \"i\")"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(expected, IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext()));
  }

  // TODO: make sure this (and others) exception chain is flattened
  @Test
  void testMatchZeroLengthString() {
    RegularExpressionMetapathException thrown = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnTokenize.SIGNATURE_TWO_ARG,
              newDynamicContext(),
              ISequence.empty(),
              ObjectUtils.notNull(List.of(sequence(string("abba")), sequence(string(".?")))));
        });
    assertThat(thrown)
        .isExactlyInstanceOf(RegularExpressionMetapathException.class)
        .hasNoCause()
        .extracting(ex -> ex.getErrorCode().getCode())
        .isEqualTo(RegularExpressionMetapathException.MATCHES_ZERO_LENGTH_STRING);
  }

  @Test
  void testInvalidPattern() {
    RegularExpressionMetapathException thrown = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnTokenize.SIGNATURE_TWO_ARG,
              newDynamicContext(),
              ISequence.empty(),
              ObjectUtils.notNull(List.of(sequence(string("input")), sequence(string("pattern[")))));
        });
    assertThat(thrown)
        .isExactlyInstanceOf(RegularExpressionMetapathException.class)
        .hasCauseExactlyInstanceOf(PatternSyntaxException.class)
        .extracting(ex -> ex.getErrorCode().getCode())
        .isEqualTo(RegularExpressionMetapathException.INVALID_EXPRESSION);
  }

  @Test
  void testInvalidFlag() {
    RegularExpressionMetapathException thrown = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnTokenize.SIGNATURE_THREE_ARG,
              newDynamicContext(),
              ISequence.empty(),
              ObjectUtils.notNull(List.of(
                  sequence(string("input")),
                  sequence(string("pattern")),
                  sequence(string("dsm")))));
        });
    assertThat(thrown)
        .isExactlyInstanceOf(RegularExpressionMetapathException.class)
        .hasNoCause()
        .extracting(ex -> ex.getErrorCode().getCode())
        .isEqualTo(RegularExpressionMetapathException.INVALID_FLAG);
  }
}
