/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.testsupport.mocking.MockedDocumentGenerator;

import org.junit.jupiter.api.Test;

/**
 * Tests for the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#func-document-uri">fn:document-uri</a>
 * function.
 * <p>
 * Per XPath 3.1 spec:
 * <ul>
 * <li>Zero-argument form uses context item as implicit argument</li>
 * <li>If context item is absent: raises err:XPDY0002</li>
 * <li>If context item is not a node: raises err:XPTY0004</li>
 * <li>If node is not a document node: returns empty sequence (NOT an
 * error)</li>
 * <li>Returns document URI if available, empty sequence otherwise</li>
 * </ul>
 */
class FnDocumentUriTest
    extends ExpressionTestBase {

  @Test
  void testWithDocumentNodeFocus() {
    DynamicContext dynamicContext = newDynamicContext();

    IAnyUriItem result = IMetapathExpression.compile("document-uri()", dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    // The mocked document should have a document URI matching the expected value
    assertNotNull(result);
    assertEquals(MockedDocumentGenerator.BASE_URI.toString(), result.asUri().toString());
  }

  /**
   * Per XPath 3.1 spec, if the argument is a node but not a document node, the
   * function returns empty sequence (not an error).
   */
  @Test
  void testWithNonDocumentNodeReturnsEmpty() {
    DynamicContext dynamicContext = newDynamicContext();

    // /root returns the root assembly node, not the document node
    IAnyUriItem result = IMetapathExpression.compile("document-uri(/root)", dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    // Non-document nodes return null (empty sequence)
    assertNull(result);
  }

  /**
   * Per XPath 3.1 spec, if the zero-argument form is called and the context item
   * is not a node, a type error (err:XPTY0004) is raised.
   */
  @Test
  void testNotANodeThrowsTypeError() {
    DynamicContext dynamicContext = newDynamicContext();

    assertThrows(InvalidTypeMetapathException.class, () -> {
      IMetapathExpression.compile("document-uri()", dynamicContext.getStaticContext())
          .evaluateAs(IStringItem.valueOf("test"), IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }

  /**
   * Per XPath 3.1 spec, if the context item is absent, a dynamic error
   * (err:XPDY0002) is raised.
   */
  @Test
  void testContextAbsentThrowsDynamicError() {
    DynamicContext dynamicContext = newDynamicContext();

    assertThrows(ContextAbsentDynamicMetapathException.class, () -> {
      IMetapathExpression.compile("document-uri()", dynamicContext.getStaticContext())
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }
}
