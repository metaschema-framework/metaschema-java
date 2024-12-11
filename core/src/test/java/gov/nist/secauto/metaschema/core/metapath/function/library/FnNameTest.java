/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.nist.secauto.metaschema.core.mdm.IDMAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMDocumentNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMFieldNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.DynamicMetapathException;
import gov.nist.secauto.metaschema.core.metapath.ExpressionTestBase;
import gov.nist.secauto.metaschema.core.metapath.IMetapathExpression;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.type.TypeMetapathException;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.model.IResourceLocation;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.MockedModelTestSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

class FnNameTest
    extends ExpressionTestBase {
  @NonNull
  private static final IEnhancedQName ROOT_QNAME = IEnhancedQName.of(NS, "root");
  @NonNull
  private static final IEnhancedQName ASSEMBLY_QNAME = IEnhancedQName.of(NS, "assembly");
  @NonNull
  private static final IEnhancedQName FIELD_QNAME = IEnhancedQName.of(NS, "field");
  @NonNull
  private static final IEnhancedQName ASSEMBLY_FLAG_QNAME = IEnhancedQName.of("assembly-flag");
  @NonNull
  private static final IEnhancedQName FIELD_FLAG_QNAME = IEnhancedQName.of("field-flag");

  private IDocumentNodeItem newDocumentNodeItem() {
    MockedModelTestSupport mocking = new MockedModelTestSupport(getContext());
    IResourceLocation resourceLocation = mocking.mock(IResourceLocation.class);

    IAssemblyDefinition rootDefinition = mocking.assembly().qname(ROOT_QNAME).rootQName(ROOT_QNAME).toDefinition();
    IAssemblyInstance assemblyInstance = mocking.assembly().qname(ASSEMBLY_QNAME).toInstance(rootDefinition);
    IFlagInstance assemblyFlag = mocking.flag().qname(ASSEMBLY_FLAG_QNAME).toInstance(assemblyInstance.getDefinition());
    IFieldInstance fieldInstance = mocking.field().qname(FIELD_QNAME).toInstance(rootDefinition);
    IFlagInstance fieldFlag = mocking.flag().qname(FIELD_FLAG_QNAME).toInstance(fieldInstance.getDefinition());

    IDMDocumentNodeItem document = IDMDocumentNodeItem.newInstance(
        URI.create("https://example.com/resource"),
        resourceLocation,
        rootDefinition,
        resourceLocation);
    IDMRootAssemblyNodeItem root = document.getRootAssemblyNodeItem();
    IDMAssemblyNodeItem assembly = root.newAssembly(assemblyInstance, resourceLocation);
    assembly.newFlag(assemblyFlag, resourceLocation, IStringItem.valueOf("assembly-flag"));
    IDMFieldNodeItem field = root.newField(fieldInstance, resourceLocation, IStringItem.valueOf("field"));
    field.newFlag(fieldFlag, resourceLocation, IStringItem.valueOf("field-flag"));

    return document;
  }

  private static Stream<Arguments> provideValues() { // NOPMD - false positive
    return Stream.of(
        Arguments.of(
            null,
            "name()"),
        Arguments.of(
            null,
            "name(.)"),
        Arguments.of(
            ROOT_QNAME,
            "name(/root)"),
        Arguments.of(
            ASSEMBLY_QNAME,
            "name(/root/assembly)"),
        Arguments.of(
            ASSEMBLY_FLAG_QNAME,
            "name(/root/assembly/@assembly-flag)"),
        Arguments.of(
            FIELD_QNAME,
            "name(/root/field)"),
        Arguments.of(
            FIELD_FLAG_QNAME,
            "name(/root/field/@field-flag)"));
  }

  @ParameterizedTest
  @MethodSource("provideValues")
  void test(@Nullable IEnhancedQName expected, @NonNull String metapath) {
    DynamicContext dynamicContext = newDynamicContext();

    IStringItem result = IMetapathExpression.compile(metapath, dynamicContext.getStaticContext())
        .evaluateAs(newDocumentNodeItem(), IMetapathExpression.ResultType.ITEM, dynamicContext);
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

    MetapathException ex = assertThrows(MetapathException.class, () -> {
      IMetapathExpression.compile("name()", dynamicContext.getStaticContext())
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
      IMetapathExpression.compile("name()", dynamicContext.getStaticContext())
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
