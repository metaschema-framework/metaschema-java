/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.decimal;
import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.string;

import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnCountTest
    extends FunctionTestBase {
  static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(integer(0), new IItem[] {}),
        Arguments.of(integer(2), new IItem[] { string("value1"), decimal("2.0") }));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable IIntegerItem expected, @NonNull IItem... items) {
    assertFunctionResult(
        FnCount.SIGNATURE,
        ISequence.of(expected),
        CollectionUtil.singletonList(ISequence.of(items)));
  }
}
