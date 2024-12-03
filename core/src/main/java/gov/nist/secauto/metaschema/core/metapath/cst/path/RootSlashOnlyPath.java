/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.node.IDocumentBasedNodeItem;
import gov.nist.secauto.metaschema.core.metapath.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

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
  public ISequence<IDocumentBasedNodeItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return ItemUtils.getDocumentNodeItems(focus);
  }
}
