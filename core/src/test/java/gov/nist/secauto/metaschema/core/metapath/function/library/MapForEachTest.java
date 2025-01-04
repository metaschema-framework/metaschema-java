/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.entry;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.map;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.assertj.core.api.Assertions.assertThat;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

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
