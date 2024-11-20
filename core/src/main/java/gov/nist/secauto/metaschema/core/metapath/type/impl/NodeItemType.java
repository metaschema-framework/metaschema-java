/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.type.impl;

import gov.nist.secauto.metaschema.core.metapath.item.node.IAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDocumentNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

public class NodeItemType
    extends AbstractItemType<INodeItem> {
  @NonNull
  public static final NodeItemType ANY_NODE = new NodeItemType(
      "node",
      INodeItem.class,
      "");
  @NonNull
  public static final NodeItemType ANY_DOCUMENT = new NodeItemType(
      "document",
      IDocumentNodeItem.class,
      "");
  @NonNull
  public static final NodeItemType ANY_ASSEMBLY = new NodeItemType(
      "assembly",
      IAssemblyNodeItem.class,
      "");
  @NonNull
  public static final NodeItemType ANY_FLAG = new NodeItemType(
      "flag",
      IAssemblyNodeItem.class,
      "");
  @NonNull
  private final String nodeName;
  @NonNull
  private final String signature;

  protected NodeItemType(
      @NonNull String name,
      @NonNull Class<? extends INodeItem> itemClass,
      @NonNull String test) {
    super(itemClass);
    this.nodeName = name;
    this.signature = ObjectUtils.notNull(new StringBuilder()
        .append(name)
        .append('(')
        .append(test)
        .append(')')
        .toString());
  }

  @NonNull
  public String getNodeName() {
    return nodeName;
  }

  @Override
  public String toSignature() {
    return signature;
  }
}
