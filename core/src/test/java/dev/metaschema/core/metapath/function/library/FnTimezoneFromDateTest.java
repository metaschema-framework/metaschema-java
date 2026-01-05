/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static dev.metaschema.core.metapath.TestUtils.sequence;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnTimezoneFromDateTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(dayTimeDuration("-PT5H")),
            "fn:timezone-from-date(meta:date('1999-05-31-05:00'))"),
        Arguments.of(
            sequence(dayTimeDuration("PT0S")),
            "fn:timezone-from-date(meta:date('2000-06-12Z'))"),
        Arguments.of(
            sequence(),
            "fn:timezone-from-date(meta:date('1999-05-31'))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(
      @Nullable ISequence<IDayTimeDurationItem> expected,
      @NonNull String metapath) {
    ISequence<IDayTimeDurationItem> result = IMetapathExpression.compile(metapath)
        .evaluate(null, newDynamicContext());
    assertEquals(expected, result);
  }
}
