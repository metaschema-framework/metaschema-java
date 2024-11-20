/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

public class RootSlashOnlyPath
    extends AbstractPathExpression<INodeItem> {

  @Override
  public List<? extends IExpression> getChildren() {
    return CollectionUtil.emptyList();
  }

  @Override
  public Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitRootSlashOnlyPath(this, context);
  }

  @Override
  public ISequence<? extends INodeItem> accept(
      DynamicContext dynamicContext,
      ISequence<?> focus) {

    return ObjectUtils.notNull(focus.stream()
        .map(ItemUtils::checkItemIsNodeItemForStep)
        .map(item -> Axis.ANCESTOR_OR_SELF.execute(ObjectUtils.notNull(item)).findFirst().get())
        .collect(CustomCollectors.toSequence()));
  }
}
