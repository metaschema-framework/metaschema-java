/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.item.atomic;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.function.InvalidValueForCastFunctionException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class INumericItemTest {

  private static Stream<Arguments> provideValuesForAbs() {
    return Stream.of(
        Arguments.of(integer(10), integer(10)),
        Arguments.of(integer(-10), integer(10)),
        Arguments.of(decimal("10.5"), decimal("10.5")),
        Arguments.of(decimal("-10.5"), decimal("10.5")));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForAbs")
  void testAbs(INumericItem arg, INumericItem expected) {
    INumericItem result = arg.abs();
    assertEquals(expected, result);
  }

  private static Stream<Arguments> provideValuesForCeiling() {
    return Stream.of(
        Arguments.of(integer(10), integer(10)),
        Arguments.of(integer(-10), integer(-10)),
        Arguments.of(decimal("10.5"), integer(11)),
        Arguments.of(decimal("-10.5"), integer(-10)));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForCeiling")
  void testCeiling(@NonNull INumericItem arg, @NonNull INumericItem expected) {
    INumericItem result = arg.ceiling();
    assertEquals(expected, result);
  }

  private static Stream<Arguments> provideValuesForFloor() {
    return Stream.of(
        Arguments.of(integer(10), integer(10)),
        Arguments.of(integer(-10), integer(-10)),
        Arguments.of(decimal("10.5"), integer(10)),
        Arguments.of(decimal("-10.5"), integer(-11)));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForFloor")
  void testFloor(@NonNull INumericItem arg, @NonNull INumericItem expected) {
    INumericItem result = arg.floor();
    assertEquals(expected, result);
  }

  private static Stream<Arguments> provideValuesForRound() {
    return Stream.of(
        Arguments.of(integer(-100), integer(-3), integer(0)),
        Arguments.of(integer(-153), integer(-2), integer(-200)),
        Arguments.of(integer(-153), integer(-1), integer(-150)),
        Arguments.of(integer(654_321), integer(-6), integer(0)),
        Arguments.of(integer(654_321), integer(-5), integer(700_000)),
        Arguments.of(integer(654_321), integer(-4), integer(650_000)),
        Arguments.of(integer(654_321), integer(0), integer(654_321)),
        Arguments.of(integer(654_321), integer(2), integer(654_321)),
        Arguments.of(decimal("2.5"), integer(0), decimal("3")),
        Arguments.of(decimal("2.4999"), integer(0), decimal("2")),
        Arguments.of(decimal("-2.5"), integer(0), decimal("-2")),
        Arguments.of(decimal("2.4999"), integer(1), decimal("2.5")),
        Arguments.of(decimal("-2.5"), integer(1), decimal("-2.5")),
        Arguments.of(decimal("1.125"), integer(2), decimal("1.13")),
        Arguments.of(integer(8_452), integer(-2), integer(8_500)),
        Arguments.of(decimal("3.1415e0"), integer(2), decimal("3.14")),
        Arguments.of(decimal("35.425e0"), integer(2), decimal("35.43")));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForRound")
  void testRound(@NonNull INumericItem arg, @NonNull IIntegerItem precision, @NonNull INumericItem expected) {
    INumericItem result = arg.round(precision);
    assertEquals(expected, result);
  }

  private static Stream<Arguments> provideValuesForCast() {
    return Stream.of(
        Arguments.of(integer(-100), integer(-100)),
        Arguments.of(integer(654_321), integer(654_321)),
        Arguments.of(decimal("2.4999"), decimal("2.4999")), // NOPMD
        Arguments.of(decimal("3.1415e0"), decimal("3.1415e0")), // NOPMD
        Arguments.of(string("-100"), decimal("-100")),
        Arguments.of(string("654321"), decimal("654321")),
        Arguments.of(string("2.5"), decimal("2.5")),
        Arguments.of(string("2.4999"), decimal("2.4999")),
        Arguments.of(string("-2.5"), decimal("-2.5")),
        Arguments.of(string("1.125"), decimal("1.125")),
        Arguments.of(string("3.1415e0"), decimal("3.1415e0")),
        Arguments.of(string("35.425e0"), decimal("35.425e0")));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForCast")
  void testCast(@NonNull IAnyAtomicItem item, @NonNull INumericItem expected) {
    INumericItem result = INumericItem.cast(item);
    assertEquals(expected, result);
  }

  private static Stream<Arguments> provideValuesForCastFail() {
    return Stream.of(
        Arguments.of(string("x123")),
        Arguments.of(string("abc")),
        Arguments.of(string("")));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForCastFail")
  void testCastFail(@NonNull IAnyAtomicItem item) {
    Assertions.assertThrows(InvalidValueForCastFunctionException.class, () -> {
      INumericItem.cast(item);
    });
  }
}
