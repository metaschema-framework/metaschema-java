/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractNamedInstanceExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.IFlagNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

public class Flag // NOPMD - intentional name
    extends AbstractNamedInstanceExpression<IFlagNodeItem> {

  /**
   * Construct a new expression that finds any child {@link IFlagNodeItem} that
   * matches the provided {@code test}.
   *
   * @param test
   *          the test to use to match
   */
  public Flag(@NonNull INodeTestExpression test) {
    super(test);
  }

  @Override
  public Class<IFlagNodeItem> getBaseResultType() {
    return IFlagNodeItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitFlag(this, context);
  }

  @Override
  public ISequence<? extends IFlagNodeItem> accept(
      DynamicContext dynamicContext,
      ISequence<?> focus) {
    return ISequence.of(ObjectUtils.notNull(focus.stream()
        .map(ItemUtils::checkItemIsNodeItem)
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
  @SuppressWarnings("null")
  @NonNull
  protected Stream<? extends IFlagNodeItem> match(@NonNull INodeItem focusedItem) {
    Stream<? extends IFlagNodeItem> retval;

    INodeTestExpression test = getTest();
    if (test instanceof NameTest) {
      IEnhancedQName name = ((NameTest) getTest()).getName();

      IFlagNodeItem item = focusedItem.getFlagByName(name);
      retval = item == null ? Stream.empty() : Stream.of(item);
    } else if (test instanceof Wildcard) {
      // match all items
      retval = ((Wildcard) test).match(focusedItem.flags());
    } else {
      throw new UnsupportedOperationException(test.getClass().getName());
    }
    return retval;
  }
}
