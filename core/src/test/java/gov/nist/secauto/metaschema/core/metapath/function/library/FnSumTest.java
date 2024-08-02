/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.yearMonthDuration;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnSumTest
    extends FunctionTestBase {

  private static Stream<Arguments> provideValuesOneArg() {
    IYearMonthDurationItem yearMonth1 = yearMonthDuration("P20Y");
    IYearMonthDurationItem yearMonth2 = yearMonthDuration("P10M");
    IDayTimeDurationItem dayTime1 = dayTimeDuration("P1DT12H");
    IDayTimeDurationItem dayTime2 = dayTimeDuration("P2D");

    return Stream.of(
        Arguments.of(integer(0), new IAnyAtomicItem[] {}),
        Arguments.of(integer(12), new IAnyAtomicItem[] { integer(3), integer(4), integer(5) }),
        Arguments.of(null, new IAnyAtomicItem[] { integer(3), integer(4), string("test") }),
        Arguments.of(dayTimeDuration("P3DT12H"), new IAnyAtomicItem[] { dayTime1, dayTime2 }),
        Arguments.of(null, new IAnyAtomicItem[] { dayTime1, dayTime2, integer(1) }),
        Arguments.of(yearMonthDuration("P20Y10M"), new IAnyAtomicItem[] { yearMonth1, yearMonth2 }),
        Arguments.of(null, new IAnyAtomicItem[] { yearMonth1, yearMonth2, integer(1) }));
  }

  @ParameterizedTest
  @MethodSource("provideValuesOneArg")
  void testAvg(@Nullable IAnyAtomicItem expected, @NonNull IAnyAtomicItem... values) {
    try {
      assertFunctionResult(
          FnSum.SIGNATURE_ONE_ARG,
          ISequence.of(expected),
          List.of(ISequence.of(values)));
    } catch (MetapathException ex) {
      if (expected == null) {
        assertAll(
            () -> assertInstanceOf(InvalidArgumentFunctionException.class, ex.getCause()));
      } else {
        throw ex;
      }
    }
  }

  private static Stream<Arguments> provideValuesTwoArg() {
    IYearMonthDurationItem yearMonth1 = yearMonthDuration("P20Y");
    IYearMonthDurationItem yearMonth2 = yearMonthDuration("P10M");

    return Stream.of(

        Arguments.of(integer(0), integer(0), new IAnyAtomicItem[] {}),
        Arguments.of(
            yearMonthDuration("P20Y10M"),
            string("ein Augenblick"),
            new IAnyAtomicItem[] { yearMonth1, yearMonth2 }));
  }

  @ParameterizedTest
  @MethodSource("provideValuesTwoArg")
  void testAvgWithZero(@Nullable IAnyAtomicItem expected, @Nullable IAnyAtomicItem zero,
      @NonNull IAnyAtomicItem... values) {
    try {
      assertFunctionResult(
          FnSum.SIGNATURE_TWO_ARG,
          ISequence.of(expected),
          List.of(ISequence.of(values), ISequence.of(zero)));
    } catch (MetapathException ex) {
      if (expected == null) {
        assertAll(
            () -> assertInstanceOf(InvalidArgumentFunctionException.class, ex.getCause()));
      } else {
        throw ex;
      }
    }
  }

  @Test
  void testAvgNoOpTwoArg() {
    assertFunctionResult(
        FnSum.SIGNATURE_TWO_ARG,
        ISequence.empty(),
        List.of(ISequence.empty(), ISequence.empty()));
  }
}
