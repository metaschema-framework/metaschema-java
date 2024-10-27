/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.function;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.array;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class IArrayItemTest
    extends ExpressionTestBase {
  private static Stream<Arguments> squareConstructorValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            array(integer(1), integer(2), integer(5), integer(7)),
            "[ 1, 2, 5, 7 ]"),
        Arguments.of(
            array(sequence(), sequence(integer(27), integer(17), integer(0))),
            "[ (), (27, 17, 0)]"),
        Arguments.of(
            array(sequence(string("a"), string("b")), sequence(string("c"), string("d"))),
            "[(\"a\", \"b\"), (\"c\", \"d\")]"));
  }

  @ParameterizedTest
  @MethodSource("squareConstructorValues")
  void testSquareConstructor(@NonNull IArrayItem<?> expected, @NonNull String metapath) {
    IArrayItem<?> result = MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }

  private static Stream<Arguments> curlyConstructorValues() { // NOPMD - false positive
    return Stream.of(
        // curly constructor
        Arguments.of(
            array(integer(1), integer(2), integer(5), integer(7)),
            "array { 1, 2, 5, 7 }"),
        Arguments.of(
            array(integer(27), integer(17), integer(0)),
            "array { (), (27, 17, 0) }"));
  }

  @ParameterizedTest
  @MethodSource("curlyConstructorValues")
  void testCurlyConstructor(@NonNull IArrayItem<?> expected, @NonNull String metapath) {
    IArrayItem<?> result = MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }

}
