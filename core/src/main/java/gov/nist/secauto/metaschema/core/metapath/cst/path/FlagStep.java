/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

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
   * @param test
   *          the test to use to match
   */
  public FlagStep(@NonNull INodeTestExpression test) {
    super(test);
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
