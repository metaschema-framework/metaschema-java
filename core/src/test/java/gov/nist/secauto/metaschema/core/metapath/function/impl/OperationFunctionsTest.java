/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.impl;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.function.ArithmeticFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class OperationFunctionsTest {
  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Numeric {
    private Stream<Arguments> provideValuesOpNumericAdd() {
      return Stream.of(
          Arguments.of(integer(2), integer(1), integer(1)),
          Arguments.of(decimal("2.0"), decimal("1.0"), integer(1)),
          Arguments.of(decimal("2.0"), integer(1), decimal("1.0")),
          Arguments.of(decimal("2.0"), decimal("1.0"), decimal("1.0")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericAdd")
    void testOpNumericAdd(
        @NonNull INumericItem expected,
        @NonNull INumericItem addend1,
        @NonNull INumericItem addend2) {
      assertEquals(expected, OperationFunctions.opNumericAdd(addend1, addend2));
    }

    private Stream<Arguments> provideValuesOpNumericSubtract() {
      return Stream.of(
          Arguments.of(integer(0), integer(1), integer(1)),
          Arguments.of(decimal("0"), decimal("1.0"), integer(1)),
          Arguments.of(decimal("0"), integer(1), decimal("1.0")),
          Arguments.of(decimal("0"), decimal("1.0"), decimal("1.0")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericSubtract")
    void testOpNumericSubtract(
        @NonNull INumericItem expected,
        @NonNull INumericItem minuend,
        @NonNull INumericItem subtrahend) {
      assertEquals(expected, OperationFunctions.opNumericSubtract(minuend, subtrahend));
    }

    private Stream<Arguments> provideValuesOpNumericMultiply() {
      return Stream.of(
          Arguments.of(integer(0), integer(1), integer(0)),
          Arguments.of(integer(1), integer(1), integer(1)),
          Arguments.of(integer(2), integer(1), integer(2)),
          Arguments.of(decimal("1.0"), decimal("1.0"), integer(1)),
          Arguments.of(decimal("1.0"), integer(1), decimal("1.0")),
          Arguments.of(decimal("1.0"), decimal("1.0"), decimal("1.0")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericMultiply")
    void testOpNumericMultiply(
        @NonNull INumericItem expected,
        @NonNull INumericItem multiplicand,
        @NonNull INumericItem multiplier) {
      assertEquals(expected, OperationFunctions.opNumericMultiply(multiplicand, multiplier));
    }

    private Stream<Arguments> provideValuesOpNumericDivide() {
      return Stream.of(
          Arguments.of(decimal("0"), integer(0), integer(1)),
          Arguments.of(decimal("1"), integer(1), integer(1)),
          Arguments.of(decimal("0.5"), integer(1), integer(2)),
          Arguments.of(decimal("1.0"), decimal("1.0"), integer(1)),
          Arguments.of(decimal("1.0"), integer(1), decimal("1.0")),
          Arguments.of(decimal("1.0"), decimal("1.0"), decimal("1.0")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericDivide")
    void testOpNumericDivide(
        @NonNull INumericItem expected,
        @NonNull INumericItem dividend,
        @NonNull INumericItem divisor) {
      assertEquals(expected, OperationFunctions.opNumericDivide(dividend, divisor));
    }

    private Stream<Arguments> provideValuesOpNumericDivideByZero() {
      return Stream.of(
          Arguments.of(integer(0), integer(0)),
          Arguments.of(decimal("1.0"), integer(0)),
          Arguments.of(integer(1), decimal("0.0")),
          Arguments.of(integer(1), decimal("0")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericDivideByZero")
    void testOpNumericDivideByZero(
        @NonNull INumericItem dividend,
        @NonNull INumericItem divisor) {
      ArithmeticFunctionException thrown = assertThrows(ArithmeticFunctionException.class, () -> {
        OperationFunctions.opNumericDivide(dividend, divisor);
      });
      assertEquals(ArithmeticFunctionException.DIVISION_BY_ZERO, thrown.getCode());
    }

    private Stream<Arguments> provideValuesOpNumericIntegerDivide() {
      return Stream.of(
          Arguments.of(integer(3), integer(10), integer(3)),
          Arguments.of(integer(-1), integer(3), integer(-2)),
          Arguments.of(integer(-1), integer(-3), integer(2)),
          Arguments.of(integer(3), decimal("9.0"), integer(3)),
          Arguments.of(integer(-1), decimal("-3.5"), integer(3)),
          Arguments.of(integer(0), decimal("3.0"), integer(4)),
          Arguments.of(integer(5), decimal("3.1E1"), integer(6)),
          Arguments.of(integer(4), decimal("3.1E1"), integer(7)));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericIntegerDivide")
    void testOpNumericIntegerDivide(
        @NonNull IIntegerItem expected,
        @NonNull INumericItem dividend,
        @NonNull INumericItem divisor) {
      assertEquals(expected, OperationFunctions.opNumericIntegerDivide(dividend, divisor));
    }

    private Stream<Arguments> provideValuesOpNumericIntegerDivideByZero() {
      return Stream.of(
          Arguments.of(integer(0), integer(0)),
          Arguments.of(decimal("1.0"), integer(0)),
          Arguments.of(integer(1), decimal("0.0")),
          Arguments.of(integer(1), decimal("0")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericIntegerDivideByZero")
    void testOpNumericIntegerDivideByZero(
        @NonNull INumericItem dividend,
        @NonNull INumericItem divisor) {
      ArithmeticFunctionException thrown = assertThrows(ArithmeticFunctionException.class, () -> {
        OperationFunctions.opNumericIntegerDivide(dividend, divisor);
      });
      assertEquals(ArithmeticFunctionException.DIVISION_BY_ZERO, thrown.getCode());
    }

    private Stream<Arguments> provideValuesOpNumericMod() {
      return Stream.of(
          Arguments.of(integer(1), integer(10), integer(3)),
          Arguments.of(decimal(0), integer(6), integer(-2)),
          Arguments.of(decimal(3.0E0), decimal(1.23E2), decimal(0.6E1)),
          Arguments.of(integer(2), integer(5), integer(3)),
          Arguments.of(integer(0), integer(6), integer(-2)),
          Arguments.of(decimal("0.9"), decimal("4.5"), decimal("1.2")),
          Arguments.of(integer(3), integer(123), integer(6)));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericMod")
    void testOpNumericMod(@Nullable INumericItem expected, @NonNull INumericItem dividend,
        @NonNull INumericItem divisor) {
      assertEquals(expected, OperationFunctions.opNumericMod(dividend, divisor));
    }
  }

  @Nested
  class YearMonthDuration {

    @Test
    void testOpAddYearMonthDurations() {
      assertEquals(
          IYearMonthDurationItem.valueOf("P6Y2M"),
          OperationFunctions.opAddYearMonthDurations(
              IYearMonthDurationItem.valueOf("P2Y11M"),
              IYearMonthDurationItem.valueOf("P3Y3M")));
    }

    @Test
    void testOpAddYearMonthDurationsOverflow() {
      DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
        OperationFunctions.opAddYearMonthDurations(
            IYearMonthDurationItem.valueOf("P" + Integer.MAX_VALUE + "Y"),
            IYearMonthDurationItem.valueOf("P" + Integer.MAX_VALUE + "Y"));
      });
      assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
    }

    @Test
    void testOpSubtractYearMonthDurations() {
      assertEquals(
          IYearMonthDurationItem.valueOf("-P4M"),
          OperationFunctions.opSubtractYearMonthDurations(
              IYearMonthDurationItem.valueOf("P2Y11M"),
              IYearMonthDurationItem.valueOf("P3Y3M")));
    }

    @Test
    void testOpSubtractYearMonthDurationsOverflow() {
      DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
        OperationFunctions.opSubtractYearMonthDurations(
            IYearMonthDurationItem.valueOf("P-" + Integer.MAX_VALUE + "Y"),
            IYearMonthDurationItem.valueOf("P" + Integer.MAX_VALUE + "Y"));
      });
      assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
    }

    @Test
    void testOpMultiplyYearMonthDuration() {
      assertAll(
          () -> assertEquals(
              IYearMonthDurationItem.valueOf("P6Y9M"),
              OperationFunctions.opMultiplyYearMonthDuration(
                  IYearMonthDurationItem.valueOf("P2Y11M"),
                  IDecimalItem.valueOf("2.3"))),
          () -> assertEquals(
              IYearMonthDurationItem.valueOf("P0M"),
              OperationFunctions.opMultiplyYearMonthDuration(
                  IYearMonthDurationItem.valueOf("P1Y"),
                  IDecimalItem.valueOf("0"))),
          () -> assertEquals(
              IYearMonthDurationItem.valueOf("-P2Y"),
              OperationFunctions.opMultiplyYearMonthDuration(
                  IYearMonthDurationItem.valueOf("P1Y"),
                  IDecimalItem.valueOf("-2"))));
    }

    @Test
    void testOpMultiplyYearMonthDurationsOverflow() {
      DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
        OperationFunctions.opMultiplyYearMonthDuration(
            IYearMonthDurationItem.valueOf("P" + Integer.MAX_VALUE + "Y"),
            IDecimalItem.valueOf("2.5"));
      });
      assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
    }

    @Test
    void testOpDivideYearMonthDuration() {
      assertEquals(
          IYearMonthDurationItem.valueOf("P1Y11M"),
          OperationFunctions.opDivideYearMonthDuration(
              IYearMonthDurationItem.valueOf("P2Y11M"),
              IDecimalItem.valueOf("1.5")));
    }

    @Test
    void testOpDivideYearMonthDurationByYearMonthDuration() {
      assertEquals(
          IDecimalItem.valueOf("-2.5"),
          OperationFunctions.opDivideYearMonthDurationByYearMonthDuration(
              IYearMonthDurationItem.valueOf("P3Y4M"),
              IYearMonthDurationItem.valueOf("-P1Y4M")));
    }
  }

  @Nested
  class DayTimeDuration {

    @Test
    void testOpAddDayTimeDurations() {
      assertEquals(
          IDayTimeDurationItem.valueOf("P8DT5M"),
          OperationFunctions.opAddDayTimeDurations(
              IDayTimeDurationItem.valueOf("P2DT12H5M"),
              IDayTimeDurationItem.valueOf("P5DT12H")));
    }

    @Test
    void testOpAddDayTimeDurationsOverflow() {
      DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
        OperationFunctions.opAddDayTimeDurations(
            IDayTimeDurationItem.valueOf("PT" + Long.MAX_VALUE + "S"),
            IDayTimeDurationItem.valueOf("PT" + Long.MAX_VALUE + "S"));
      });
      assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
    }

    @Test
    void testOpSubtractDayTimeDurations() {
      assertEquals(
          IDayTimeDurationItem.valueOf("P1DT1H30M"),
          OperationFunctions.opSubtractDayTimeDurations(
              IDayTimeDurationItem.valueOf("P2DT12H"),
              IDayTimeDurationItem.valueOf("P1DT10H30M")));
    }

    @Test
    void testOpSubtractDayTimeDurationsOverflow() {
      DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
        OperationFunctions.opSubtractDayTimeDurations(
            IDayTimeDurationItem.valueOf("PT-" + Long.MAX_VALUE + "S"),
            IDayTimeDurationItem.valueOf("PT" + Long.MAX_VALUE + "S"));
      });
      assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
    }

    @Test
    void testOpMultiplyDayTimeDuration() {
      assertEquals(
          IDayTimeDurationItem.valueOf("PT4H33M"),
          OperationFunctions.opMultiplyDayTimeDuration(
              IDayTimeDurationItem.valueOf("PT2H10M"),
              IDecimalItem.valueOf("2.1")));
    }

    @Test
    void testOpMultiplyDayTimeDurationsOverflow() {
      DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
        OperationFunctions.opMultiplyDayTimeDuration(
            IDayTimeDurationItem.valueOf("PT" + Long.MAX_VALUE + "S"),
            IDecimalItem.valueOf("2.5"));
      });
      assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
    }

    @Test
    void testOpDivideDayTimeDuration() {
      assertEquals(
          IDayTimeDurationItem.valueOf("PT17H40M7S"),
          OperationFunctions.opDivideDayTimeDuration(
              IDayTimeDurationItem.valueOf("P1DT2H30M10.5S"),
              IDecimalItem.valueOf("1.5")));
    }

    @Test
    void testOpDivideDayTimeDurationByDayTimeDuration() {
      assertAll(
          () -> assertEquals(
              IDecimalItem.valueOf("1.437834967320261"),
              OperationFunctions.opDivideDayTimeDurationByDayTimeDuration(
                  IDayTimeDurationItem.valueOf("P2DT53M11S"),
                  IDayTimeDurationItem.valueOf("P1DT10H"))),
          () -> assertEquals(
              IDecimalItem.valueOf("175991.0"),
              OperationFunctions.opDivideDayTimeDurationByDayTimeDuration(
                  IDayTimeDurationItem.valueOf("P2DT53M11S"),
                  IDayTimeDurationItem.valueOf("PT1S"))));
    }
  }
}
