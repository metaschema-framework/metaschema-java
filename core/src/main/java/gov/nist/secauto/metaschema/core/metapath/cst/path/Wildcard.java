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
  public Wildcard(@Nullable Predicate<IDefinitionNodeItem<?, ?>> matcher) {
    this.matcher = matcher;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitWildcard(this, context);
  }

  @Override
  public ISequence<? extends INodeItem> accept(
      DynamicContext dynamicContext, ISequence<?> focus) {
    Stream<? extends INodeItem> nodes = focus.stream().map(ItemUtils::checkItemIsNodeItemForStep);
    if (matcher != null) {
      Predicate<IDefinitionNodeItem<?, ?>> test = matcher;
      nodes = nodes.filter(item -> {
        assert matcher != null;
        return !(item instanceof IDefinitionNodeItem) ||
            test.test((IDefinitionNodeItem<?, ?>) item);
      });
    }
    return ISequence.of(ObjectUtils.notNull(nodes));
  }

  /**
   * A wildcard matcher that matches a specific local name in any namespace.
   */
  public static class MatchAnyNamespace implements Predicate<IDefinitionNodeItem<?, ?>> {
    @NonNull
    private final String localName;

    /**
     * Construct the matcher using the provided local name for matching.
     *
     * @param localName
     *          the name used to match nodes
     */
    public MatchAnyNamespace(@NonNull String localName) {
      this.localName = localName;
    }

    @Override
    public boolean test(IDefinitionNodeItem<?, ?> item) {
      return localName.equals(item.getQName().getLocalPart());
    }
  }

  /**
   * A wildcard matcher that matches any local name in a specific namespace.
   */
  public static class MatchAnyLocalName implements Predicate<IDefinitionNodeItem<?, ?>> {
    @NonNull
    private final String namespace;

    /**
     * Construct the matcher using the provided namespace for matching.
     *
     * @param namespace
     *          the namespace used to match nodes
     */
    public MatchAnyLocalName(@NonNull String namespace) {
      this.namespace = namespace;
    }

    @Override
    public boolean test(IDefinitionNodeItem<?, ?> item) {
      return namespace.equals(item.getQName().getNamespaceURI());
    }
  }
}
