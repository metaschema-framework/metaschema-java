/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.ExpressionUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for Metapath expressions based on the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-31/#id-relative-path-expressions">relative path
 * expressions</a>.
 */
public abstract class AbstractRelativePathExpression
    extends AbstractSearchPathExpression {
  @NonNull
  private final IExpression left;
  @NonNull
  private final IExpression right;

  /**
   * Construct a new relative path expression of "left/right".
   *
   * @param text
   *          the parsed text of the expression
   * @param left
   *          the left part of the path
   * @param right
   *          the right part of the path
   */
  @SuppressWarnings("null")
  public AbstractRelativePathExpression(
      @NonNull String text,
      @NonNull IExpression left,
      @NonNull IExpression right) {
    super(text, ExpressionUtils.analyzeStaticResultType(INodeItem.class, List.of(left, right)));
    this.left = left;
    this.right = right;
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
}
