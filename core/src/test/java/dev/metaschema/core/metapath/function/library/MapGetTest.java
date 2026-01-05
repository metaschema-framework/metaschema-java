/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.sequence;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.function.LookupTest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class MapGetTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(string("Donnerstag")),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:get($week, 4)"),
        Arguments.of(
            sequence(),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:get($week, 9)"),
        Arguments.of(sequence(), "map:get(map:entry(7,()), 7)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(
        expected,
        IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext()));
  }
}
