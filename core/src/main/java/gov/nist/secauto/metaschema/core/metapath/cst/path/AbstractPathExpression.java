/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

/**
 * The base class of all
 * <a href="https://www.w3.org/TR/xpath-31/#id-path-expressions">path
 * expressions</a> that select node items.
 *
 * @param <RESULT_TYPE>
 *          the Java base type of the resulting node item
 */
public abstract class AbstractPathExpression<RESULT_TYPE extends INodeItem>
    extends AbstractExpression
    implements IPathExpression<RESULT_TYPE> {
  @Override
  public abstract Class<RESULT_TYPE> getBaseResultType();

  @Override
  public Class<? extends RESULT_TYPE> getStaticResultType() {
    return getBaseResultType();
  }
}
