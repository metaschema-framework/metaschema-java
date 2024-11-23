/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class KindDocumentTestImpl implements IKindTest<IDocumentNodeItem> {
  @NonNull
  public static final KindDocumentTestImpl ANY = new KindDocumentTestImpl();
  private final IKindTest<IAssemblyNodeItem> assemblyTest;
  @NonNull
  private final String signature;

  private KindDocumentTestImpl() {
    this.assemblyTest = null;
    this.signature = "document()";
  }

  public KindDocumentTestImpl(@NonNull IKindTest<IAssemblyNodeItem> assemblyTest) {
    this.assemblyTest = assemblyTest;

    // build the signature
    this.signature = ObjectUtils.notNull(new StringBuilder()
        .append("document-node(")
        .append(assemblyTest.toSignature())
        .append(')')
        .toString());
  }

  @Override
  public Class<IDocumentNodeItem> getItemClass() {
    return IDocumentNodeItem.class;
  }

  @Override
  public boolean isInstance(IItem item) {
    return item != null
        && IKindTest.super.isInstance(item)
        && assemblyMatches(ObjectUtils.asType(item));
  }

  private boolean assemblyMatches(@NonNull IDocumentNodeItem item) {
    IRootAssemblyNodeItem root = item.getRootAssemblyNodeItem();
    return assemblyTest == null || assemblyTest.isInstance(root);
  }

  @Override
  public String toSignature() {
    return this.signature;
  }

}
