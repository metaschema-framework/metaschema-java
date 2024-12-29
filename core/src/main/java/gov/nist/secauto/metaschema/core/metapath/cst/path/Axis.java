/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathError;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of <a href="https://www.w3.org/TR/xpath-31/#axes">Metapath
 * axes</a>.
 */
@SuppressWarnings("PMD.ShortClassName")
public enum Axis implements IExpression {
  /**
   * The {@code self::} axis, referring to the current context node.
   */
  SELF(Stream::of),
  /**
   * The {@code parent::} axis, referring to the current context node's parent.
   *
   * @see INodeItem#getParentNodeItem()
   */
  PARENT(focus -> Stream.ofNullable(focus.getParentNodeItem())),
  /**
   * The {@code flag::} axis, referring to the current context node's flags.
   *
   * @see INodeItem#getFlags()
   */
  FLAG(INodeItem::flags),
  /**
   * The {@code ancestor::} axis, referring to the current context node's
   * parentage.
   *
   * @see INodeItem#ancestor()
   */
  ANCESTOR(INodeItem::ancestor),
  /**
   * The {@code ancestor-or-self::} axis, referring to the current context node
   * and its parentage.
   *
   * @see INodeItem#ancestorOrSelf()
   */
  ANCESTOR_OR_SELF(INodeItem::ancestorOrSelf),
  /**
   * The {@code children::} axis, referring to the current context node's direct
   * children.
   *
   * @see INodeItem#modelItems()
   */
  CHILDREN(INodeItem::modelItems),
  /**
   * The {@code descendant::} axis, referring to all of the current context node's
   * descendants (i.e., the children, the children of the children, etc).
   *
   * @see INodeItem#descendant()
   */
  DESCENDANT(INodeItem::descendant),
  /**
   * The {@code descendant-or-self::} axis, referring to the current context node
   * and all of the current context node's descendants (i.e., the children, the
   * children of the children, etc).
   *
   * @see INodeItem#descendantOrSelf()
   */
  DESCENDANT_OR_SELF(INodeItem::descendantOrSelf),
  /**
   * The {@code following-sibling::} axis, referring to those children of the
   * context node's parent that occur after the context node in
   * <a href="https://www.w3.org/TR/xpath-31/#dt-document-order">document
   * order</a>.
   */
  FOLLOWING_SIBLING(INodeItem::followingSibling),
  /**
   * The {@code preceding-sibling::} axis, referring to those children of the
   * context node's parent that occur before the context node in
   * <a href="https://www.w3.org/TR/xpath-31/#dt-document-order">document
   * order</a>.
   */
  PRECEDING_SIBLING(INodeItem::precedingSibling),
  /**
   * The {@code preceding-sibling::} axis, referring to all nodes that are
   * descendants of the root of the tree in which the context node is found, are
   * not descendants of the context node, and occur after the context node in
   * <a href="https://www.w3.org/TR/xpath-31/#dt-document-order">document
   * order</a>.
   */
  FOLLOWING(INodeItem::following),
  /**
   * The {@code preceding-sibling::} axis, referring to all nodes that are
   * descendants of the root of the tree in which the context node is found, are
   * not ancestors of the context node, and occur before the context node in
   * <a href="https://www.w3.org/TR/xpath-31/#dt-document-order">document
   * order</a>.
   */
  PRECEDING(INodeItem::preceding),
  /**
   * This axis is not supported.
   */
  NAMESPACE(focus -> {
    throw new StaticMetapathError(
        StaticMetapathError.AXIS_NAMESPACE_UNSUPPORTED,
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
          .map(ItemUtils::checkItemIsNodeItem)
          .flatMap(item -> {
            assert item != null;
            return execute(item);
          }).distinct()));
    }
    return retval;
  }
}
