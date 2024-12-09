/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for anonymous function calls in Metapath expressions.
 * <p>
 * These tests validate the compilation and execution of anonymous functions as
 * defined in the Metaschema specification.
 */
class AnonymousFunctionCallTest {
  private static final String NS = "http://example.com/ns";

  /**
   * Tests the basic functionality of anonymous function definition and execution.
   * This test validates:
   * <ul>
   * <li>Function definition using the 'let' syntax</li>
   * <li>Function execution with string parameters</li>
   * <li>String concatenation within the function body</li>
   * </ul>
   */
  @Test
  void test() {
    StaticContext staticContext = StaticContext.builder()
        .namespace("ex", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);

    String metapath = "let $function := function($str) as meta:string { fn:concat('extra ',$str) } "
        + "return $function('cool')";

    assertEquals(
        "extra cool",
        IMetapathExpression.compile(metapath, staticContext).evaluateAs(
            null,
            IMetapathExpression.ResultType.STRING,
            dynamicContext));
  }

  @Test
  void testMultipleParameters() {
    // FIXME: Add test for function with multiple parameters
  }

  @Test
  void testDifferentReturnTypes() {
    // FIXME: Add test for functions returning different types
  }

  @Test
  void testErrorCases() {
    // FIXME: Add test for invalid function definitions
  }
}