/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.math;

import static dev.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.MetapathException;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for the IntegerDivision (idiv) operation.
 */
public class IntegerDivisionTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        // Basic integer division
        Arguments.of(integer(2), "5 idiv 2"),
        Arguments.of(integer(2), "4 idiv 2"),
        Arguments.of(integer(1), "5 idiv 3"),
        Arguments.of(integer(0), "0 idiv 5"),
        Arguments.of(integer(0), "1 idiv 5"),
        // Negative numbers
        Arguments.of(integer(-2), "-5 idiv 2"),
        Arguments.of(integer(-2), "5 idiv -2"),
        Arguments.of(integer(2), "-5 idiv -2"),
        // Decimal operands (truncated to integer result)
        Arguments.of(integer(2), "5.5 idiv 2.5"),
        Arguments.of(integer(2), "5.9 idiv 2.1"),
        // Mixed integer and decimal
        Arguments.of(integer(2), "5.5 idiv 2"),
        Arguments.of(integer(2), "5 idiv 2.25"));
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
        // Invalid type for idiv - strings cannot be cast to numeric
        Arguments.of("'abc' idiv 2"),
        // Division by zero
        Arguments.of("5 idiv 0"));
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
