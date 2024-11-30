/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathError;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class CastableTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return CastTest.provideValues()
        .map(args -> {
          Object[] values = args.get();
          return Arguments.of(values[0], values[1]);
        });
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void testCastable(@NonNull IAnyAtomicItem actual, @NonNull String singleType) {
    IMetapathExpression metapath = IMetapathExpression.compile(". castable as " + singleType);
    boolean result = ObjectUtils.notNull(metapath.evaluateAs(actual, IMetapathExpression.ResultType.BOOLEAN));

    assertTrue(
        result,
        String.format("Expected `%s` to be castable.", singleType));
  }

  @Test
  void testInvalidTypePrefix() {
    StaticMetapathError ex = assertThrows(StaticMetapathError.class, () -> {
      IMetapathExpression.compile("'a' castable as foo:bar");
    });
    assertEquals(StaticMetapathError.PREFIX_NOT_EXPANDABLE, ex.getErrorCode().getCode());
  }

  @Test
  void testInvalidType() {
    StaticMetapathError ex = assertThrows(StaticMetapathError.class, () -> {
      IMetapathExpression.compile("'a' castable as meta:bar");
    });
    assertEquals(StaticMetapathError.CAST_UNKNOWN_TYPE, ex.getErrorCode().getCode());
  }

  @Test
  void testAnyAtomicType() {
    StaticMetapathError ex = assertThrows(StaticMetapathError.class, () -> {
      IMetapathExpression.compile("'a' castable as meta:any-atomic-type");
    });
    assertEquals(StaticMetapathError.CAST_ANY_ATOMIC, ex.getErrorCode().getCode());
  }
}
