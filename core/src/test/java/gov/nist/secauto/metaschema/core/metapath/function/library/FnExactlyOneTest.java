/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnExactlyOneTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            null,
            "exactly-one((10, 20, 30))"),
        Arguments.of(
            null,
            "exactly-one((10, 20))"),
        Arguments.of(
            sequence(integer(10)),
            "exactly-one((10))"),
        Arguments.of(
            null,
            "exactly-one(())"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable ISequence<?> expected, @NonNull String metapath) {
    if (expected == null) {
      InvalidArgumentFunctionException thrown = assertThrows(InvalidArgumentFunctionException.class, () -> {
        IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext());
      });
      assertThat(thrown).extracting(ex -> ex.getErrorCode().getCode())
          .isEqualTo(InvalidArgumentFunctionException.INVALID_ARGUMENT_EXACTLY_ONE);
    } else {
      assertEquals(expected, IMetapathExpression.compile(metapath)
          .evaluate(null, newDynamicContext()));
    }
  }
}
