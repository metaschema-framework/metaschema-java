/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.string;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for anonymous function calls in Metapath expressions.
 * <p>
 * These tests validate the compilation and execution of anonymous functions as
 * defined in the Metaschema specification.
 */
class AnonymousFunctionCallTest {
  private static final String NS = "http://example.com/ns";

  @Test
  void test() {
    StaticContext staticContext = StaticContext.builder()
        .namespace("ex", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    dynamicContext.bindVariableValue(IEnhancedQName.of(NS, "var1"), ISequence.of(string("fn:empty")));

    String metapath = "let $function := function($str) as meta:string { fn:concat('extra ',$str) } "
        + "return $function('cool')";

    assertEquals(
        "extra cool",
        IMetapathExpression.compile(metapath, staticContext).evaluateAs(
            null,
            IMetapathExpression.ResultType.STRING,
            dynamicContext));
  }

}
