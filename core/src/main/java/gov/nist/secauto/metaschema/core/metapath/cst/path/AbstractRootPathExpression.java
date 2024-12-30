/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.ExpressionUtils;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractRootPathExpression
    extends AbstractSearchPathExpression {
  @NonNull
  private final IExpression expression;

  /**
   * Construct a new relative path expression of "/expression".
   *
   * @param expression
   *          the path expression to evaluate from the root
   */
  @SuppressWarnings("null")
  public AbstractRootPathExpression(@NonNull IExpression expression) {
    super(ExpressionUtils.analyzeStaticResultType(INodeItem.class, List.of(expression)));
    this.expression = expression;
  }

  /**
   * Get the path expression.
   *
   * @return the expression
   */
  @NonNull
  public IExpression getExpression() {
    return expression;
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of(expression);
  }
}
