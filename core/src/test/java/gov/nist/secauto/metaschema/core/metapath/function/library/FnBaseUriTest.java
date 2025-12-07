/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.testing.model.mocking.MockedDocumentGenerator;

import org.junit.jupiter.api.Test;

/**
 * Tests for the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-base-uri">fn:base-uri</a>
 * function.
 * <p>
 * Per XPath 3.1 spec:
 * <ul>
 * <li>Zero-argument form uses context item as implicit argument</li>
 * <li>If context item is absent: raises err:XPDY0002</li>
 * <li>If context item is not a node: raises err:XPTY0004</li>
 * <li>Returns base URI of the node or empty sequence if no base URI</li>
 * </ul>
 */
class FnBaseUriTest
    extends ExpressionTestBase {

  @Test
  void testWithNodeFocus() {
    DynamicContext dynamicContext = newDynamicContext();

    IAnyUriItem result = IMetapathExpression.compile("base-uri()", dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    // The mocked document should have a base URI
    assertNotNull(result);
  }

  @Test
  void testWithOneArg() {
    DynamicContext dynamicContext = newDynamicContext();

    IAnyUriItem result = IMetapathExpression.compile("base-uri(/root)", dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    assertNotNull(result);
  }

  /**
   * Per XPath 3.1 spec, if the zero-argument form is called and the context item
   * is not a node, a type error (err:XPTY0004) is raised.
   */
  @Test
  void testNotANodeThrowsTypeError() {
    DynamicContext dynamicContext = newDynamicContext();

    assertThrows(InvalidTypeMetapathException.class, () -> {
      IMetapathExpression.compile("base-uri()", dynamicContext.getStaticContext())
          .evaluateAs(IStringItem.valueOf("test"), IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }
}
