/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnOneOrMoreTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(integer(10), integer(20), integer(30)),
            "one-or-more((10, 20, 30))"),
        Arguments.of(
            sequence(integer(10), integer(20)),
            "one-or-more((10, 20))"),
        Arguments.of(
            sequence(integer(10)),
            "one-or-more((10))"),
        Arguments.of(
            null,
            "one-or-more(())"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable ISequence<?> expected, @NonNull String metapath) {
    try {
      assertEquals(expected, IMetapathExpression.compile(metapath)
          .evaluate(null, newDynamicContext()));
    } catch (MetapathException ex) {
      // FIXME: After refactoring the exception hierarchy, target the actual exception
      Throwable cause = ex.getCause() == null ? ex.getCause() : ex.getCause().getCause();
      assertAll(
          () -> assertNull(expected),
          () -> assertEquals(InvalidArgumentFunctionException.class, cause == null ? null : cause.getClass()),
          () -> assertEquals(
              InvalidArgumentFunctionException.INVALID_ARGUMENT_ONE_OR_MORE,
              cause instanceof InvalidArgumentFunctionException
                  ? ((InvalidArgumentFunctionException) cause).getCode()
                  : null));
    }
  }
}
