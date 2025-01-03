/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath path expression that finds any child {@link IModelNodeItem} that
 * matches the provided {@code test}.
 * <p>
 * Based on the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-steps">step</a> syntax.
 */
@SuppressWarnings("rawtypes")
public class ModelInstanceStep
    extends AbstractStepExpression<IModelNodeItem> {

  /**
   * Construct a new expression that finds any child {@link IModelNodeItem} that
   * matches the provided {@code test}.
   *
   * @param text
   *          the parsed text of the expression
   * @param test
   *          the test to use to match
   */
  public ModelInstanceStep(@NonNull String text, @NonNull INodeTestExpression test) {
    super(text, test);
  }

  @Override
  public Class<IModelNodeItem> getBaseResultType() {
    return IModelNodeItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitModelInstanceStep(this, context);
  }

  @Override
  protected Stream<? extends IModelNodeItem<?, ?>> getChildNodes(INodeItem focus) {
    return focus.modelItems();
  }

  @Override
  protected Stream<? extends IModelNodeItem<?, ?>> getChildNodesWithName(
      INodeItem focus,
      IEnhancedQName name) {
    return ObjectUtils.notNull(focus.getModelItemsByName(name).stream());
  }
}
