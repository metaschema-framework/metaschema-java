/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for the Modulo (mod) operation.
 */
public class ModuloTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        // Integer modulo
        Arguments.of(integer(1), "5 mod 2"),
        Arguments.of(integer(0), "4 mod 2"),
        Arguments.of(integer(2), "5 mod 3"),
        Arguments.of(integer(0), "0 mod 5"),
        // Negative numbers
        Arguments.of(integer(-1), "-5 mod 2"),
        Arguments.of(integer(1), "5 mod -2"),
        Arguments.of(integer(-1), "-5 mod -2"),
        // Decimal modulo
        Arguments.of(decimal("0.5"), "5.5 mod 2.5"),
        Arguments.of(decimal("0.0"), "5.0 mod 2.5"),
        // Mixed integer and decimal
        Arguments.of(decimal("1.5"), "5.5 mod 2"),
        Arguments.of(decimal("0.5"), "5 mod 2.25"));
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
        // Invalid type for modulo - strings cannot be cast to numeric
        Arguments.of("'abc' mod 2"));
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
