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

import edu.umd.cs.findbugs.annotations.NonNull;

public class RootSlashPath
    extends AbstractRootPathExpression {

  /**
   * Construct a new expression that finds a child of the document root using the
   * {@code right} expression.
   *
   * @param node
   *          the path to evaluate relative to the document root
   */
  public RootSlashPath(@NonNull IExpression node) {
    super(node);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitRootSlashPath(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return getExpression().accept(dynamicContext, ItemUtils.getDocumentNodeItems(focus));
  }
}
