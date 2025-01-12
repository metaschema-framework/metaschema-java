/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.base64;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.date;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dateTime;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.dayTimeDuration;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.yearMonthDuration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class CastTest
    extends ExpressionTestBase {

  static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(string("a"), "meta:string", string("a")),

        Arguments.of(base64("a12bc3"), "meta:base64", base64("a12bc3")),
        Arguments.of(string("a12bc3"), "meta:base64", base64("a12bc3")),

        Arguments.of(bool(true), "meta:boolean", bool(true)),
        Arguments.of(bool(false), "meta:boolean", bool(false)),
        Arguments.of(string("1"), "meta:boolean", bool(true)),
        Arguments.of(string("2"), "meta:boolean", bool(true)),
        Arguments.of(string("0"), "meta:boolean", bool(false)),
        Arguments.of(string("true"), "meta:boolean", bool(true)),
        Arguments.of(string("false"), "meta:boolean", bool(false)),
        Arguments.of(string("anything else"), "meta:boolean", bool(false)),
        Arguments.of(bool(true), "meta:boolean", bool(true)),
        Arguments.of(bool(false), "meta:boolean", bool(false)),
        Arguments.of(integer(1), "meta:boolean", bool(true)),
        Arguments.of(integer(2), "meta:boolean", bool(true)),
        Arguments.of(integer(0), "meta:boolean", bool(false)),

        Arguments.of(date("2024-02-15"), "meta:date", date("2024-02-15")),
        Arguments.of(date("2024-02-15Z"), "meta:date", date("2024-02-15Z")),
        Arguments.of(date("2024-02-15Z"), "meta:date", date("2024-02-15Z")),
        Arguments.of(string("2024-02-15"), "meta:date", date("2024-02-15")),
        Arguments.of(string("2024-02-15Z"), "meta:date", date("2024-02-15Z")),
        Arguments.of(string("2024-02-15Z"), "meta:date", date("2024-02-15Z")),
        Arguments.of(dateTime("2024-02-15T06:12:34"), "meta:date", date("2024-02-15")),
        Arguments.of(dateTime("2024-02-15T06:12:34Z"), "meta:date", date("2024-02-15Z")),
        Arguments.of(dateTime("2024-02-15T06:12:34Z"), "meta:date", date("2024-02-15Z")),

        Arguments.of(date("2024-02-15Z"), "meta:date-with-timezone", date("2024-02-15Z")),
        Arguments.of(string("2024-02-15Z"), "meta:date-with-timezone", date("2024-02-15Z")),
        Arguments.of(string("2024-02-15Z"), "meta:date-with-timezone", date("2024-02-15Z")),
        Arguments.of(dateTime("2024-02-15T06:12:34Z"), "meta:date-with-timezone", date("2024-02-15Z")),
        Arguments.of(dateTime("2024-02-15T06:12:34Z"), "meta:date-with-timezone", date("2024-02-15Z")),

        Arguments.of(dateTime("2024-02-15T06:12:34"), "meta:date-time", dateTime("2024-02-15T06:12:34")),
        Arguments.of(dateTime("2024-02-15T06:12:34Z"), "meta:date-time", dateTime("2024-02-15T06:12:34Z")),
        Arguments.of(string("2024-02-15T00:00:00"), "meta:date-time", dateTime("2024-02-15T00:00:00")),
        Arguments.of(string("2024-02-15T00:00:00Z"), "meta:date-time", dateTime("2024-02-15T00:00:00Z")),
        Arguments.of(date("2024-02-15"), "meta:date-time", dateTime("2024-02-15T00:00:00")),
        Arguments.of(date("2024-02-15Z"), "meta:date-time", dateTime("2024-02-15T00:00:00Z")),
        Arguments.of(date("2024-02-15Z"), "meta:date-time", dateTime("2024-02-15T00:00:00Z")),

        Arguments.of(
            dateTime("2024-02-15T06:12:34Z"),
            "meta:date-time-with-timezone",
            dateTime("2024-02-15T06:12:34Z")),
        Arguments.of(string("2024-02-15T00:00:00Z"), "meta:date-time-with-timezone", dateTime("2024-02-15T00:00:00Z")),
        Arguments.of(string("2024-02-15T00:00:00Z"), "meta:date-time-with-timezone", dateTime("2024-02-15T00:00:00Z")),
        Arguments.of(date("2024-02-15Z"), "meta:date-time-with-timezone", dateTime("2024-02-15T00:00:00Z")),
        Arguments.of(date("2024-02-15Z"), "meta:date-time-with-timezone", dateTime("2024-02-15T00:00:00Z")),

        Arguments.of(dayTimeDuration("P2DT3H4M3.45S"), "meta:day-time-duration", dayTimeDuration("P2DT3H4M3.45S")),
        Arguments.of(string("P2DT3H4M3.45S"), "meta:day-time-duration", dayTimeDuration("P2DT3H4M3.45S")),

        Arguments.of(dayTimeDuration("P2DT3H4M3.45S"), "meta:day-time-duration", dayTimeDuration("P2DT3H4M3.45S")),
        Arguments.of(string("P2DT3H4M3.45S"), "meta:day-time-duration", dayTimeDuration("P2DT3H4M3.45S")),

        Arguments.of(string("1"), "meta:integer", integer(1)),

        Arguments.of(yearMonthDuration("P1Y2M"), "meta:year-month-duration", yearMonthDuration("P1Y2M")),
        Arguments.of(string("P1Y2M"), "meta:year-month-duration", yearMonthDuration("P1Y2M")),

        Arguments.of(null, "meta:string?", null));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testCast(@NonNull IAnyAtomicItem actual, @NonNull String singleType, @NonNull IAnyAtomicItem expected) {
    IMetapathExpression metapath = IMetapathExpression.compile(". cast as " + singleType);
    IItem result = metapath.evaluateAs(actual, IMetapathExpression.ResultType.ITEM);

    assertEquals(
        expected,
        result,
        String.format("Expected `%s` to cast to '%s'", singleType, expected));
  }

  @Test
  void testInvalidTypePrefix() {
    StaticMetapathException ex = assertThrows(StaticMetapathException.class, () -> {
      IMetapathExpression.compile("'a' cast as foo:bar");
    });
    assertEquals(StaticMetapathException.PREFIX_NOT_EXPANDABLE, ex.getCode());
  }

  @Test
  void testInvalidType() {
    StaticMetapathException ex = assertThrows(StaticMetapathException.class, () -> {
      IMetapathExpression.compile("'a' cast as meta:bar");
    });
    assertEquals(StaticMetapathException.CAST_UNKNOWN_TYPE, ex.getCode());
  }

  @Test
  void testAnyAtomicType() {
    StaticMetapathException ex = assertThrows(StaticMetapathException.class, () -> {
      IMetapathExpression.compile("'a' cast as meta:any-atomic-type");
    });
    assertEquals(StaticMetapathException.CAST_ANY_ATOMIC, ex.getCode());
  }
}
