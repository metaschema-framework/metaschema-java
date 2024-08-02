/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.function.OperationFunctions;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class OperationFunctionsTest {
  private static Stream<Arguments> provideValuesForIntegerDivide() {
    return Stream.of(
        Arguments.of(integer(10), integer(3), integer(3)),
        Arguments.of(integer(3), integer(-2), integer(-1)),
        Arguments.of(integer(-3), integer(2), integer(-1)),
        Arguments.of(integer(-3), integer(-2), integer(1)),
        Arguments.of(decimal("9.0"), integer(3), integer(3)),
        Arguments.of(decimal("-3.5"), integer(3), integer(-1)),
        Arguments.of(decimal("3.0"), integer(4), integer(0)),
        Arguments.of(decimal("3.1E1"), integer(6), integer(5)),
        Arguments.of(decimal("3.1E1"), integer(7), integer(4)));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForIntegerDivide")
  void testIntegerDivide(@NonNull INumericItem dividend, @NonNull INumericItem divisor,
      @NonNull IIntegerItem expected) {
    INumericItem result = OperationFunctions.opNumericIntegerDivide(dividend, divisor);
    assertEquals(expected, result);
  }

  private static Stream<Arguments> provideValuesForMod() {
    return Stream.of(
        Arguments.of(integer(5), integer(3), decimal("2")),
        Arguments.of(integer(6), integer(-2), decimal("0")),
        Arguments.of(decimal("4.5"), decimal("1.2"), decimal("0.9")),
        Arguments.of(integer(123), integer(6), decimal("3")));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForMod")
  void test(@NonNull INumericItem dividend, @NonNull INumericItem divisor, @NonNull INumericItem expected) {
    INumericItem result = OperationFunctions.opNumericMod(dividend, divisor);
    assertEquals(expected, result);
  }

}
