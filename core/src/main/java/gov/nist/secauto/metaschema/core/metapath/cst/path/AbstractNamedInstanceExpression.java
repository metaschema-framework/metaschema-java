/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A path expression that references a named instance.
 *
 * @param <RESULT_TYPE>
 *          the Java type of the referenced node item
 */
public abstract class AbstractNamedInstanceExpression<RESULT_TYPE extends INodeItem>
    extends AbstractPathExpression<RESULT_TYPE> {
  @NonNull
  private final INodeTestExpression test;

  /**
   * Construct a new expression that finds children that match the provided
   * {@code test} expression.
   *
   * @param test
   *          the expression to use to determine a match
   */
  public AbstractNamedInstanceExpression(@NonNull INodeTestExpression test) {
    this.test = test;
  }

  /**
   * Get the {@link WildcardNodeTest} or {@link NameNodeTest} test.
   *
   * @return the test
   */
  @NonNull
  public INodeTestExpression getTest() {
    return test;
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of(test);
  }

  @Override
  public ISequence<? extends RESULT_TYPE> accept(
      DynamicContext dynamicContext,
      ISequence<?> focus) {
    return ISequence.of(ObjectUtils.notNull(focus.stream()
        .map(ItemUtils::checkItemIsNodeItemForStep)
        .flatMap(item -> {
          assert item != null;
          return match(item);
        })));
  }

  /**
   * Get a stream of matching child node items for the provided {@code context}.
   *
   * @param focusedItem
   *          the node item to match child items of
   * @return the stream of matching node items
   */
  @NonNull
  protected Stream<? extends RESULT_TYPE> match(@NonNull INodeItem focusedItem) {
    Stream<? extends RESULT_TYPE> retval;

    INodeTestExpression test = getTest();
    // FIXME: Are kind tests missing here?
    if (test instanceof NameNodeTest) {
      IEnhancedQName name = ((NameNodeTest) getTest()).getName();
      retval = getFocusedChildrenWithName(focusedItem, name);
    } else if (test instanceof WildcardNodeTest) {
      // match all items
      retval = ((WildcardNodeTest) test).matchStream(getFocusedChildren(focusedItem));
    } else {
      throw new UnsupportedOperationException(test.getClass().getName());
    }
    return retval;
  }

  @NonNull
  protected abstract Stream<? extends RESULT_TYPE> getFocusedChildrenWithName(
      @NonNull INodeItem focusedItem,
      @NonNull IEnhancedQName name);

  @NonNull
  protected abstract Stream<? extends RESULT_TYPE> getFocusedChildren(@NonNull INodeItem focusedItem);
}
