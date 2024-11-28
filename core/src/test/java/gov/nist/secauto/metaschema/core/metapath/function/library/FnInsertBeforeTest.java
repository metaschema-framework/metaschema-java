/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnInsertBeforeTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            ISequence.of(string("z"), string("a"), string("b"), string("c")),
            "insert-before(('a', 'b', 'c'), 0, 'z')"),
        Arguments.of(
            ISequence.of(string("z"), string("a"), string("b"), string("c")),
            "insert-before(('a', 'b', 'c'), 1, 'z')"),
        Arguments.of(
            ISequence.of(string("a"), string("z"), string("b"), string("c")),
            "insert-before(('a', 'b', 'c'), 2, 'z')"),
        Arguments.of(
            ISequence.of(string("a"), string("b"), string("z"), string("c")),
            "insert-before(('a', 'b', 'c'), 3, 'z')"),
        Arguments.of(
            ISequence.of(string("a"), string("b"), string("c"), string("z")),
            "insert-before(('a', 'b', 'c'), 4, 'z')"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(expected, IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext()));
  }
}
