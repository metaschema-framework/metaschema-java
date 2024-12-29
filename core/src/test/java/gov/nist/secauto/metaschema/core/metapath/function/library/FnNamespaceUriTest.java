/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.metapath.ContextAbsentDynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.TypeMetapathError;
import gov.nist.secauto.metaschema.core.metapath.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.function.library.impl.MockedDocumentGenerator;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnNamespaceUriTest
    extends ExpressionTestBase {

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            null,
            "namespace-uri()"),
        Arguments.of(
            null,
            "namespace-uri(.)"),
        Arguments.of(
            MockedDocumentGenerator.ROOT_QNAME,
            "namespace-uri(/root)"),
        Arguments.of(
            MockedDocumentGenerator.ASSEMBLY_QNAME,
            "namespace-uri(/root/assembly)"),
        Arguments.of(
            MockedDocumentGenerator.ASSEMBLY_FLAG_QNAME,
            "namespace-uri(/root/assembly/@assembly-flag)"),
        Arguments.of(
            MockedDocumentGenerator.FIELD_QNAME,
            "namespace-uri(/root/field)"),
        Arguments.of(
            MockedDocumentGenerator.FIELD_FLAG_QNAME,
            "namespace-uri(/root/field/@field-flag)"));
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
            : expected.getNamespace(),
        result.asString());
  }

  @Test
  void testContextAbsent() {
    DynamicContext dynamicContext = newDynamicContext();

    MetapathException ex = assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("namespace-uri()", dynamicContext.getStaticContext())
          .evaluateAs(null, IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
    Throwable cause = ex.getCause() != null ? ex.getCause().getCause() : null;

    assertEquals(ContextAbsentDynamicMetapathException.class, cause == null ? null : cause.getClass());
  }

  @Test
  void testNotANode() {
    DynamicContext dynamicContext = newDynamicContext();

    MetapathException ex = assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("namespace-uri()", dynamicContext.getStaticContext())
          .evaluateAs(IStringItem.valueOf("test"), IMetapathExpression.ResultType.ITEM, dynamicContext);
    });
    Throwable cause = ex.getCause() != null ? ex.getCause().getCause() : null;

    assertAll(
        () -> assertEquals(InvalidTypeMetapathException.class, cause == null
            ? null
            : cause.getClass()),
        () -> assertEquals(TypeMetapathError.INVALID_TYPE_ERROR, cause instanceof TypeMetapathError
            ? ((TypeMetapathError) cause).getErrorCode().getCode()
            : null));
  }
}
