/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnMinutesFromDateTimeTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            integer(20),
            "fn:minutes-from-dateTime(meta:date-time('1999-05-31T13:20:00-05:00'))"),
        Arguments.of(
            integer(30),
            "fn:minutes-from-dateTime(meta:date-time('1999-05-31T13:30:00+05:30'))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IIntegerItem expected, @NonNull String metapath) {
    assertEquals(expected, IMetapathExpression.compile(metapath).evaluateAs(null, IMetapathExpression.ResultType.ITEM,
        newDynamicContext()));
  }
}
