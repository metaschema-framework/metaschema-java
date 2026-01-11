/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static dev.metaschema.core.metapath.TestUtils.decimal;
import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.string;
import static dev.metaschema.core.metapath.TestUtils.yearMonthDuration;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import dev.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import dev.metaschema.core.metapath.item.atomic.IDayTimeDurationItem;
import dev.metaschema.core.metapath.item.atomic.IYearMonthDurationItem;
import dev.metaschema.core.util.CollectionUtil;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnAvgTest
    extends FunctionTestBase {

  private static Stream<Arguments> provideValuesForAvg() {
    IYearMonthDurationItem yearMonth1 = yearMonthDuration("P20Y");
    IYearMonthDurationItem yearMonth2 = yearMonthDuration("P10M");
    IDayTimeDurationItem dayTime1 = dayTimeDuration("P1DT12H");
    IDayTimeDurationItem dayTime2 = dayTimeDuration("P2D");

    return Stream.of(
        Arguments.of(decimal("4"), new IAnyAtomicItem[] { integer(3), integer(4), integer(5) }),
        Arguments.of(null, new IAnyAtomicItem[] { integer(3), integer(4), string("test") }),
        Arguments.of(dayTimeDuration("P1DT18H"), new IAnyAtomicItem[] { dayTime1, dayTime2 }),
        Arguments.of(null, new IAnyAtomicItem[] { dayTime1, dayTime2, integer(1) }),
        Arguments.of(yearMonthDuration("P10Y5M"), new IAnyAtomicItem[] { yearMonth1, yearMonth2 }),
        Arguments.of(null, new IAnyAtomicItem[] { yearMonth1, yearMonth2, integer(1) }));
  }

  @ParameterizedTest
  @MethodSource("provideValuesForAvg")
  void testAvg(@Nullable IAnyAtomicItem expected, @NonNull IAnyAtomicItem... values) {
    List<ISequence<?>> arguments = CollectionUtil.singletonList(ISequence.of(values));
    if (expected == null) {
      assertThrows(InvalidArgumentFunctionException.class, () -> {
        FunctionTestBase.executeFunction(
            FnAvg.SIGNATURE,
            newDynamicContext(),
            null,
            arguments);
      });
    } else {
      assertFunctionResult(
          FnAvg.SIGNATURE,
          ISequence.of(expected),
          arguments);
    }
  }

  @Test
  void testAvgNoOp() {
    assertFunctionResult(
        FnAvg.SIGNATURE,
        ISequence.empty(),
        CollectionUtil.singletonList(ISequence.empty()));
  }
}
