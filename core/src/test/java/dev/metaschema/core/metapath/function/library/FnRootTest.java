/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;
import dev.metaschema.core.testsupport.mocking.MockedDocumentGenerator;

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

    assertThrows(ContextAbsentDynamicMetapathException.class, () -> {
      IMetapathExpression.compile("root()", dynamicContext.getStaticContext())
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }

  @Test
  void testNotANode() {
    DynamicContext dynamicContext = newDynamicContext();

    assertThrows(InvalidTypeMetapathException.class, () -> {
      IMetapathExpression.compile("root()", dynamicContext.getStaticContext())
          .evaluateAs(IStringItem.valueOf("test"), IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }
}
