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
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnIndexOfTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(),
            "index-of((10, 20, 30, 40), 35)"),
        Arguments.of(
            sequence(integer(2), integer(5)),
            "index-of((10, 20, 30, 30, 20, 10), 20)"),
        Arguments.of(
            sequence(integer(1), integer(4)),
            "index-of(('a', 'sport', 'and', 'a', 'pasttime'), 'a')"),
        Arguments.of(
            ISequence.empty(),
            "index-of(current-date(), 23)"),
        Arguments.of(
            sequence(integer(1)),
            "index-of((true()), 'true')"),
        Arguments.of(
            sequence(integer(3), integer(4)),
            "index-of([1, [5, 6], [6, 7]], 6)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<IIntegerItem> expected, @NonNull String metapath) {
    assertEquals(expected, IMetapathExpression.compile(metapath)
        .evaluate(null, newDynamicContext()));
  }
}
