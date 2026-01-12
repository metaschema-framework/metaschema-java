/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst;

import static dev.metaschema.core.metapath.TestUtils.bool;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.StaticContext;
import dev.metaschema.core.metapath.StaticMetapathException;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.qname.IEnhancedQName;
import edu.umd.cs.findbugs.annotations.NonNull;

class ArrowExpressionTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(ISequence.of(string("ABC")), "'abc' => upper-case()"),
        Arguments.of(ISequence.of(string("123")), "'1' => concat('2') => concat('3')"),
        Arguments.of(ISequence.of(bool(true)), "() => $ex:var1()"));
  }

  /**
   * Tests the casting functionality using various input strings and target types.
   * <p>
   * The dynamic context is created fresh for each test case to ensure isolation.
   *
   * @param text
   *          The input string to cast
   * @param type
   *          The target type to cast to
   * @param expected
   *          The expected result after casting
   */
  @ParameterizedTest
  @MethodSource("provideValues")
  void testArrowExpression(@NonNull ISequence<?> expected, @NonNull String metapath) {
    StaticContext staticContext = StaticContext.builder()
        .namespace("ex", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    dynamicContext.bindVariableValue(IEnhancedQName.of(NS, "var1"), ISequence.of(string("fn:empty")));

    assertEquals(
        expected,
        IMetapathExpression.compile(metapath, staticContext).evaluate(null, dynamicContext));
  }

  @Test
  void testArrowExpressionWithUndefinedVariable() {
    StaticContext staticContext = StaticContext.builder()
        .namespace("ex", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);

    StaticMetapathException thrown = assertThrows(StaticMetapathException.class,
        () -> IMetapathExpression.compile("() => $ex:undefined()", staticContext)
            .evaluate(null, dynamicContext));
    assertThat(thrown)
        .isExactlyInstanceOf(StaticMetapathException.class)
        .extracting(StaticMetapathException::getErrorCode)
        .extracting(code -> code.getCode())
        .isEqualTo(StaticMetapathException.NOT_DEFINED);
  }
}
