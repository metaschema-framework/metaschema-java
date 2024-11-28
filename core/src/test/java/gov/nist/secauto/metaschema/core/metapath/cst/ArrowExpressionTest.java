/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.AbstractCodedMetapathException;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class ArrowExpressionTest
    extends ExpressionTestBase {
  private static final String NS = "http://example.com/ns";

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(ISequence.of(string("ABC")), "'abc' => upper-case()"),
        Arguments.of(ISequence.of(string("123")), "'1' => concat('2') => concat('3')"),
        Arguments.of(ISequence.of(bool(true)), "() => $ex:var1()"));
  }

  /**
   * Tests the casting functionality using various input strings and target types.
   * <p>
   * The dynamic context is created fresh for each test case to ensure isolation.
   *
   * @param text
   *          The input string to cast
   * @param type
   *          The target type to cast to
   * @param expected
   *          The expected result after casting
   */
  @ParameterizedTest
  @MethodSource("provideValues")
  void testArrowExpression(@NonNull ISequence<?> expected, @NonNull String metapath) {
    StaticContext staticContext = StaticContext.builder()
        .namespace("ex", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    dynamicContext.bindVariableValue(IEnhancedQName.of(NS, "var1"), ISequence.of(string("fn:empty")));

    assertEquals(
        expected,
        IMetapathExpression.compile(metapath, staticContext).evaluate(null, dynamicContext));
  }

  @Test
  void testArrowExpressionWithUndefinedVariable() {
    StaticContext staticContext = StaticContext.builder()
        .namespace("ex", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);

    MetapathException ex = assertThrows(
        MetapathException.class,
        () -> IMetapathExpression.compile("() => $ex:undefined()", staticContext)
            .evaluate(null, dynamicContext));
    assertEquals(StaticMetapathException.NOT_DEFINED,
        ObjectUtils.requireNonNull((AbstractCodedMetapathException) ex.getCause()).getCode());
  }
}
