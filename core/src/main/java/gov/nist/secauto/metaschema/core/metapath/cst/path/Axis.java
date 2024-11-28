/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

@SuppressWarnings("PMD.ShortClassName") // intentional
public enum Axis implements IExpression {
  SELF(Stream::of),
  PARENT(focus -> Stream.ofNullable(focus.getParentNodeItem())),
  FLAG(INodeItem::flags),
  ANCESTOR(INodeItem::ancestor),
  ANCESTOR_OR_SELF(INodeItem::ancestorOrSelf),
  CHILDREN(INodeItem::modelItems),
  DESCENDANT(INodeItem::descendant),
  DESCENDANT_OR_SELF(INodeItem::descendantOrSelf),
  FOLLOWING_SIBLING(INodeItem::followingSibling),
  PRECEDING_SIBLING(INodeItem::precedingSibling),
  FOLLOWING(INodeItem::following),
  PRECEDING(INodeItem::preceding),
  NAMESPACE(focus -> {
    throw new StaticMetapathException(
        StaticMetapathException.AXIS_NAMESPACE_UNSUPPORTED,
        "The 'namespace' axis is not supported");
  });

  @NonNull
  private final Function<INodeItem, Stream<? extends INodeItem>> action;

  Axis(@NonNull Function<INodeItem, Stream<? extends INodeItem>> action) {
    this.action = action;
  }

  /**
   * Execute the axis operation on the provided {@code focus}.
   *
   * @param focus
   *          the node to operate on
   * @return the result of the axis operation
   */
  @NonNull
  public Stream<? extends INodeItem> execute(@NonNull INodeItem focus) {
    return ObjectUtils.notNull(action.apply(focus));
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return CollectionUtil.emptyList();
  }

  @Override
  public Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  public Class<INodeItem> getStaticResultType() {
    return getBaseResultType();
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitAxis(this, context);
  }

  @Override
  public ISequence<? extends INodeItem> accept(
      DynamicContext dynamicContext,
      ISequence<?> outerFocus) {
    ISequence<? extends INodeItem> retval;
    if (outerFocus.isEmpty()) {
      retval = ISequence.empty();
    } else {
      retval = ISequence.of(ObjectUtils.notNull(outerFocus.stream()
          .map(ItemUtils::checkItemIsNodeItemForStep)
          .flatMap(item -> {
            assert item != null;
            return execute(item);
          }).distinct()));
    }
    return retval;
  }
}
