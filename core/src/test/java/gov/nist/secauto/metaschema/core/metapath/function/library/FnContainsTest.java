/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnContainsTest
    extends FunctionTestBase {
  static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(bool(true), "contains( \"tattoo\", \"t\")"),
        Arguments.of(bool(false), "contains ( \"tattoo\", \"ttt\")"),
        Arguments.of(bool(true), "ends-with( (), ())"),
        Arguments.of(bool(true), "contains( \"\", ())"),
        Arguments.of(bool(true), "contains( \"something\", \"\")"),
        Arguments.of(bool(true), "contains( \"something\", ())"),
        Arguments.of(bool(false), "contains( \"\", \"nothing\")"),
        Arguments.of(bool(false), "contains( (), \"nothing\")"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IBooleanItem expected, @NonNull String metapath) {
    IBooleanItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
