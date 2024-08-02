/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnCeilingTest
    extends FunctionTestBase {

  private static Stream<Arguments> provideValues() {
    return Stream.of(
        Arguments.of(decimal("11"), decimal("10.5")),
        Arguments.of(decimal("-10"), decimal("-10.5")));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testCeiling(@NonNull INumericItem expected, @NonNull INumericItem actual) {
    assertFunctionResult(
        FnCeiling.SIGNATURE,
        ISequence.of(expected),
        List.of(ISequence.of(actual)));
  }

  @Test
  void testNoOp() {
    assertFunctionResult(
        FnCeiling.SIGNATURE,
        ISequence.empty(),
        List.of(ISequence.empty()));
  }
}
