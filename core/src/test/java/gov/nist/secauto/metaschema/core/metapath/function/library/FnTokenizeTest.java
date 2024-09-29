/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.function.regex.RegularExpressionMetapathException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnTokenizeTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            sequence(string("red"), string("green"), string("blue")),
            "tokenize(\" red green blue \")"),
        Arguments.of(
            sequence(string("The"), string("cat"), string("sat"), string("on"), string("the"), string("mat")),
            "tokenize(\"The cat sat on the mat\", \"\\s+\")"),
        Arguments.of(
            sequence(string(""), string("red"), string("green"), string("blue"), string("")),
            "tokenize(\" red green blue \", \"\\s+\")"),
        Arguments.of(
            sequence(string("1"), string("15"), string("24"), string("50")),
            "tokenize(\"1, 15, 24, 50\", \",\\s*\")"),
        Arguments.of(
            sequence(string("1"), string("15"), string(""), string("24"), string("50"), string("")),
            "tokenize(\"1,15,,24,50,\", \",\")"),
        Arguments.of(
            sequence(string("Some unparsed"), string("HTML"), string("text")),
            "tokenize(\"Some unparsed <br> HTML <BR> text\", \"\\s*<br>\\s*\", \"i\")"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull ISequence<?> expected, @NonNull String metapath) {
    assertEquals(expected, MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.SEQUENCE,
            newDynamicContext()));
  }

  @Test
  void testMatchZeroLengthString() {
    RegularExpressionMetapathException throwable = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          try {
            FunctionTestBase.executeFunction(
                FnTokenize.SIGNATURE_TWO_ARG,
                newDynamicContext(),
                ISequence.empty(),
                List.of(sequence(string("abba")), sequence(string(".?"))));
          } catch (MetapathException ex) {
            Throwable cause = ex.getCause();
            if (cause != null) {
              throw cause;
            }
            throw ex;
          }
        });
    assertEquals(RegularExpressionMetapathException.MATCHES_ZERO_LENGTH_STRING, throwable.getCode());
  }

  @Test
  void testInvalidPattern() {
    RegularExpressionMetapathException throwable = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          try {
            FunctionTestBase.executeFunction(
                FnTokenize.SIGNATURE_TWO_ARG,
                newDynamicContext(),
                ISequence.empty(),
                List.of(sequence(string("input")), sequence(string("pattern["))));
          } catch (MetapathException ex) {
            Throwable cause = ex.getCause();
            if (cause != null) {
              throw cause;
            }
            throw ex;
          }
        });
    assertEquals(RegularExpressionMetapathException.INVALID_EXPRESSION, throwable.getCode());
  }

  @Test
  void testInvalidFlag() {
    RegularExpressionMetapathException throwable = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          try {
            FunctionTestBase.executeFunction(
                FnTokenize.SIGNATURE_THREE_ARG,
                newDynamicContext(),
                ISequence.empty(),
                List.of(sequence(string("input")), sequence(string("pattern")), sequence(string("dsm"))));
          } catch (MetapathException ex) {
            Throwable cause = ex.getCause();
            if (cause != null) {
              throw cause;
            }
            throw ex;
          }
        });
    assertEquals(RegularExpressionMetapathException.INVALID_FLAG, throwable.getCode());
  }
}
