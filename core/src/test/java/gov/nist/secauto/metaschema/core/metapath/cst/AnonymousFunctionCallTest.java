/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.qname;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static gov.nist.secauto.metaschema.core.metapath.TestUtils.qname;

import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression.ResultType;
import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression.ResultType;

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
    StaticContext staticContext = StaticContext.builder()
        .namespace("ex", NS)
        .build();
    DynamicContext dynamicContext = new DynamicContext(staticContext);
    String metapath = "function ($argument1 as meta:string, $argument2 as meta:string) as meta:string { $argument2 }";
    dynamicContext.bindVariableValue(qname(NS, "boom"),
        IMetapathExpression.compile(metapath, staticContext).evaluate(null, dynamicContext));
    String result = IMetapathExpression.compile("$ex:boom('a', 'b')", staticContext).evaluateAs(null, ResultType.STRING,
        dynamicContext);
    assertEquals(result, "b");
  }

  @Test
  void testDifferentReturnTypes() {
    // FIXME: Add test for functions returning different types
  }

  @Test
  void testErrorCases() {
    // FIXME: Add test for invalid function definitions
  }
  
  @Test
  void testParameterConversionFlagNodeArgument() {
	  StaticContext staticContext = StaticContext.builder()
			  .namespace("ex", NS)
			  .defaultModelNamespace(NS)
			  .build();
	  DynamicContext dynamicContext = new DynamicContext(staticContext);
	  INodeItem document = MockedDocumentGenerator.generateDocumentNodeItem();
	  INodeItem rootFlagValue = IMetapathExpression.compile("/root/@root-flag", dynamicContext.getStaticContext()).evaluateAs(document, ResultType.ITEM, dynamicContext);
	  dynamicContext.bindVariableValue(qname(NS, "flag-value"), IMetapathExpression.compile("/root/field/@field-flag", dynamicContext.getStaticContext()).evaluate(document, dynamicContext));
	  dynamicContext.bindVariableValue(qname(NS, "should-dereference-param-flag-value"), IMetapathExpression.compile("function($arg as meta:string) as meta:string { $arg }", dynamicContext.getStaticContext()).evaluate(document, dynamicContext));
	  String result = IMetapathExpression.compile("$should-dereference-param-flag-value(/root/field/@field-flag)", dynamicContext.getStaticContext()).evaluateAs(document, ResultType.STRING, dynamicContext);
	  assertEquals(result, "field-flag");
  }
}
