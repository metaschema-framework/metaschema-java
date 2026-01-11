/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.entry;
import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.map;
import static dev.metaschema.core.metapath.TestUtils.sequence;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import edu.umd.cs.findbugs.annotations.NonNull;

class MapForEachTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(integer(1), integer(2)),
            "map:for-each(map{1:'yes', 2:'no'}, function($k, $v){$k})"),
        Arguments.of(
            sequence(string("yes"), string("no")),
            "distinct-values(map:for-each(map{1:'yes', 2:'no'}, function($k, $v){$v}))"),
        Arguments.of(
            sequence(map(entry(string("a"), integer(2)), entry(string("b"), integer(3)))),
            "map:merge(map:for-each(map{'a':1, 'b':2}, function($k, $v){map:entry($k, $v+1)}))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull ISequence<IItem> expected, @NonNull String metapath) {

    ISequence<IItem> result = IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext());
    assertThat(result).containsAll(expected);
  }
}
