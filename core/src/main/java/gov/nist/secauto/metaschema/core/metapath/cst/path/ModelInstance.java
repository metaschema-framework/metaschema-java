/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractNamedInstanceExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.IModelNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

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
  public ISequence<? extends IModelNodeItem<?, ?>> accept(
      DynamicContext dynamicContext,
      ISequence<?> focus) {
    return ISequence.of(ObjectUtils.notNull(focus.stream()
        .map(ItemUtils::checkItemIsNodeItemForStep)
        .flatMap(item -> {
          assert item != null;
          return match(dynamicContext, item);
        })));
  }

  /**
   * Get a stream of matching child node items for the provided {@code context}.
   *
   * @param dynamicContext
   *          the evaluation context
   * @param focusedItem
   *          the node item to match child items of
   * @return the stream of matching node items
   */
  @SuppressWarnings("null")
  @NonNull
  protected Stream<? extends IModelNodeItem<?, ?>> match(
      @NonNull DynamicContext dynamicContext,
      @NonNull INodeItem focusedItem) {
    Stream<? extends IModelNodeItem<?, ?>> retval;

    INodeTestExpression test = getTest();
    if (test instanceof NameTest) {
      QName name = ((NameTest) getTest()).getName();
      List<? extends IModelNodeItem<?, ?>> items = focusedItem.getModelItemsByName(name);
      retval = items.stream();
    } else if (test instanceof Wildcard) {
      // match all items
      retval = ((Wildcard) test).match(focusedItem.modelItems());
    } else {
      throw new UnsupportedOperationException(test.getClass().getName());
    }
    return retval;
  }
}
