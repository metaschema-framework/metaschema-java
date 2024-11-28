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

class FnEndsWithTest
    extends FunctionTestBase {
  static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(bool(true), "ends-with( \"tattoo\", \"tattoo\")"),
        Arguments.of(bool(false), "ends-with ( \"tattoo\", \"atto\")"),
        Arguments.of(bool(true), "ends-with( (), ())"),
        Arguments.of(bool(true), "ends-with( \"\", ())"),
        Arguments.of(bool(true), "ends-with( \"something\", \"\")"),
        Arguments.of(bool(true), "ends-with( \"something\", ())"),
        Arguments.of(bool(false), "ends-with( \"\", \"nothing\")"),
        Arguments.of(bool(false), "ends-with( (), \"nothing\")"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IBooleanItem expected, @NonNull String metapath) {
    IBooleanItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }
}
