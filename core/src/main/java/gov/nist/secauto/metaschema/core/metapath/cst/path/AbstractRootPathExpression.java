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
    extends AbstractPathExpression<INodeItem> {
  @NonNull
  private final IExpression expression;
  @NonNull
  private final Class<? extends INodeItem> staticResultType;

  /**
   * Construct a new relative path expression of "/expression".
   *
   * @param expression
   *          the path expression to evaluate from the root
   */
  @SuppressWarnings("null")
  public AbstractRootPathExpression(@NonNull IExpression expression) {
    this.expression = expression;
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(INodeItem.class, List.of(expression));
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

  @Override
  public Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  public Class<? extends INodeItem> getStaticResultType() {
    return staticResultType;
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of(expression);
  }
}
