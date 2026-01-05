/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.path;

import dev.metaschema.core.metapath.cst.IExpressionVisitor;
import dev.metaschema.core.metapath.item.node.IFlagNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath path expression that finds any child {@link IFlagNodeItem} that
 * matches the provided {@code test}.
 * <p>
 * Based on the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-steps">step</a> syntax.
 */
public class FlagStep
    extends AbstractStepExpression<IFlagNodeItem> {

  /**
   * Construct a new expression that finds any child {@link IFlagNodeItem} that
   * matches the provided {@code test}.
   *
   * @param text
   *          the parsed text of the expression
   * @param test
   *          the test to use to match
   */
  public FlagStep(@NonNull String text, @NonNull INodeTestExpression test) {
    super(text, test);
  }

  @Override
  public Class<IFlagNodeItem> getBaseResultType() {
    return IFlagNodeItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitFlagStep(this, context);
  }

  @Override
  protected Stream<? extends IFlagNodeItem> getChildNodes(INodeItem focus) {
    return focus.flags();
  }

  @Override
  protected Stream<? extends IFlagNodeItem> getChildNodesWithName(
      INodeItem focus,
      IEnhancedQName name) {
    return ObjectUtils.notNull(Stream.ofNullable(focus.getFlagByName(name)));
  }
}
