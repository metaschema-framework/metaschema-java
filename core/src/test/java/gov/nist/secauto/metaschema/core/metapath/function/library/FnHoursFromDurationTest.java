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

class FnHoursFromDurationTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            integer(10),
            "fn:hours-from-duration(meta:day-time-duration('P3DT10H'))"),
        Arguments.of(
            integer(12),
            "fn:hours-from-duration(meta:day-time-duration('P3DT12H32M12S'))"),
        Arguments.of(
            integer(3),
            "fn:hours-from-duration(meta:day-time-duration('PT123H'))"),
        Arguments.of(
            integer(-10),
            "fn:hours-from-duration(meta:day-time-duration('-P3DT10H'))"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IIntegerItem expected, @NonNull String metapath) {
    assertEquals(expected, IMetapathExpression.compile(metapath).evaluateAs(null, IMetapathExpression.ResultType.ITEM,
        newDynamicContext()));
  }
}
