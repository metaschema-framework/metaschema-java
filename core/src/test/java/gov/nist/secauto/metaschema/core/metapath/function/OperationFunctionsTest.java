/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.decimal;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.function.library.FunctionTestBase;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class OperationFunctionsTest
    extends FunctionTestBase {

  private static Stream<Arguments> provideValuesOpNumericMod() {
    return Stream.of(
        Arguments.of(decimal(1), integer(10), integer(3)),
        Arguments.of(decimal(0), integer(6), integer(-2)),
        Arguments.of(decimal(0.9), decimal(4.5), decimal(1.2)),
        Arguments.of(decimal(3.0E0), decimal(1.23E2), decimal(0.6E1)));
  }

  @ParameterizedTest
  @MethodSource("provideValuesOpNumericMod")
  void testOpNumericMod(@Nullable INumericItem expected, @NonNull INumericItem dividend,
      @NonNull INumericItem divisor) {
    assertEquals(expected, OperationFunctions.opNumericMod(dividend, divisor));
  }
}
