/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ArrowExpressionTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        // Arguments.of(ISequence.of(string("true")), "true() => string()"),
        Arguments.of(ISequence.of(bool(false)), "() => exists()"),
        Arguments.of(ISequence.of(integer(3)), "(1, 2) => sum()"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testArrowExpression(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(
        expected,
        MetapathExpression.compile(metapath).evaluateAs(null, MetapathExpression.ResultType.SEQUENCE,
            newDynamicContext()));
  }
}
