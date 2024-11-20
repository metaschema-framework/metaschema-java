/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.function.Predicate;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#doc-xpath31-Wildcard">wildcard name
 * test</a>.
 */
public class Wildcard implements INameTestExpression {
  @Nullable
  private final Predicate<IDefinitionNodeItem<?, ?>> matcher;

  /**
   * Construct a new wildcard name test expression using the provided matcher.
   *
   * @param matcher
   *          the matcher used to determine matching nodes
   */
  public Wildcard(@Nullable IWildcardMatcher matcher) {
    this.matcher = matcher;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitWildcard(this, context);
  }

  @Override
  public ISequence<? extends INodeItem> accept(
      DynamicContext dynamicContext, ISequence<?> focus) {
    Stream<? extends INodeItem> nodes = ObjectUtils.notNull(focus.stream().map(ItemUtils::checkItemIsNodeItemForStep));
    return ISequence.of(match(nodes));
  }

  /**
   * Check the provided items to determine if each item matches the wildcard. All
   * items that match are returned.
   * <p>
   * This is an intermediate stream operation.
   *
   * @param <T>
   *          the item Java type
   * @param items
   *          the items to check if they match
   * @return the matching items
   */
  @NonNull
  public <T extends INodeItem> Stream<T> match(@SuppressWarnings("resource") @NonNull Stream<T> items) {
    Stream<T> nodes = items;
    if (matcher != null) {
      Predicate<IDefinitionNodeItem<?, ?>> test = matcher;
      nodes = ObjectUtils.notNull(nodes.filter(item -> {
        assert matcher != null;
        return !(item instanceof IDefinitionNodeItem) ||
            test.test((IDefinitionNodeItem<?, ?>) item);
      }));
    }
    return nodes;
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[%s]", getClass().getName(), matcher == null ? "*:*" : matcher.toString());
  }

}
