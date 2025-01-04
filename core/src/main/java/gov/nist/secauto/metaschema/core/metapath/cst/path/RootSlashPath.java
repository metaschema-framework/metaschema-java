/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.util.CustomCollectors;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An expression that finds a child of the document root using the {@code right}
 * expression.
 * <p>
 * Based on the XPath 3.1
 * <a href= "https://www.w3.org/TR/xpath-31/#id-path-operator">path
 * operator</a>.
 */
public class RootSlashPath
    extends AbstractRootPathExpression {

  /**
   * Construct a new expression that finds a child of the document root using the
   * {@code node} expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param node
   *          the path to evaluate relative to the document root
   */
  public RootSlashPath(@NonNull String text, @NonNull IExpression node) {
    super(text, node);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitRootSlashPath(this, context);
  }

  @Override
  protected ISequence<?> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<?> roots = ObjectUtils.notNull(focus.stream()
        .map(ItemUtils::checkItemIsNodeItemForStep)
        // the previous checks for a null instance
        .flatMap(item -> Axis.ANCESTOR_OR_SELF.execute(ObjectUtils.notNull(item)).limit(1))
        .collect(CustomCollectors.toSequence()));
    return getExpression().accept(dynamicContext, roots);
  }
}
