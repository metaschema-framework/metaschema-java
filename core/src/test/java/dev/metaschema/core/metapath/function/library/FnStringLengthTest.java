/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.integer;
import static dev.metaschema.core.metapath.TestUtils.sequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IIntegerItem;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.util.CollectionUtil;
import edu.umd.cs.findbugs.annotations.NonNull;

class FnStringLengthTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            integer(45),
            "string-length('Harp not on that string, madam; that is past.')"),
        Arguments.of(
            integer(0),
            "string-length(())"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testExpression(@NonNull IIntegerItem expected, @NonNull String metapath) {
    IIntegerItem result = IMetapathExpression.compile(metapath)
        .evaluateAs(null, IMetapathExpression.ResultType.ITEM, newDynamicContext());
    assertEquals(expected, result);
  }

  @Test
  void testFocusStringTest() {
    assertEquals(
        ISequence.of(integer(6)),
        FunctionTestBase.executeFunction(
            FnStringLength.SIGNATURE_NO_ARG,
            newDynamicContext(),
            ISequence.of(IStringItem.valueOf("000001")),
            CollectionUtil.emptyList()));
  }

  @Test
  void testNoFocus() {
    assertThrows(ContextAbsentDynamicMetapathException.class,
        () -> {
          FunctionTestBase.executeFunction(
              FnStringLength.SIGNATURE_NO_ARG,
              newDynamicContext(),
              null,
              CollectionUtil.singletonList(sequence()));
        });
  }
}
