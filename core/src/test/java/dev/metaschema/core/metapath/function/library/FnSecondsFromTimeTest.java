/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.decimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.atomic.IDecimalItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnSecondsFromTimeTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            decimal(10.5),
            "fn:seconds-from-time(meta:time('13:20:10.5'))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IDecimalItem expected, @NonNull String metapath) {
    assertEquals(expected, IMetapathExpression.compile(metapath).evaluateAs(null, IMetapathExpression.ResultType.ITEM,
        newDynamicContext()));
  }
}
