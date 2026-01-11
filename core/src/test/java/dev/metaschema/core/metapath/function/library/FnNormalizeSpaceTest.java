/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static dev.metaschema.core.metapath.TestUtils.sequence;
import static dev.metaschema.core.metapath.TestUtils.string;
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
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.util.CollectionUtil;
import edu.umd.cs.findbugs.annotations.NonNull;

class FnNormalizeSpaceTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            string("The wealthy curled darlings of our nation."),
            "fn:normalize-space(\" The    wealthy curled darlings \n\r       \t" +
                "                                 of    our    nation. \")"));
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
              FnNormalizeSpace.SIGNATURE_NO_ARG,
              newDynamicContext(),
              null,
              CollectionUtil.singletonList(sequence()));
        });
  }
}
