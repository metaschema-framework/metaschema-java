/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.array;
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

class ArrayRemoveTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            array(string("b"), string("c"), string("d")),
            "array:remove([\"a\", \"b\", \"c\", \"d\"], 1)"),
        Arguments.of(
            array(string("a"), string("c"), string("d")),
            "array:remove([\"a\", \"b\", \"c\", \"d\"], 2)"),
        Arguments.of(
            array(),
            "array:remove([\"a\"], 1)"),
        Arguments.of(
            array(string("d")),
            "array:remove([\"a\", \"b\", \"c\", \"d\"], 1 to 3)"),
        Arguments.of(
            array(string("a"), string("b"), string("c"), string("d")),
            "array:remove([\"a\", \"b\", \"c\", \"d\"], ())"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IItem expected, @NonNull String metapath) {

    IItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
