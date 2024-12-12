/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.library.impl.MockedDocumentGenerator;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.type.TypeMetapathException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnRootTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            "root()"),
        Arguments.of(
            "root(.)"),
        Arguments.of(
            "root(/root)"),
        Arguments.of(
            "root(/root/assembly)"),
        Arguments.of(
            "root(/root/assembly/@assembly-flag)"),
        Arguments.of(
            "root(/root/field)"),
        Arguments.of(
            "root(/root/field/@field-flag)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull String metapath) {
    DynamicContext dynamicContext = newDynamicContext();

    INodeItem root = MockedDocumentGenerator.generateDocumentNodeItem();
    INodeItem result = IMetapathExpression.compile(metapath, dynamicContext.getStaticContext())
        .evaluateAs(root, IMetapathExpression.ResultType.ITEM, dynamicContext);
    INodeItem rootResult
        = IMetapathExpression.compile("ancestor-or-self::node()[1]", dynamicContext.getStaticContext())
            .evaluateAs(root, IMetapathExpression.ResultType.ITEM, dynamicContext);
    assertEquals(root, result);
    assertEquals(rootResult, result);
  }

  @Test
  void testContextAbsent() {
    DynamicContext dynamicContext = newDynamicContext();

    MetapathException ex = assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("root()", dynamicContext.getStaticContext())
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
    Throwable cause = ex.getCause() != null ? ex.getCause().getCause() : null;

    assertAll(
        () -> assertEquals(DynamicMetapathException.class, cause == null
            ? null
            : cause.getClass()),
        () -> assertEquals(DynamicMetapathException.DYNAMIC_CONTEXT_ABSENT, cause instanceof DynamicMetapathException
            ? ((DynamicMetapathException) cause).getCode()
            : null));
  }

  @Test
  void testNotANode() {
    DynamicContext dynamicContext = newDynamicContext();

    MetapathException ex = assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("root()", dynamicContext.getStaticContext())
          .evaluateAs(IStringItem.valueOf("test"), IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
    Throwable cause = ex.getCause() != null ? ex.getCause().getCause() : null;

    assertAll(
        () -> assertEquals(InvalidTypeMetapathException.class, cause == null
            ? null
            : cause.getClass()),
        () -> assertEquals(TypeMetapathException.INVALID_TYPE_ERROR, cause instanceof TypeMetapathException
            ? ((TypeMetapathException) cause).getCode()
            : null));
  }
}
