/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.nist.secauto.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.testing.model.mocking.MockedDocumentGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests for the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-functions-31/#func-path">fn:path</a>
 * function.
 * <p>
 * Per XPath 3.1 spec:
 * <ul>
 * <li>Zero-argument form uses context item as implicit argument</li>
 * <li>If context item is absent: raises err:XPDY0002</li>
 * <li>If context item is not a node: raises err:XPTY0004</li>
 * <li>Returns a path expression identifying the node in the tree</li>
 * </ul>
 */
class FnPathTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() {
    // path(/root) and deeper return Q{...} namespace-qualified paths
    return Stream.of(
        Arguments.of("path(/root)"),
        Arguments.of("path(/root/assembly)"),
        Arguments.of("path(/root/assembly/@assembly-flag)"),
        Arguments.of("path(/root/field)"),
        Arguments.of("path(/root/field/@field-flag)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull String metapath) {
    DynamicContext dynamicContext = newDynamicContext();

    IStringItem result = IMetapathExpression.compile(metapath, dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    assertNotNull(result);
    // Path should start with "/" for absolute paths or "Q{" for namespace-qualified
    // paths
    assertTrue(result.asString().startsWith("/") || result.asString().startsWith("Q{"));
  }

  /**
   * Per XPath 3.1 spec, path() with document node focus returns "/".
   */
  @Test
  void testWithDocumentNodeFocus() {
    DynamicContext dynamicContext = newDynamicContext();

    IStringItem result = IMetapathExpression.compile("path()", dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    assertNotNull(result);
    assertEquals("/", result.asString());
  }

  /**
   * Per XPath 3.1 spec, path(.) with document node focus returns "/".
   */
  @Test
  void testWithSelfAndDocumentNodeFocus() {
    DynamicContext dynamicContext = newDynamicContext();

    IStringItem result = IMetapathExpression.compile("path(.)", dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    assertNotNull(result);
    assertEquals("/", result.asString());
  }

  /**
   * Per XPath 3.1 spec, if the zero-argument form is called and the context item
   * is not a node, a type error (err:XPTY0004) is raised.
   */
  @Test
  void testNotANodeThrowsTypeError() {
    DynamicContext dynamicContext = newDynamicContext();

    assertThrows(InvalidTypeMetapathException.class, () -> {
      IMetapathExpression.compile("path()", dynamicContext.getStaticContext())
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
      IMetapathExpression.compile("path()", dynamicContext.getStaticContext())
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }
}
