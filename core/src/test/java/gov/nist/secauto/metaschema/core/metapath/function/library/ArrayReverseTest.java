/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.array;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ArrayReverseTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            array(string("d"), string("c"), string("b"), string("a")),
            "array:reverse([\"a\", \"b\", \"c\", \"d\"])"),
        Arguments.of(
            array(sequence(string("c"), string("d")), sequence(string("a"), string("b"))),
            "array:reverse([(\"a\", \"b\"), (\"c\", \"d\")])"),
        Arguments.of(
            array(sequence(integer(1), integer(2), integer(3), integer(4), integer(5))),
            "array:reverse([(1 to 5)])"),
        Arguments.of(
            array(),
            "array:reverse([])"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IItem expected, @NonNull String metapath) {
    IItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
