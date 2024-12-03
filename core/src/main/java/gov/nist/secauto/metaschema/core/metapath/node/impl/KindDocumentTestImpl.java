/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.node.impl;

import gov.nist.secauto.metaschema.core.metapath.IItem;
import gov.nist.secauto.metaschema.core.metapath.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.node.IKindTest;
import gov.nist.secauto.metaschema.core.metapath.node.IRootAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Tests that that a given item is an {@link IDocumentNodeItem} that has a root
 * node of the provided kind.
 */
public class KindDocumentTestImpl implements IKindTest<IDocumentNodeItem> {
  private final IKindTest<IAssemblyNodeItem> assemblyTest;
  @NonNull
  private final String signature;

  /**
   * Construct a new test.
   *
   * @param assemblyTest
   *          the root node test
   */
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
