/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnCeilingTest
    extends FunctionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(integer(11), decimal("10.5")),
        Arguments.of(integer(-10), decimal("-10.5")),
        Arguments.of(integer(11), decimal("10.1")),
        Arguments.of(integer(0), decimal("0.0")),
        Arguments.of(integer(1), decimal("0.999999")));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testCeiling(@NonNull INumericItem expected, @NonNull INumericItem actual) {
    assertFunctionResult(
        FnCeiling.SIGNATURE,
        ISequence.of(expected),
        CollectionUtil.singletonList(ISequence.of(actual)));
  }

  @Test
  void testNoOp() {
    assertFunctionResult(
        FnCeiling.SIGNATURE,
        ISequence.empty(),
        CollectionUtil.singletonList(ISequence.empty()));
  }
}
