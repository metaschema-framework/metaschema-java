/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.array;
import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.sequence;
import static dev.metaschema.core.metapath.TestUtils.string;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.function.InvalidTypeFunctionException;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;
import dev.metaschema.core.util.CollectionUtil;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

class FnStringTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            string("23"),
            "string(23)"),
        Arguments.of(
            string("false"),
            "string(false())"),
        Arguments.of(
            string("Paris"),
            "string(\"Paris\")"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IStringItem expected, @NonNull String metapath) {
    IStringItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }

  @Test
  void testNoFocus() {
    assertThrows(ContextAbsentDynamicMetapathException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnString.SIGNATURE_NO_ARG,
              newDynamicContext(),
              null,
              CollectionUtil.singletonList(sequence()));
        });
  }

  @Test
  void testInvalidArgument() {
    assertThrows(InvalidTypeMetapathException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnString.SIGNATURE_ONE_ARG,
              newDynamicContext(),
              ISequence.empty(),
              ObjectUtils.notNull(List.of(sequence(integer(1), integer(2)))));
        });
  }

  @Test
  void testInvalidArgumentType() {
    InvalidTypeFunctionException throwable = assertThrows(InvalidTypeFunctionException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnString.SIGNATURE_ONE_ARG,
              newDynamicContext(),
              ISequence.empty(),
              ObjectUtils.notNull(List.of(
                  sequence(
                      array(
                          array(integer(1), integer(2)),
                          array(integer(3), integer(4)))))));
        });
    assertThat(throwable)
        .extracting(ex -> ex.getErrorCode().getCode())
        .isEqualTo(InvalidTypeFunctionException.ARGUMENT_TO_STRING_IS_FUNCTION);
  }
}
