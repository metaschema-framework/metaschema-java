/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class QuantifiedTest
    extends ExpressionTestBase {
  private static Stream<Arguments> testQuantified() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            true,
            MetapathExpression.compile("some $x in (1, 2, 3), $y in (2, 3, 4) satisfies $x + $y = 4")),
        Arguments.of(
            false,
            MetapathExpression.compile("every $x in (1, 2, 3), $y in (2, 3, 4) satisfies $x + $y = 4")));
  }

  @ParameterizedTest
  @MethodSource
  void testQuantified(boolean expected, @NonNull MetapathExpression metapath) {
    DynamicContext dynamicContext = newDynamicContext();

    assertEquals(expected, metapath.evaluateAs(null, MetapathExpression.ResultType.BOOLEAN, dynamicContext));
  }
}
