/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.InvalidMetapathGrammarException;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
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
    InvalidMetapathGrammarException thrown = assertThrows(InvalidMetapathGrammarException.class, () -> {
      IMetapathExpression.compile("'a' castable as foo:bar");
    });
    assertThat(thrown)
        .cause()
        .isExactlyInstanceOf(StaticMetapathException.class)
        .extracting(
            ex -> ex instanceof StaticMetapathException
                ? ((StaticMetapathException) ex).getErrorCode().getCode()
                : null)
        .isEqualTo(StaticMetapathException.PREFIX_NOT_EXPANDABLE);
  }

  @Test
  void testInvalidType() {
    InvalidMetapathGrammarException thrown = assertThrows(InvalidMetapathGrammarException.class, () -> {
      IMetapathExpression.compile("'a' castable as meta:bar");
    });
    assertThat(thrown)
        .cause()
        .isExactlyInstanceOf(StaticMetapathException.class)
        .extracting(
            ex -> ex instanceof StaticMetapathException
                ? ((StaticMetapathException) ex).getErrorCode().getCode()
                : null)
        .isEqualTo(StaticMetapathException.CAST_UNKNOWN_TYPE);
  }

  @Test
  void testAnyAtomicType() {
    InvalidMetapathGrammarException thrown = assertThrows(InvalidMetapathGrammarException.class, () -> {
      IMetapathExpression.compile("'a' castable as meta:any-atomic-type");
    });
    assertThat(thrown)
        .cause()
        .isExactlyInstanceOf(StaticMetapathException.class)
        .extracting(
            ex -> ex instanceof StaticMetapathException
                ? ((StaticMetapathException) ex).getErrorCode().getCode()
                : null)
        .isEqualTo(StaticMetapathException.CAST_ANY_ATOMIC);
  }
}
