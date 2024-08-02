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

class FnRemoveTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            ISequence.of(string("a"), string("b"), string("c")),
            "remove(('a', 'b', 'c'), 0)"),
        Arguments.of(
            ISequence.of(string("b"), string("c")),
            "remove(('a', 'b', 'c'), 1)"),
        Arguments.of(
            ISequence.of(string("a"), string("c")),
            "remove(('a', 'b', 'c'), 2)"),
        Arguments.of(
            ISequence.of(string("a"), string("b")),
            "remove(('a', 'b', 'c'), 3)"),
        Arguments.of(
            ISequence.of(string("a"), string("b"), string("c")),
            "remove(('a', 'b', 'c'), 6)"),
        Arguments.of(
            ISequence.empty(),
            "remove((), 3)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(expected, MetapathExpression.compile(metapath).evaluateAs(null, MetapathExpression.ResultType.SEQUENCE,
        newDynamicContext()));
  }
}
