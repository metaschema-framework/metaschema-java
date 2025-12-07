/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.testing.model.mocking.MockedDocumentGenerator;

import org.junit.jupiter.api.Test;

/**
 * Tests for the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-functions-31/#func-data">fn:data</a>
 * function.
 * <p>
 * Per XPath 3.1 spec, fn:data() atomizes its argument:
 * <ul>
 * <li>Atomic values pass through unchanged</li>
 * <li>Nodes are replaced with their typed values</li>
 * <li>Arrays are flattened</li>
 * <li>The function does NOT throw for non-node items</li>
 * </ul>
 */
class FnDataTest
    extends ExpressionTestBase {

  @Test
  void testWithFieldNodeFocus() {
    DynamicContext dynamicContext = newDynamicContext();

    // When focus is a field node with a value, fn:data returns its typed value
    // Use a field node which has a typed value, not a document node
    IAnyAtomicItem result = IMetapathExpression.compile("data(/root/field)", dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    // Field nodes have typed values
    assertNotNull(result);
  }

  /**
   * Per XPath 3.1 spec, fn:data() atomizes its argument. For atomic values, they
   * pass through unchanged. This is different from fn:base-uri, fn:path, etc.
   * which require a node.
   */
  @Test
  void testWithAtomicValueFocus() {
    DynamicContext dynamicContext = newDynamicContext();

    // When focus is an atomic value (string), fn:data returns it unchanged
    IStringItem result = IMetapathExpression.compile("data()", dynamicContext.getStaticContext())
        .evaluateAs(IStringItem.valueOf("test"), IMetapathExpression.ResultType.ITEM, dynamicContext);
    assertNotNull(result);
    assertEquals("test", result.asString());
  }

  @Test
  void testWithIntegerFocus() {
    DynamicContext dynamicContext = newDynamicContext();

    // When focus is an integer, fn:data returns it unchanged
    IIntegerItem result = IMetapathExpression.compile("data()", dynamicContext.getStaticContext())
        .evaluateAs(IIntegerItem.valueOf(42), IMetapathExpression.ResultType.ITEM, dynamicContext);
    assertNotNull(result);
    assertEquals(42, result.asInteger().intValue());
  }

  @Test
  void testWithOneArgNode() {
    DynamicContext dynamicContext = newDynamicContext();

    // Use field node which has a typed value (not assembly which has no typed
    // value)
    ISequence<IAnyAtomicItem> result
        = IMetapathExpression.compile("data(/root/field)", dynamicContext.getStaticContext())
            .evaluate(
                MockedDocumentGenerator.generateDocumentNodeItem(),
                dynamicContext);
    assertNotNull(result);
  }

  @Test
  void testWithOneArgSequence() {
    DynamicContext dynamicContext = newDynamicContext();

    // Test atomizing a sequence of atomic values
    ISequence<IAnyAtomicItem> result = IMetapathExpression.compile("data((1, 2, 3))", dynamicContext.getStaticContext())
        .evaluate(null, dynamicContext);
    assertNotNull(result);
    assertEquals(3, result.size());
  }
}
