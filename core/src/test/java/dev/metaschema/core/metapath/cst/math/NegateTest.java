/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.math;

import static dev.metaschema.core.metapath.TestUtils.decimal;
import static dev.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.MetapathException;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for the Negate (unary minus) operation.
 */
public class NegateTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        // Negate positive integers
        Arguments.of(integer(-5), "-5"),
        Arguments.of(integer(-1), "-1"),
        Arguments.of(integer(0), "-0"),
        // Negate negative integers (double negation)
        Arguments.of(integer(5), "--5"),
        Arguments.of(integer(5), "- -5"),
        // Negate positive decimals
        Arguments.of(decimal("-5.5"), "-5.5"),
        Arguments.of(decimal("-1.0"), "-1.0"),
        // Negate negative decimals
        Arguments.of(decimal("5.5"), "--5.5"),
        // Negate expressions
        Arguments.of(integer(-7), "-(3 + 4)"),
        Arguments.of(integer(-1), "-(5 - 4)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IAnyAtomicItem expected, @NonNull String metapath) {
    IAnyAtomicItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }

  private static Stream<Arguments> provideInvalidValues() {
    return Stream.of(
        // Invalid type for negation - strings cannot be cast to numeric
        Arguments.of("-'abc'"));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidValues")
  void testInvalidExpression(@NonNull String metapath) {
    IMetapathExpression expr = IMetapathExpression.compile(metapath);
    assertThrows(MetapathException.class, () -> {
      expr.evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    });
  }
}
