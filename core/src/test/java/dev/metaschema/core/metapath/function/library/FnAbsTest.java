/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.decimal;
import static dev.metaschema.core.metapath.TestUtils.integer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.INumericItem;
import dev.metaschema.core.util.CollectionUtil;
import edu.umd.cs.findbugs.annotations.NonNull;

class FnAbsTest
    extends FunctionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(decimal("10.5"), decimal("10.5")),
        Arguments.of(decimal("10.5"), decimal("-10.5")),
        Arguments.of(integer(5), integer(5)),
        Arguments.of(integer(5), integer(-5)));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testRound(@NonNull INumericItem expected, @NonNull INumericItem actual) {
    assertFunctionResult(
        FnAbs.SIGNATURE,
        ISequence.of(expected),
        CollectionUtil.singletonList(ISequence.of(actual)));
  }

  @Test
  void testNoOp() {
    assertFunctionResult(
        FnAbs.SIGNATURE,
        ISequence.empty(),
        CollectionUtil.singletonList(ISequence.empty()));
  }
}
