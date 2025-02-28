/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnHoursFromDateTimeTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            integer(8),
            "fn:hours-from-dateTime(meta:date-time('1999-05-31T08:20:00-05:00'))"),
        Arguments.of(
            integer(21),
            "fn:hours-from-dateTime(meta:date-time('1999-12-31T21:20:00-05:00'))"),
        Arguments.of(
            integer(2),
            "fn:hours-from-dateTime(fn:adjust-dateTime-to-timezone(meta:date-time('1999-12-31T21:20:00-05:00'), meta:day-time-duration('PT0S')))"),
        Arguments.of(
            integer(12),
            "fn:hours-from-dateTime(meta:date-time('1999-12-31T12:00:00'))"),
        Arguments.of(
            integer(0),
            "fn:hours-from-dateTime(meta:date-time('1999-12-31T24:00:00'))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IIntegerItem expected, @NonNull String metapath) {
    assertEquals(expected, IMetapathExpression.compile(metapath).evaluateAs(null, IMetapathExpression.ResultType.ITEM,
        newDynamicContext()));
  }
}
