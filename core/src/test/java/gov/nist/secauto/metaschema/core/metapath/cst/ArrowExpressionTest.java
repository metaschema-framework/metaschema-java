/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.bool;
import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.MetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

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
        // Arguments.of(ISequence.of(string("true")), "true() => string()"),
        Arguments.of(ISequence.of(string("ABC")), "'abc' => upper-case()"),
        Arguments.of(ISequence.of(string("123")), "'1' => concat('2') => concat('3')"),
        Arguments.of(ISequence.of(bool(true)), "() => $ex:var1()"));
  }

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
        MetapathExpression.compile(metapath, staticContext)
            .evaluateAs(
                null,
                MetapathExpression.ResultType.SEQUENCE,
                dynamicContext));
  }
}
