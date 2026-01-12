/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.sequence;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.function.InvalidArgumentFunctionException;
import dev.metaschema.core.metapath.item.ISequence;
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
    if (expected == null) {
      InvalidArgumentFunctionException thrown = assertThrows(InvalidArgumentFunctionException.class, () -> {
        IMetapathExpression.compile(metapath).evaluate(null, newDynamicContext());
      });
      assertThat(thrown).extracting(ex -> ex.getErrorCode().getCode())
          .isEqualTo(InvalidArgumentFunctionException.INVALID_ARGUMENT_ONE_OR_MORE);
    } else {
      assertEquals(expected, IMetapathExpression.compile(metapath)
          .evaluate(null, newDynamicContext()));
    }
  }
}
