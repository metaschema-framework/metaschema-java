/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst.path;

import java.util.function.Predicate;
import java.util.stream.Stream;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.cst.AbstractExpression;
import dev.metaschema.core.metapath.cst.IExpressionVisitor;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.metapath.item.ItemUtils;
import dev.metaschema.core.metapath.item.node.IDefinitionNodeItem;
import dev.metaschema.core.metapath.item.node.INodeItem;
import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#doc-xpath31-Wildcard">wildcard name
 * test</a>.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class WildcardNodeTest
    extends AbstractExpression
    implements INodeTestExpression {
  @Nullable
  private final Predicate<IDefinitionNodeItem<?, ?>> matcher;

  /**
   * Construct a new wildcard name test expression using the provided matcher.
   *
   * @param text
   *          the parsed text of the expression
   * @param matcher
   *          the matcher used to determine matching nodes
   */
  public WildcardNodeTest(@NonNull String text, @Nullable IWildcardMatcher matcher) {
    super(text);
    this.matcher = matcher;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitWildcardNodeTest(this, context);
  }

  @Override
  protected ISequence<? extends INodeItem> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    Stream<INodeItem> stream = focus.stream()
        .map(item -> ItemUtils.checkItemIsNodeItem(dynamicContext, item));

    if (matcher != null) {
      stream = stream.filter(this::match);
    }

    return ISequence.of(ObjectUtils.notNull(stream));
  }

  /**
   * {@inheritDoc}
   * <p>
   * If no matcher is provided, this method is a no-op.
   */
  @Override
  @NonNull
  public <T extends INodeItem> Stream<T> filterStream(@NonNull Stream<T> items) {
    Stream<T> nodes = items;
    if (matcher != null) {
      nodes = INodeTestExpression.super.filterStream(nodes);
    }
    return nodes;
  }

  /**
   * Check the provided item to determine if it matches the wildcard.
   *
   * @param item
   *          the item to check for a match
   * @return {@code true} if the item matches or {@code false} otherwise
   */
  @Override
  public boolean match(@NonNull INodeItem item) {
    assert matcher != null;
    Predicate<IDefinitionNodeItem<?, ?>> test = matcher;
    return !(item instanceof IDefinitionNodeItem) ||
        test.test((IDefinitionNodeItem<?, ?>) item);
  }

  @SuppressWarnings("null")
  @Override
  public String toCSTString() {
    return String.format("%s[%s]", getClass().getName(), matcher == null ? "*:*" : matcher.toString());
  }
}
