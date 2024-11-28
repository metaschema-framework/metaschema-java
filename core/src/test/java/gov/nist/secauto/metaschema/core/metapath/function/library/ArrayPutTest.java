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
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ArrayPutTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            array(string("a"), string("d"), string("c")),
            "array:put([\"a\", \"b\", \"c\"], 2, \"d\")"),
        Arguments.of(
            array(string("a"), sequence(string("d"), string("e")), string("c")),
            "array:put([\"a\", \"b\", \"c\"], 2, (\"d\", \"e\"))"),
        Arguments.of(
            array(array(string("d"), string("e"))),
            "array:put([\"a\"], 1, [\"d\", \"e\"]) "));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IItem expected, @NonNull String metapath) {

    IItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
