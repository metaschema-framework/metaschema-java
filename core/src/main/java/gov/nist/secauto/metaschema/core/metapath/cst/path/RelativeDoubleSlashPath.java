/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class RelativeDoubleSlashPath
    extends AbstractRelativePathExpression {

  /**
   * Construct a new expression that finds an ancestor of the {@code left}
   * expression using the {@code right} expression.
   *
   * @param left
   *          the context path
   * @param right
   *          the path to evaluate in the context of the left
   */
  public RelativeDoubleSlashPath(@NonNull IExpression left, @NonNull IExpression right) {
    super(left, right);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitRelativeDoubleSlashPath(this, context);
  }

  @Override
  public ISequence<? extends INodeItem> accept(
      DynamicContext dynamicContext,
      ISequence<?> focus) {
    ISequence<?> leftResult = getLeft().accept(dynamicContext, focus);

    // evaluate the right path in the context of the left
    Stream<? extends INodeItem> result = search(getRight(), dynamicContext, leftResult);
    return ISequence.of(result);
  }
}
