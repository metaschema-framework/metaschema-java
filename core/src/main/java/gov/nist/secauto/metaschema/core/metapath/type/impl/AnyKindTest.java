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

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class AnyKindTest<I extends INodeItem>
    extends AbstractItemType<I>
    implements IKindTest<I> {
  @NonNull
  public static final IKindTest<INodeItem> ANY_NODE = new AnyKindTest<>(
      "node",
      INodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IModuleNodeItem> ANY_MODULE = new AnyKindTest<>(
      "module",
      IModuleNodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IDocumentNodeItem> ANY_DOCUMENT = new AnyKindTest<>(
      "document-node",
      IDocumentNodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IAssemblyNodeItem> ANY_ASSEMBLY = new AnyKindTest<>(
      "assembly",
      IAssemblyNodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IFieldNodeItem> ANY_FIELD = new AnyKindTest<>(
      "field",
      IFieldNodeItem.class,
      "");
  @NonNull
  public static final IKindTest<IFlagNodeItem> ANY_FLAG = new AnyKindTest<>(
      "flag",
      IFlagNodeItem.class,
      "");
  @NonNull
  private final String signature;

  protected AnyKindTest(
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
