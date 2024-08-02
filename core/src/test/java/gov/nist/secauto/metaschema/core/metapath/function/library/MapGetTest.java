/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.function.LookupTest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class MapGetTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(string("Donnerstag")),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:get($week, 4)"),
        Arguments.of(
            sequence(),
            "let $week :=  " + LookupTest.WEEKDAYS_GERMAN + " return map:get($week, 9)"),
        Arguments.of(
            sequence(),
            "map:get(map:entry(7,()), 7)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull ISequence<?> expected, @NonNull String metapath) {

    ISequence<?> result = MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.SEQUENCE, newDynamicContext());
    assertEquals(expected, result);
  }
}
