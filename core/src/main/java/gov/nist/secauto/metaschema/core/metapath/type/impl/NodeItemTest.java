/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFieldNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModuleNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.metapath.type.IKindTest;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class NodeItemTest<I extends INodeItem>
    extends AbstractItemType<I>
    implements IKindTest<I> {
  @NonNull
  public static final IKindTest<INodeItem> ANY_NODE = new NodeItemTest(
      "node",
      INodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IModuleNodeItem> ANY_MODULE = new NodeItemTest(
      "module",
      IModuleNodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IDocumentNodeItem> ANY_DOCUMENT = new NodeItemTest(
      "document-node",
      IDocumentNodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IAssemblyNodeItem> ANY_ASSEMBLY = new NodeItemTest(
      "assembly",
      IAssemblyNodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IFieldNodeItem> ANY_FIELD = new NodeItemTest(
      "field",
      IFieldNodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IFlagNodeItem> ANY_FLAG = new NodeItemTest(
      "flag",
      IFlagNodeItem.class,
      "");
  @NonNull
  private final String signature;

  protected NodeItemTest(
      @NonNull String testName,
      @NonNull Class<I> itemClass,
      @NonNull String test) {
    super(itemClass);
    this.signature = ObjectUtils.notNull(new StringBuilder()
        .append(testName)
        .append('(')
        .append(test)
        .append(')')
        .toString());
  }

  @Override
  public String toSignature() {
    return signature;
  }
}
