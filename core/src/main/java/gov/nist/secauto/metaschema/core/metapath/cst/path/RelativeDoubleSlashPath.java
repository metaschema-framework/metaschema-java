/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An expression that finds an ancestor of the {@code left} expression using the
 * {@code right} expression.
 * <p>
 * Based on the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-31/#id-relative-path-expressions">relative path
 * expressions</a>.
 */
public class RelativeDoubleSlashPath
    extends AbstractRelativePathExpression {

  /**
   * Construct a new expression that finds an ancestor of the {@code left}
   * expression using the {@code right} expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param left
   *          the context path
   * @param right
   *          the path to evaluate in the context of the left
   */
  public RelativeDoubleSlashPath(
      @NonNull String text,
      @NonNull IExpression left,
      @NonNull IExpression right) {
    super(text, left, right);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitRelativeDoubleSlashPath(this, context);
  }

  @Override
  protected ISequence<? extends INodeItem> evaluate(
      DynamicContext dynamicContext,
      ISequence<?> focus) {
    ISequence<?> leftResult = getLeft().accept(dynamicContext, focus);

    // evaluate the right path in the context of the left
    return ISequence.of(search(getRight(), dynamicContext, leftResult));
  }
}
