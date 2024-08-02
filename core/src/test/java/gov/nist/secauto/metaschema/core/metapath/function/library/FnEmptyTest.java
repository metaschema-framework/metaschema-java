/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/*
 *
 * The expression fn:empty((1,2,3)[10]) returns true().
 * The expression fn:empty(fn:remove(("hello", "world"), 1)) returns false().
 * The expression fn:empty([]) returns false().
 * The expression fn:empty(map{}) returns false().
 * The expression fn:empty("") returns false().
 *
 * Source: https://www.w3.org/TR/xpath-functions-31/#func-empty
 *
 */

class FnEmptyTest
    extends FunctionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            ISequence.of(bool(false)),
            "empty((1,2,3))"),
        Arguments.of(
            ISequence.of(bool(false)),
            "empty(('hello','world'))"),
        Arguments.of(
            ISequence.of(bool(false)),
            "empty((''))"));
    // TODO: Add when we support map constructor syntax.
    // Arguments.of(
    // ISequence.of(bool(true)),
    // "empty(map{})"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(expected, MetapathExpression.compile(metapath).evaluateAs(null, MetapathExpression.ResultType.SEQUENCE,
        newDynamicContext()));
  }
}
