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

class FnYearFromDateTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            integer(1999),
            "fn:year-from-date(meta:date('1999-05-31'))"),
        Arguments.of(
            integer(2000),
            "fn:year-from-date(meta:date('2000-01-01+05:00'))"),
        Arguments.of(
            integer(-2),
            "fn:year-from-date(meta:date('-0002-06-01'))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IIntegerItem expected, @NonNull String metapath) {
    assertEquals(expected, IMetapathExpression.compile(metapath).evaluateAs(null, IMetapathExpression.ResultType.ITEM,
        newDynamicContext()));
  }
}
