/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnDistinctValuesTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(
            sequence(decimal(2.0)),
            "fn:distinct-values((2.0, 2))"),
        Arguments.of(
            sequence(integer(1), decimal(2.0), integer(3)),
            "fn:distinct-values((1, 2.0, 3, 2))"),
        Arguments.of(
            sequence(string("cherry"), string("plum")),
            "fn:distinct-values((meta:string('cherry'),meta:string('plum'),meta:string('plum')))"),
        Arguments.of(
            sequence(string("a"), integer(2)),
            "fn:distinct-values(('a', 2, 'a', 2.0))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    Set<? extends IItem> expectedItems = new HashSet<>(expected);
    Set<? extends IItem> actualItems = new HashSet<>(IMetapathExpression.compile(metapath)
        .evaluate(null, newDynamicContext()));

    assertEquals(expectedItems, actualItems);
  }
}
