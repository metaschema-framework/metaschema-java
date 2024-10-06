/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnRoundTest
    extends FunctionTestBase {

  private static Stream<Arguments> provideValuesForRound() {
    return Stream.of(
        Arguments.of(decimal("2.5"), decimal("3")),
        Arguments.of(decimal("2.4999"), decimal("2")),
        Arguments.of(decimal("-2.5"), decimal("-2")));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForRound")
  void testRound(@NonNull INumericItem actual, @NonNull INumericItem expected) {
    assertFunctionResult(
        FnRound.SIGNATURE,
        ISequence.of(expected),
        CollectionUtil.singletonList(ISequence.of(actual)));
  }

  @Test
  void testRoundNoOp() {
    assertFunctionResult(
        FnRound.SIGNATURE,
        ISequence.empty(),
        CollectionUtil.singletonList(ISequence.empty()));
  }

  private static Stream<Arguments> provideValuesForRoundWithPrecision() {
    return Stream.of(
        Arguments.of(decimal("1.125"), integer(2), decimal("1.13")),
        Arguments.of(decimal("8452"), integer(-2), integer(8500)),
        Arguments.of(decimal("3.1415e0"), integer(2), decimal("3.14e0")));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForRoundWithPrecision")
  void testRoundWithPrecision(
      @NonNull INumericItem actual,
      @NonNull IIntegerItem precision,
      @NonNull INumericItem expected) {
    assertFunctionResult(
        FnRound.SIGNATURE_WITH_PRECISION,
        ISequence.of(expected),
        ObjectUtils.notNull(List.of(ISequence.of(actual), ISequence.of(precision))));
  }

  @Test
  void testRoundWithPrecisionNoOp() {
    assertFunctionResult(
        FnRound.SIGNATURE_WITH_PRECISION,
        ISequence.empty(),
        CollectionUtil.singletonList(ISequence.empty()));
  }
}
