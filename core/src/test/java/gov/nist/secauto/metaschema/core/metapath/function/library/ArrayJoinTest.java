/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.array;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
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

class ArrayJoinTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            array(),
            "array:join(())"),
        Arguments.of(
            array(integer(1), integer(2), integer(3)),
            "array:join([1, 2, 3])"),
        Arguments.of(
            array(string("a"), string("b"), string("c"), string("d")),
            "array:join(([\"a\", \"b\"], [\"c\", \"d\"]))"),
        Arguments.of(
            array(string("a"), string("b"), string("c"), string("d")),
            "array:join(([\"a\", \"b\"], [\"c\", \"d\"], [ ]))"),
        Arguments.of(
            array(string("a"), string("b"), string("c"), string("d"), array(string("e"), string("f"))),
            "array:join(([\"a\", \"b\"], [\"c\", \"d\"], [[\"e\", \"f\"]]))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IItem expected, @NonNull String metapath) {
    IItem result = MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
