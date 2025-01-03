/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An expression that finds an ancestor of the document root using the
 * {@code right} expression.
 * <p>
 * Based on the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-31/#id-path-operator">path
 * operator</a>.
 */
public class RootDoubleSlashPath
    extends AbstractRootPathExpression {

  /**
   * Construct a new expression that finds an ancestor of the document root using
   * the {@code right} expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param node
   *          the path to evaluate relative to the document root
   */
  public RootDoubleSlashPath(@NonNull String text, @NonNull IExpression node) {
    super(text, node);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitRootDoubleSlashPath(this, context);
  }

  @Override
  public ISequence<?> accept(
      DynamicContext dynamicContext, ISequence<?> context) {
    return ISequence.of(search(getExpression(), dynamicContext, context));
  }
}
