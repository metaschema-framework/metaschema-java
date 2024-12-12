/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.Collections;
import java.util.List;

public interface INodeTestExpression extends IExpression {
  @SuppressWarnings("null")
  @Override
  default List<? extends IExpression> getChildren() {
    return Collections.emptyList();
  }

  @Override
  default Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  default Class<INodeItem> getStaticResultType() {
    return getBaseResultType();
  }
}
