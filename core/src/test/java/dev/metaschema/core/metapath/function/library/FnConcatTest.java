/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.ISequence;
import edu.umd.cs.findbugs.annotations.NonNull;

class FnConcatTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            ISequence.of(string("ungrateful")),
            "concat('un','grateful')"),
        Arguments.of(
            ISequence.of(string("Thy old groans ring yet in my ancient ears.")),
            "concat('Thy ', (), 'old ', \"groans\", \"\", ' ring', ' yet', ' in', ' my', ' ancient',' ears.')"),
        Arguments.of(
            ISequence.of(string("Ciao!")),
            "concat('Ciao!',())"),
        Arguments.of(
            ISequence.of(string("Ingratitude, thou marble-hearted fiend!")),
            "concat('Ingratitude, ', 'thou ', 'marble-hearted', ' fiend!')"),
        Arguments.of(
            ISequence.of(string("1234true")),
            "concat(01, 02, 03, 04, true())"),
        Arguments.of(
            ISequence.of(string("10/6")),
            "10 || '/' || 6"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(
        expected,
        IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext()));
  }

}
