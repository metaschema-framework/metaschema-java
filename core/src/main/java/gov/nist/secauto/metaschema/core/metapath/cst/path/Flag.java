/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Flag // NOPMD - intentional name
    extends AbstractNamedInstanceExpression<IFlagNodeItem> {

  /**
   * Construct a new expression that finds any child {@link IFlagNodeItem} that
   * matches the provided {@code test}.
   *
   * @param test
   *          the test to use to match
   */
  public Flag(@NonNull INodeTestExpression test) {
    super(test);
  }

  @Override
  public Class<IFlagNodeItem> getBaseResultType() {
    return IFlagNodeItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitFlag(this, context);
  }

  @Override
  protected Stream<? extends IFlagNodeItem> getFocusedChildren(INodeItem focusedItem) {
    return focusedItem.flags();
  }

  @Override
  protected Stream<? extends IFlagNodeItem> getFocusedChildrenWithName(
      INodeItem focusedItem,
      IEnhancedQName name) {
    IFlagNodeItem item = focusedItem.getFlagByName(name);
    return ObjectUtils.notNull(item == null ? Stream.empty() : Stream.of(item));
  }
}
