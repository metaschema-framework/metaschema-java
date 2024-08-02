/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnReverseTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            ISequence.of(string("c"), string("b"), string("a")),
            "reverse(('a', 'b', 'c'))"),
        Arguments.of(
            ISequence.of(string("hello")),
            "reverse(('hello'))"),
        Arguments.of(
            ISequence.empty(),
            "reverse(())"));
    // TODO: Add tests when Metapath array syntax supported.
    // Arguments.of(
    // ISequence.of(array([1, 2, 3])),
    // "reverse(([1,2,3]))"),
    // Arguments.of(
    // ISequence.of(array([1, 2, 3]), array([1, 2, 3])),
    // "reverse(([1,2,3],[4,5,6]))");
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(expected, MetapathExpression.compile(metapath).evaluateAs(null, MetapathExpression.ResultType.SEQUENCE,
        newDynamicContext()));
  }
}
