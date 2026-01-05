/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.bool;
import static dev.metaschema.core.metapath.TestUtils.integer;

import dev.metaschema.core.metapath.item.IItem;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IBooleanItem;
import dev.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnExistsTest
    extends FunctionTestBase {
  private static Stream<Arguments> provideValuesForExists() {
    return Stream.of(
        Arguments.of(bool(true), new IItem[] { integer(3) }),
        Arguments.of(bool(false), new IItem[] {}));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForExists")
  void test(@Nullable IBooleanItem expected, @NonNull IItem... values) {
    assertFunctionResult(
        FnExists.SIGNATURE,
        ISequence.of(expected),
        CollectionUtil.singletonList(ISequence.of(values)));
  }
}
