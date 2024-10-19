/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.array;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.integer;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.function.InvalidTypeFunctionException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

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
    IIntegerItem result = MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.NODE, newDynamicContext());
    assertEquals(expected, result);
  }

  @Test
  void testNoFocus() {
    DynamicMetapathException throwable = assertThrows(DynamicMetapathException.class,
        () -> {
          try {
            FunctionTestBase.executeFunction(
                FnStringLength.SIGNATURE_NO_ARG,
                newDynamicContext(),
                null,
                CollectionUtil.singletonList(sequence()));
          } catch (MetapathException ex) {
            throw ex.getCause();
          }
        });
    assertEquals(DynamicMetapathException.DYNAMIC_CONTEXT_ABSENT, throwable.getCode());
  }
}
