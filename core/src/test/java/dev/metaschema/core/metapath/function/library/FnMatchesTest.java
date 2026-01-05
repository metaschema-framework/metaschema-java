/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.bool;
import static dev.metaschema.core.metapath.TestUtils.sequence;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.function.regex.RegularExpressionMetapathException;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IBooleanItem;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnMatchesTest
    extends ExpressionTestBase {
  private static final String POEM = "Kaum hat dies der Hahn gesehen,\n"
      + "Fängt er auch schon an zu krähen:\n"
      + "Kikeriki! Kikikerikih!!\n"
      + "Tak, tak, tak! - da kommen sie.";

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            bool(true),
            "matches(\"abracadabra\", \"bra\")"),
        Arguments.of(
            bool(true),
            "matches(\"abracadabra\", \"^a.*a$\")"),
        Arguments.of(
            bool(false),
            "matches(\"abracadabra\", \"^bra\")"),
        Arguments.of(
            bool(false),
            "matches($poem, \"Kaum.*krähen\")"),
        Arguments.of(
            bool(true),
            "matches($poem, \"Kaum.*krähen\", \"s\")"),
        Arguments.of(
            bool(true),
            "matches($poem, \"^Kaum.*gesehen,$\", \"m\")"),
        Arguments.of(
            bool(false),
            "matches($poem, \"^Kaum.*gesehen,$\")"),
        Arguments.of(
            bool(true),
            "matches($poem, \"kiki\", \"i\")"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IBooleanItem expected, @NonNull String metapath) {
    DynamicContext dynamicContext = newDynamicContext();
    dynamicContext.bindVariableValue(IEnhancedQName.of("poem"), ISequence.of(IStringItem.valueOf(POEM)));
    assertEquals(expected, IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, dynamicContext));
  }

  @Test
  void testInvalidPattern() {
    RegularExpressionMetapathException thrown = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnMatches.SIGNATURE_TWO_ARG,
              newDynamicContext(),
              ISequence.empty(),
              ObjectUtils.notNull(List.of(sequence(string("input")), sequence(string("pattern[")))));
        });
    assertThat(thrown)
        .isExactlyInstanceOf(RegularExpressionMetapathException.class)
        .hasCauseExactlyInstanceOf(PatternSyntaxException.class)
        .returns(
            RegularExpressionMetapathException.INVALID_EXPRESSION,
            from(ex -> ex.getErrorCode().getCode()));

  }

  @Test
  void testInvalidFlag() {
    RegularExpressionMetapathException thrown = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnMatches.SIGNATURE_THREE_ARG,
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
        .returns(
            RegularExpressionMetapathException.INVALID_FLAG,
            from(ex -> ex.getErrorCode().getCode()));
  }
}
