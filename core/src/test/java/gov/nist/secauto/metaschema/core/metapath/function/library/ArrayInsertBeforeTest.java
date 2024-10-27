/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.array;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ArrayInsertBeforeTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            array(string("a"), string("b"), sequence(string("x"), string("y")), string("c"), string("d")),
            "array:insert-before([\"a\", \"b\", \"c\", \"d\"], 3, (\"x\", \"y\"))"),
        Arguments.of(
            array(string("a"), string("b"), string("c"), string("d"), sequence(string("x"), string("y"))),
            "array:insert-before([\"a\", \"b\", \"c\", \"d\"], 5, (\"x\", \"y\"))"),
        Arguments.of(
            array(sequence(string("x"), string("y")), string("a"), string("b"), string("c"), string("d")),
            "array:insert-before([\"a\", \"b\", \"c\", \"d\"], 1, (\"x\", \"y\"))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IItem expected, @NonNull String metapath) {
    IItem result = MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
