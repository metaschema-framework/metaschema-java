/*
/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.mocking.MockedDocumentGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

class FnInnermostTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            "(/root/assembly/@assembly-flag,/root/field/@field-flag)",
            "(.,/root,/root/assembly,/root/assembly/@assembly-flag,/root/field,/root/field/@field-flag)"),
        Arguments.of(
            "(/root/assembly,/root/field)",
            "(.,/root,/root/assembly,/root/field)"),
        Arguments.of(
            "(/root/assembly/@assembly-flag,/root/field/@field-flag)",
            "(.,/root,/root/assembly,/root/assembly/@assembly-flag,/root/field,/root/field/@field-flag," +
                ".,/root,/root/assembly,/root/assembly/@assembly-flag,/root/field,/root/field/@field-flag)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@NonNull String expectedValueMetapath, @NonNull String actualValuesMetapath) {
    DynamicContext dynamicContext = newDynamicContext();
    INodeItem node = MockedDocumentGenerator.generateDocumentNodeItem();

    ISequence<? extends INodeItem> expected
        = IMetapathExpression.compile(expectedValueMetapath, dynamicContext.getStaticContext())
            .evaluate(node, dynamicContext);

    ISequence<? extends INodeItem> actual
        = IMetapathExpression.compile("innermost(" + actualValuesMetapath + ")", dynamicContext.getStaticContext())
            .evaluate(node, dynamicContext);

    // Test the expected values against the alternate implementation from the spec
    ISequence<? extends INodeItem> values
        = IMetapathExpression.compile(expectedValueMetapath, dynamicContext.getStaticContext())
            .evaluate(node, dynamicContext);
    ISequence<? extends INodeItem> alternate
        = IMetapathExpression.compile("$nodes except $nodes/ancestor::node()", dynamicContext.getStaticContext())
            .evaluate(null, dynamicContext.subContext().bindVariableValue(IEnhancedQName.of("nodes"), values));

    assertEquals(expected, actual);
    assertEquals(expected, alternate);
  }

  @Test
  void testNotANode() {
    DynamicContext dynamicContext = newDynamicContext();
    assertThrows(InvalidTypeMetapathException.class, () -> {
      IMetapathExpression.compile("innermost('test')", dynamicContext.getStaticContext())
          .evaluateAs(IStringItem.valueOf("test"), IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }
}
