/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.impl;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.duration;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.yearMonthDuration;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.function.ArithmeticFunctionException;
import gov.nist.secauto.metaschema.core.metapath.function.DateTimeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDateTimeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;

import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("Functions and operators on numerics")
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
    @DisplayName("op:numeric-add")
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
    @DisplayName("op:numeric-subtract")
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
    @DisplayName("op:numeric-multiply")
    void testOpNumericMultiply(
        @NonNull INumericItem expected,
        @NonNull INumericItem multiplicand,
        @NonNull INumericItem multiplier) {
      assertEquals(expected, OperationFunctions.opNumericMultiply(multiplicand, multiplier));
    }

    @Nested
    @DisplayName("op:numeric-divide")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NumericDivide {
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
      @DisplayName("op:numeric-divide - known good")
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
      @DisplayName("op:numeric-divide - by zero")
      void testOpNumericDivideByZero(
          @NonNull INumericItem dividend,
          @NonNull INumericItem divisor) {
        ArithmeticFunctionException thrown = assertThrows(ArithmeticFunctionException.class, () -> {
          OperationFunctions.opNumericDivide(dividend, divisor);
        });
        assertEquals(ArithmeticFunctionException.DIVISION_BY_ZERO, thrown.getCode());
      }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("op:numeric-integer-divide")
    class NumericIntegerDivide {
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
      @DisplayName("op:numeric-integer-divide - by zero")
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
    @DisplayName("op:numeric-mod")
    void testOpNumericMod(@Nullable INumericItem expected, @NonNull INumericItem dividend,
        @NonNull INumericItem divisor) {
      assertEquals(expected, OperationFunctions.opNumericMod(dividend, divisor));
    }

    private Stream<Arguments> provideValuesOpNumericUnaryMinus() {
      return Stream.of(
          Arguments.of(integer(-10), integer(10)),
          Arguments.of(integer(3), integer(-3)),
          Arguments.of(decimal(0), integer(-0)),
          Arguments.of(decimal(3.0E0), decimal(-3.0E0)),
          Arguments.of(decimal("-0.9"), decimal("0.9")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericUnaryMinus")
    @DisplayName("op:numeric-unary-minus")
    void testOpNumericUnaryMinus(@Nullable INumericItem expected, @NonNull INumericItem item) {
      assertEquals(expected, OperationFunctions.opNumericUnaryMinus(item));
    }
  }

  @Nested
  @DisplayName("Comparison operators on numeric values")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class NumericComparison {

    private Stream<Arguments> provideValuesOpNumericEqual() {
      return Stream.of(
          Arguments.of(bool(false), null, integer(1)),
          Arguments.of(bool(false), integer(1), null),
          Arguments.of(bool(false), null, null),
          Arguments.of(bool(true), integer(1), integer(1)),
          Arguments.of(bool(true), integer(0), integer(0)),
          Arguments.of(bool(true), decimal(0), integer(-0)),
          Arguments.of(bool(true), decimal(3.0E0), decimal(3.0E0)),
          Arguments.of(bool(false), integer(-10), integer(10)),
          Arguments.of(bool(false), integer(3), integer(-3)),
          Arguments.of(bool(false), decimal(3.0E0), decimal(-3.0E0)),
          Arguments.of(bool(false), decimal("-0.9"), decimal("0.9")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericEqual")
    @DisplayName("op:numeric-equal")
    void testOpNumericUnaryMinus(
        @NonNull IBooleanItem expected,
        @Nullable INumericItem item1,
        @Nullable INumericItem item2) {
      assertEquals(expected, OperationFunctions.opNumericEqual(item1, item2));
    }

    private Stream<Arguments> provideValuesOpNumericLessThan() {
      return Stream.of(
          Arguments.of(bool(false), null, integer(1)),
          Arguments.of(bool(false), integer(1), null),
          Arguments.of(bool(false), null, null),
          Arguments.of(bool(false), integer(1), integer(1)),
          Arguments.of(bool(false), integer(0), integer(0)),
          Arguments.of(bool(false), decimal(0), integer(-0)),
          Arguments.of(bool(false), decimal(3.0E0), decimal(3.0E0)),
          Arguments.of(bool(true), integer(-10), integer(10)),
          Arguments.of(bool(false), integer(3), integer(-3)),
          Arguments.of(bool(false), decimal(3.0E0), decimal(-3.0E0)),
          Arguments.of(bool(true), decimal("-0.9"), decimal("0.9")));
    }

    @ParameterizedTest
    @MethodSource("provideValuesOpNumericLessThan")
    @DisplayName("op:numeric-less-than and op:numeric-greater-than")
    void testOpNumericNumericLessThan(
        @NonNull IBooleanItem expected,
        @Nullable INumericItem item1,
        @Nullable INumericItem item2) {
      assertAll(
          () -> assertEquals(expected, OperationFunctions.opNumericLessThan(item1, item2)),
          () -> assertEquals(expected, OperationFunctions.opNumericGreaterThan(item2, item1)));
    }
  }

  @Nested
  @DisplayName("Functions and operators on Boolean values")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Boolean {

    @Nested
    @DisplayName("Operators on Boolean values")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class BooleanComparison {
      private Stream<Arguments> provideValuesOpBooleanEqual() {
        return Stream.of(
            Arguments.of(bool(true), bool(true), bool(true)),
            Arguments.of(bool(false), bool(true), bool(false)),
            Arguments.of(bool(false), bool(false), bool(true)),
            Arguments.of(bool(true), bool(false), bool(false)));
      }

      @ParameterizedTest
      @MethodSource("provideValuesOpBooleanEqual")
      @DisplayName("op:boolean-equal")
      void testOpBooleanEqual(
          @Nullable IBooleanItem expected,
          @NonNull IBooleanItem item1,
          @NonNull IBooleanItem item2) {
        assertEquals(expected, OperationFunctions.opBooleanEqual(item1, item2));
      }

      private Stream<Arguments> provideValuesOpBooleanLessThan() {
        return Stream.of(
            Arguments.of(bool(false), bool(true), bool(true)),
            Arguments.of(bool(false), bool(true), bool(false)),
            Arguments.of(bool(true), bool(false), bool(true)),
            Arguments.of(bool(false), bool(false), bool(false)));
      }

      @ParameterizedTest
      @MethodSource("provideValuesOpBooleanLessThan")
      @DisplayName("op:boolean-less-than")
      void testOpBooleanLessThan(
          @Nullable IBooleanItem expected,
          @NonNull IBooleanItem item1,
          @NonNull IBooleanItem item2) {
        assertEquals(expected, OperationFunctions.opBooleanLessThan(item1, item2));
      }

      private Stream<Arguments> provideValuesOpBooleanGreaterThan() {
        return Stream.of(
            Arguments.of(bool(false), bool(true), bool(true)),
            Arguments.of(bool(true), bool(true), bool(false)),
            Arguments.of(bool(false), bool(false), bool(true)),
            Arguments.of(bool(false), bool(false), bool(false)));
      }

      @ParameterizedTest
      @MethodSource("provideValuesOpBooleanGreaterThan")
      @DisplayName("op:boolean-greater-than")
      void testOpBooleanGreaterThan(
          @Nullable IBooleanItem expected,
          @NonNull IBooleanItem item1,
          @NonNull IBooleanItem item2) {
        assertEquals(expected, OperationFunctions.opBooleanGreaterThan(item1, item2));
      }
    }
  }

  @Nested
  @DisplayName("Functions and operators on durations")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Duration {
    @Nested
    @DisplayName("Comparison operators on durations")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DurationComparison {
      private Stream<Arguments> provideValuesDurationEqual() {
        return Stream.of(
            Arguments.of(bool(true), duration("P1Y"), duration("P12M")),
            Arguments.of(bool(true), duration("PT24H"), duration("P1D")),
            Arguments.of(bool(false), duration("P1Y"), duration("P365D")),
            Arguments.of(bool(true), duration("P0Y"), duration("P0D")),
            Arguments.of(bool(true), yearMonthDuration("P0Y"), dayTimeDuration("P0D")),
            Arguments.of(bool(false), yearMonthDuration("P1Y"), dayTimeDuration("P365D")),
            Arguments.of(bool(true), yearMonthDuration("P2Y"), yearMonthDuration("P24M")),
            Arguments.of(bool(true), dayTimeDuration("P10D"), dayTimeDuration("PT240H")),
            Arguments.of(bool(true), duration("P2Y0M0DT0H0M0S"), yearMonthDuration("P24M")),
            Arguments.of(bool(true), duration("P0Y0M10D"), dayTimeDuration("PT240H")));
      }

      @ParameterizedTest
      @DisplayName("op:duration-equal")
      @MethodSource("provideValuesDurationEqual")
      void testOpDurationEqual(
          @NonNull IBooleanItem expected,
          @NonNull IDurationItem arg1,
          @NonNull IDurationItem arg2) {
        assertEquals(expected, OperationFunctions.opDurationEqual(arg1, arg2));
      }

      private Stream<Arguments> provideValuesYearMonthDurationLessAndGreaterThan() {
        return Stream.of(
            Arguments.of(bool(false), yearMonthDuration("P1Y"), yearMonthDuration("P11M")),
            Arguments.of(bool(true), yearMonthDuration("P2Y"), yearMonthDuration("P25M")),
            Arguments.of(bool(false), yearMonthDuration("P2Y"), yearMonthDuration("P1Y9M")),
            Arguments.of(bool(true), yearMonthDuration("P1Y"), yearMonthDuration("P15M")),
            Arguments.of(bool(false), yearMonthDuration("P3Y"), yearMonthDuration("P1Y15M")),
            Arguments.of(bool(true), yearMonthDuration("P3Y"), yearMonthDuration("P1Y72M")),
            Arguments.of(bool(false), yearMonthDuration("P1Y"), yearMonthDuration("P12M")),
            Arguments.of(bool(false), yearMonthDuration("P2Y"), yearMonthDuration("P24M")));
      }

      @ParameterizedTest
      @DisplayName("op:yearMonthDuration-less-than and op:yearMonthDuration-greater-than")
      @MethodSource("provideValuesYearMonthDurationLessAndGreaterThan")
      void testOpYearMonthDurationLessAndGreaterThan(
          @NonNull IBooleanItem expected,
          @NonNull IYearMonthDurationItem item1,
          @NonNull IYearMonthDurationItem item2) {
        assertAll(
            () -> assertEquals(expected, OperationFunctions.opYearMonthDurationLessThan(item1, item2)),
            () -> assertEquals(expected, OperationFunctions.opYearMonthDurationGreaterThan(item2, item1)));

      }

      private Stream<Arguments> provideValuesDayTimeDurationLessAndGreaterThan() {
        return Stream.of(
            Arguments.of(bool(false), dayTimeDuration("P1D"), dayTimeDuration("PT24H")),
            Arguments.of(bool(true), dayTimeDuration("P2D"), dayTimeDuration("P1DT25H")),
            Arguments.of(bool(true), dayTimeDuration("P2D"), dayTimeDuration("P1DT86401S")),
            Arguments.of(bool(true), dayTimeDuration("P1D"), dayTimeDuration("P2D")),
            Arguments.of(bool(true), dayTimeDuration("P3D"), dayTimeDuration("PT4321M")),
            Arguments.of(bool(false), dayTimeDuration("P1D"), dayTimeDuration("P1D")),
            Arguments.of(bool(false), dayTimeDuration("PT1H"), dayTimeDuration("PT1H")),
            Arguments.of(bool(false), dayTimeDuration("PT1M"), dayTimeDuration("PT1M")),
            Arguments.of(bool(false), dayTimeDuration("PT1S"), dayTimeDuration("PT1S")),
            Arguments.of(bool(true), dayTimeDuration("PT1S"), dayTimeDuration("PT2S")));
      }

      @ParameterizedTest
      @DisplayName("op:dayTimeDuration-less-than and op:dayTimeDuration-greater-than")
      @MethodSource("provideValuesDayTimeDurationLessAndGreaterThan")
      void testOpDayTimeDurationLessAndGreaterThan(
          @NonNull IBooleanItem expected,
          @NonNull IDayTimeDurationItem item1,
          @NonNull IDayTimeDurationItem item2) {
        assertAll(
            () -> assertEquals(expected, OperationFunctions.opDayTimeDurationLessThan(item1, item2)),
            () -> assertEquals(expected, OperationFunctions.opDayTimeDurationGreaterThan(item2, item1)));
      }
    }

    @Nested
    @DisplayName("Arithmetic operators on durations")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DurationArithmetic {
      @Nested
      class YearMonthDuration {

        @Test
        @DisplayName("op:add-yearMonthDurations: known good")
        void testOpAddYearMonthDurations() {
          assertEquals(
              IYearMonthDurationItem.valueOf("P6Y2M"),
              OperationFunctions.opAddYearMonthDurations(
                  IYearMonthDurationItem.valueOf("P2Y11M"),
                  IYearMonthDurationItem.valueOf("P3Y3M")));
        }

        @Test
        @DisplayName("op:add-yearMonthDurations: overflow")
        void testOpAddYearMonthDurationsOverflow() {
          DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
            OperationFunctions.opAddYearMonthDurations(
                IYearMonthDurationItem.valueOf("P" + Integer.MAX_VALUE + "Y"),
                IYearMonthDurationItem.valueOf("P" + Integer.MAX_VALUE + "Y"));
          });
          assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
        }

        @Test
        @DisplayName("op:subtract-yearMonthDurations: known good")
        void testOpSubtractYearMonthDurations() {
          assertEquals(
              IYearMonthDurationItem.valueOf("-P4M"),
              OperationFunctions.opSubtractYearMonthDurations(
                  IYearMonthDurationItem.valueOf("P2Y11M"),
                  IYearMonthDurationItem.valueOf("P3Y3M")));
        }

        @Test
        @DisplayName("op:subtract-yearMonthDurations: overflow")
        void testOpSubtractYearMonthDurationsOverflow() {
          DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
            OperationFunctions.opSubtractYearMonthDurations(
                IYearMonthDurationItem.valueOf("-P" + Integer.MAX_VALUE + "Y"),
                IYearMonthDurationItem.valueOf("P" + Integer.MAX_VALUE + "Y"));
          });
          assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
        }

        @Test
        @DisplayName("op:multiply-yearMonthDurations: known good")
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
        @DisplayName("op:multiply-yearMonthDurations: overflow")
        void testOpMultiplyYearMonthDurationsOverflow() {
          DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
            OperationFunctions.opMultiplyYearMonthDuration(
                IYearMonthDurationItem.valueOf("P" + Integer.MAX_VALUE + "Y"),
                IDecimalItem.valueOf("2.5"));
          });
          assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
        }

        @Test
        @DisplayName("op:divide-yearMonthDurations: known good")
        void testOpDivideYearMonthDuration() {
          assertEquals(
              IYearMonthDurationItem.valueOf("P1Y11M"),
              OperationFunctions.opDivideYearMonthDuration(
                  IYearMonthDurationItem.valueOf("P2Y11M"),
                  IDecimalItem.valueOf("1.5")));
        }

        @Test
        @DisplayName("op:divide-yearMonthDuration-by-yearMonthDuration: known good")
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
        @DisplayName("op:add-dayTimeDurations: known good")
        void testOpAddDayTimeDurations() {
          assertEquals(
              IDayTimeDurationItem.valueOf("P8DT5M"),
              OperationFunctions.opAddDayTimeDurations(
                  IDayTimeDurationItem.valueOf("P2DT12H5M"),
                  IDayTimeDurationItem.valueOf("P5DT12H")));
        }

        @Test
        @DisplayName("op:add-dayTimeDurations: overflow")
        void testOpAddDayTimeDurationsOverflow() {
          DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
            OperationFunctions.opAddDayTimeDurations(
                // subtracting 807 ensures the long doesn't overflow
                IDayTimeDurationItem.valueOf("PT" + (Long.MAX_VALUE - 807) + "S"),
                IDayTimeDurationItem.valueOf("PT" + (Long.MAX_VALUE - 807) + "S"));
          });
          assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
        }

        @Test
        @DisplayName("op:subtract-dayTimeDurations: known good")
        void testOpSubtractDayTimeDurations() {
          assertEquals(
              IDayTimeDurationItem.valueOf("P1DT1H30M"),
              OperationFunctions.opSubtractDayTimeDurations(
                  IDayTimeDurationItem.valueOf("P2DT12H"),
                  IDayTimeDurationItem.valueOf("P1DT10H30M")));
        }

        @Test
        @DisplayName("op:subtract-dayTimeDurations: overflow")
        void testOpSubtractDayTimeDurationsOverflow() {
          DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
            OperationFunctions.opSubtractDayTimeDurations(
                IDayTimeDurationItem.valueOf("-PT" + (Long.MAX_VALUE - 807) + "S"),
                IDayTimeDurationItem.valueOf("PT" + (Long.MAX_VALUE - 807) + "S"));
          });
          assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
        }

        @Test
        @DisplayName("op:multiply-dayTimeDuration: known good")
        void testOpMultiplyDayTimeDuration() {
          assertEquals(
              IDayTimeDurationItem.valueOf("PT4H33M"),
              OperationFunctions.opMultiplyDayTimeDuration(
                  IDayTimeDurationItem.valueOf("PT2H10M"),
                  IDecimalItem.valueOf("2.1")));
        }

        @Test
        @DisplayName("op:multiply-dayTimeDuration: overflow")
        void testOpMultiplyDayTimeDurationsOverflow() {
          DateTimeFunctionException thrown = assertThrows(DateTimeFunctionException.class, () -> {
            OperationFunctions.opMultiplyDayTimeDuration(
                IDayTimeDurationItem.valueOf("PT" + Long.MAX_VALUE / 2 + "S"),
                IDecimalItem.valueOf("5"));
          });
          assertEquals(DateTimeFunctionException.DURATION_OVERFLOW_UNDERFLOW_ERROR, thrown.getCode());
        }

        @Test
        @DisplayName("op:divide-dayTimeDuration: known good")
        void testOpDivideDayTimeDuration() {
          assertEquals(
              IDayTimeDurationItem.valueOf("PT17H40M7S"),
              OperationFunctions.opDivideDayTimeDuration(
                  IDayTimeDurationItem.valueOf("P1DT2H30M10.5S"),
                  IDecimalItem.valueOf("1.5")));
        }

        @Test
        @DisplayName("op:divide-dayTimeDuration-by-dayTimeDuration: known good")
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
  }

  @Nested
  @DisplayName("Functions and operators on dates and times")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class DateTime {
    @Nested
    @DisplayName("Functions and operators on dates and times")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DateTimeOperators {
      private Stream<Arguments> provideValuesDateTimeEqual() {
        return Stream.of(
            Arguments.of(bool(true), duration("P1Y"), duration("P12M")),
            Arguments.of(bool(true), duration("PT24H"), duration("P1D")),
            Arguments.of(bool(false), duration("P1Y"), duration("P365D")),
            Arguments.of(bool(true), duration("P0Y"), duration("P0D")),
            Arguments.of(bool(true), yearMonthDuration("P0Y"), dayTimeDuration("P0D")),
            Arguments.of(bool(false), yearMonthDuration("P1Y"), dayTimeDuration("P365D")),
            Arguments.of(bool(true), yearMonthDuration("P2Y"), yearMonthDuration("P24M")),
            Arguments.of(bool(true), dayTimeDuration("P10D"), dayTimeDuration("PT240H")),
            Arguments.of(bool(true), duration("P2Y0M0DT0H0M0S"), yearMonthDuration("P24M")),
            Arguments.of(bool(true), duration("P0Y0M10D"), dayTimeDuration("PT240H")));
      }

      @ParameterizedTest
      @DisplayName("op:dateTime-equal")
      @MethodSource("provideValuesDateTimeEqual")
      void testOpDateTimeEqual(
          @NonNull IBooleanItem expected,
          @NonNull IDateTimeItem arg1,
          @NonNull IDateTimeItem arg2) {
        assertEquals(expected, OperationFunctions.opDateTimeEqual(arg1, arg2));
      }

      // TODO: op:dateTime-equal
      // TODO: op:dateTime-less-than
      // TODO: op:dateTime-greater-than

      // TODO: op:date-equal
      // TODO: op:date-less-than
      // TODO: op:date-greater-than

      // TODO: op:time-equal
      // TODO: op:time-less-than
      // TODO: op:time-greater-than

      // TODO: op:gYearMonth-equal
      // TODO: op:gYear-equal
      // TODO: op:gMonthDay-equal
      // TODO: op:gMonth-equal
      // TODO: op:gDay-equal
    }

    @Nested
    @DisplayName("Arithmetic operators on durations, dates and times")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DateTimeArithmetic {
      // TODO: op:subtract-dateTimes
      // TODO: op:subtract-dates
      // TODO: op:subtract-times
      // TODO: op:add-yearMonthDuration-to-dateTime
      // TODO: op:add-dayTimeDuration-to-dateTime
      // TODO: op:subtract-yearMonthDuration-from-dateTime
      // TODO: op:subtract-dayTimeDuration-from-dateTime
      // TODO: op:add-yearMonthDuration-to-date
      // TODO: op:add-dayTimeDuration-to-date
      // TODO: op:subtract-yearMonthDuration-from-date
      // TODO: op:subtract-dayTimeDuration-from-date
      // TODO: op:add-dayTimeDuration-to-time
      // TODO: op:subtract-dayTimeDuration-from-time
    }
  }

  @Nested
  @DisplayName("Functions and operators related to QNames")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class QName {
    // TODO: op:QName-equal
  }

  @Nested
  @DisplayName("Operators on base64Binary and hexBinary")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class Binary {
    @Nested
    @DisplayName("Comparisons of base64Binary and hexBinary values")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class BinaryOperators {
      // TODO: op:hexBinary-equal
      // TODO: op:hexBinary-less-than
      // TODO: op:hexBinary-greater-than
      // TODO: op:base64Binary-equal
      // TODO: op:base64Binary-less-than
      // TODO: op:base64Binary-greater-than
    }
  }
}
