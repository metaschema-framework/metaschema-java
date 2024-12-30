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

@SuppressWarnings("rawtypes")
public class ModelInstance
    extends AbstractNamedInstanceExpression<IModelNodeItem> {

  /**
   * Construct a new expression that finds any child {@link IModelNodeItem} that
   * matches the provided {@code test}.
   *
   * @param test
   *          the test to use to match
   */
  public ModelInstance(@NonNull INodeTestExpression test) {
    super(test);
  }

  @Override
  public Class<IModelNodeItem> getBaseResultType() {
    return IModelNodeItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitModelInstance(this, context);
  }

  @Override
  protected Stream<? extends IModelNodeItem<?, ?>> getFocusedChildren(INodeItem focusedItem) {
    return focusedItem.modelItems();
  }

  @Override
  protected Stream<? extends IModelNodeItem<?, ?>> getFocusedChildrenWithName(
      INodeItem focusedItem,
      IEnhancedQName name) {
    return ObjectUtils.notNull(focusedItem.getModelItemsByName(name).stream());
  }
}
