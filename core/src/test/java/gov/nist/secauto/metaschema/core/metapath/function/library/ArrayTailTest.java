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
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ArrayTailTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            array(integer(6), integer(7), integer(8)),
            "array:tail([5, 6, 7, 8])"),
        Arguments.of(
            array(),
            "array:tail([5])"),
        Arguments.of(
            array(sequence(string("b"), string("c")), string("d")),
            "array:tail([\"a\", (\"b\", \"c\"), \"d\"])"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IArrayItem<?> expected, @NonNull String metapath) {

    IArrayItem<?> result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
