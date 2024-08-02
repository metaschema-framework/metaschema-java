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

public abstract class AbstractRelativePathExpression
    extends AbstractPathExpression<INodeItem> {
  @NonNull
  private final IExpression left;
  @NonNull
  private final IExpression right;
  @NonNull
  private final Class<? extends INodeItem> staticResultType;

  /**
   * Construct a new relative path expression of "left/right".
   *
   * @param left
   *          the left part of the path
   * @param right
   *          the right part of the path
   */
  @SuppressWarnings("null")
  public AbstractRelativePathExpression(@NonNull IExpression left, @NonNull IExpression right) {
    this.left = left;
    this.right = right;
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(getBaseResultType(), List.of(left, right));
  }

  /**
   * The expression associated with the left path segment.
   *
   * @return the expression
   */
  @NonNull
  public IExpression getLeft() {
    return left;
  }

  /**
   * The expression associated with the right path segment.
   *
   * @return the expression
   */
  @NonNull
  public IExpression getRight() {
    return right;
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of(left, right);
  }

  @Override
  public final @NonNull Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  public Class<? extends INodeItem> getStaticResultType() {
    return staticResultType;
  }
}
