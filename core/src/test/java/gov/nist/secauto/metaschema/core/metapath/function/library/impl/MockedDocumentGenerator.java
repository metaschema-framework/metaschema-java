/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function.library.impl;

import gov.nist.secauto.metaschema.core.mdm.IDMAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMDocumentNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMFieldNodeItem;
import gov.nist.secauto.metaschema.core.mdm.IDMRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.model.IAssemblyDefinition;
import gov.nist.secauto.metaschema.core.model.IAssemblyInstance;
import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.core.model.IFlagInstance;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.testing.MockedModelTestSupport;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;

public class MockedDocumentGenerator {
  @NonNull
  public static final URI BASE_URI = URI.create("https://example.com/resource");
  @NonNull
  public static final String NS = "http://example.com/ns";
  @NonNull
  public static final IEnhancedQName ROOT_QNAME = IEnhancedQName.of(NS, "root");
  @NonNull
  public static final IEnhancedQName ASSEMBLY_QNAME = IEnhancedQName.of(NS, "assembly");
  @NonNull
  public static final IEnhancedQName FIELD_QNAME = IEnhancedQName.of(NS, "field");
  @NonNull
  public static final IEnhancedQName ASSEMBLY_FLAG_QNAME = IEnhancedQName.of("assembly-flag");
  @NonNull
  public static final IEnhancedQName FIELD_FLAG_QNAME = IEnhancedQName.of("field-flag");

  public static IDMDocumentNodeItem generateDocumentNodeItem() {
    MockedModelTestSupport mocking = new MockedModelTestSupport();

    IAssemblyDefinition rootDefinition = mocking.assembly().qname(ROOT_QNAME).rootQName(ROOT_QNAME).toDefinition();
    IAssemblyInstance assemblyInstance = mocking.assembly().qname(ASSEMBLY_QNAME).toInstance(rootDefinition);
    IFlagInstance assemblyFlag = mocking.flag().qname(ASSEMBLY_FLAG_QNAME).toInstance(assemblyInstance.getDefinition());
    IFieldInstance fieldInstance = mocking.field().qname(FIELD_QNAME).toInstance(rootDefinition);
    IFlagInstance fieldFlag = mocking.flag().qname(FIELD_FLAG_QNAME).toInstance(fieldInstance.getDefinition());

    IDMDocumentNodeItem document = IDMDocumentNodeItem.newInstance(
        BASE_URI,
        rootDefinition);
    IDMRootAssemblyNodeItem root = document.getRootAssemblyNodeItem();
    IDMAssemblyNodeItem assembly = root.newAssembly(assemblyInstance);
    assembly.newFlag(assemblyFlag, IStringItem.valueOf("assembly-flag"));
    IDMFieldNodeItem field = root.newField(fieldInstance, IStringItem.valueOf("field"));
    field.newFlag(fieldFlag, IStringItem.valueOf("field-flag"));
    return document;
  }

  private MockedDocumentGenerator() {
    // disable construction
  }

}
