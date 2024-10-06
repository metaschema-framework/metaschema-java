/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.sequence;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.function.regex.RegularExpressionMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnMatchesTest
    extends ExpressionTestBase {
  private static final String POEM = "Kaum hat dies der Hahn gesehen,\n"
      + "Fängt er auch schon an zu krähen:\n"
      + "Kikeriki! Kikikerikih!!\n"
      + "Tak, tak, tak! - da kommen sie.";

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            bool(true),
            "matches(\"abracadabra\", \"bra\")"),
        Arguments.of(
            bool(true),
            "matches(\"abracadabra\", \"^a.*a$\")"),
        Arguments.of(
            bool(false),
            "matches(\"abracadabra\", \"^bra\")"),
        Arguments.of(
            bool(false),
            "matches($poem, \"Kaum.*krähen\")"),
        Arguments.of(
            bool(true),
            "matches($poem, \"Kaum.*krähen\", \"s\")"),
        Arguments.of(
            bool(true),
            "matches($poem, \"^Kaum.*gesehen,$\", \"m\")"),
        Arguments.of(
            bool(false),
            "matches($poem, \"^Kaum.*gesehen,$\")"),
        Arguments.of(
            bool(true),
            "matches($poem, \"kiki\", \"i\")"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull IBooleanItem expected, @NonNull String metapath) {
    assertEquals(expected, MetapathExpression.compile(metapath)
        .evaluateAs(null, MetapathExpression.ResultType.ITEM,
            newDynamicContext()));
  }

  /**
   * Construct a new dynamic context for testing.
   *
   * @return the dynamic context
   */
  @NonNull
  protected static DynamicContext newDynamicContext() {
    DynamicContext retval = ExpressionTestBase.newDynamicContext();

    retval.bindVariableValue(new QName("poem"), ISequence.of(IStringItem.valueOf(POEM)));

    return retval;
  }

  @Test
  void testInvalidPattern() {
    RegularExpressionMetapathException throwable = assertThrows(RegularExpressionMetapathException.class,
        () -> {
          try {
            FunctionTestBase.executeFunction(
                FnMatches.SIGNATURE_TWO_ARG,
                newDynamicContext(),
                ISequence.empty(),
                ObjectUtils.notNull(List.of(sequence(string("input")), sequence(string("pattern[")))));
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
                FnMatches.SIGNATURE_THREE_ARG,
                newDynamicContext(),
                ISequence.empty(),
                ObjectUtils.notNull(List.of(
                    sequence(string("input")),
                    sequence(string("pattern")),
                    sequence(string("dsm")))));
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
