/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The base class for all Metapath expressions that select node items based on
 * the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-path-expressions">path
 * expressions</a>.
 *
 * @param <RESULT_TYPE>
 *          the Java base type of the resulting node item
 */
public abstract class AbstractPathExpression<RESULT_TYPE extends INodeItem>
    extends AbstractExpression {

  /**
   * Construct a new path expression.
   *
   * @param text
   *          the parsed text of the expression
   */
  public AbstractPathExpression(@NonNull String text) {
    super(text);
  }

  @Override
  public abstract Class<RESULT_TYPE> getBaseResultType();

  @Override
  public Class<? extends RESULT_TYPE> getStaticResultType() {
    return getBaseResultType();
  }
}
