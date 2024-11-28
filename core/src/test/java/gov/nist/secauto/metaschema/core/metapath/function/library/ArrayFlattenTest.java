/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ArrayFlattenTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(integer(1), integer(4), integer(6), integer(5), integer(3)),
            "array:flatten([1, 4, 6, 5, 3])"),
        Arguments.of(
            sequence(integer(1), integer(2), integer(5), integer(10), integer(11), integer(12), integer(13)),
            "array:flatten(([1, 2, 5], [[10, 11], 12], [], 13))"),
        Arguments.of(
            sequence(integer(1), integer(0), integer(1), integer(1), integer(0), integer(1), integer(0), integer(0)),
            "array:flatten([(1,0), (1,1), (0,1), (0,0)])"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull ISequence<?> expected, @NonNull String metapath) {

    ISequence<?> result = IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext());
    assertEquals(expected, result);
  }
}
