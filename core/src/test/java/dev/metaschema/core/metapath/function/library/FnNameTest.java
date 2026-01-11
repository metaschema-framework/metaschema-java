/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import dev.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.ExpressionTestBase;
import dev.metaschema.core.metapath.IMetapathExpression;
import dev.metaschema.core.metapath.item.atomic.IStringItem;
import dev.metaschema.core.metapath.type.InvalidTypeMetapathException;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.testsupport.mocking.MockedDocumentGenerator;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnNameTest
    extends ExpressionTestBase {
  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            null,
            "name()"),
        Arguments.of(
            null,
            "name(.)"),
        Arguments.of(
            MockedDocumentGenerator.ROOT_QNAME,
            "name(/root)"),
        Arguments.of(
            MockedDocumentGenerator.ASSEMBLY_QNAME,
            "name(/root/assembly)"),
        Arguments.of(
            MockedDocumentGenerator.ASSEMBLY_FLAG_QNAME,
            "name(/root/assembly/@assembly-flag)"),
        Arguments.of(
            MockedDocumentGenerator.FIELD_QNAME,
            "name(/root/field)"),
        Arguments.of(
            MockedDocumentGenerator.FIELD_FLAG_QNAME,
            "name(/root/field/@field-flag)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable IEnhancedQName expected, @NonNull String metapath) {
    DynamicContext dynamicContext = newDynamicContext();

    IStringItem result = IMetapathExpression.compile(metapath, dynamicContext.getStaticContext())
        .evaluateAs(
            MockedDocumentGenerator.generateDocumentNodeItem(),
            IMetapathExpression.ResultType.ITEM,
            dynamicContext);
    assertNotNull(result);
    assertEquals(
        expected == null
            ? ""
            : expected.toEQName(dynamicContext.getStaticContext()),
        result.asString());
  }

  @Test
  void testContextAbsent() {
    DynamicContext dynamicContext = newDynamicContext();

    assertThrows(ContextAbsentDynamicMetapathException.class, () -> {
      IMetapathExpression.compile("name()", dynamicContext.getStaticContext())
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }

  @Test
  void testNotANode() {
    DynamicContext dynamicContext = newDynamicContext();

    assertThrows(InvalidTypeMetapathException.class, () -> {
      IMetapathExpression.compile("name()", dynamicContext.getStaticContext())
          .evaluateAs(IStringItem.valueOf("test"), IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
  }
}
