/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.ICycledAssemblyNodeItem;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A base class for Metapath expressions based on the XPath 3.1 <a href=
 * "https://www.w3.org/TR/xpath-31/#id-relative-path-expressions">relative path
 * expressions</a> that entail searching.
 */
public abstract class AbstractSearchPathExpression
    extends AbstractPathExpression<INodeItem> {
  @NonNull
  private final Class<? extends INodeItem> staticResultType;

  /**
   * Construct a new relative path expression, used for searching.
   *
   * @param text
   *          the parsed text of the expression
   * @param staticResultType
   *          the static result type
   */
  public AbstractSearchPathExpression(
      @NonNull String text,
      @NonNull Class<? extends INodeItem> staticResultType) {
    super(text);
    this.staticResultType = staticResultType;
  }

  @Override
  public final Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  public Class<? extends INodeItem> getStaticResultType() {
    return staticResultType;
  }

  /**
   * Evaluate the {@code nodeContext} and its ancestors against the provided
   * {@code expression}, keeping any matching nodes.
   *
   * @param expression
   *          the expression to evaluate
   * @param dynamicContext
   *          the evaluation context
   * @param outerFocus
   *          the current context node
   * @return the matching nodes
   */
  @NonNull
  protected Stream<? extends INodeItem> search(
      @NonNull IExpression expression,
      @NonNull DynamicContext dynamicContext,
      @NonNull ISequence<?> outerFocus) {
    // ensure the sequence is backed by a list
    ISequence<?> focus = outerFocus.reusable();

    // check the current focus
    @SuppressWarnings("unchecked")
    Stream<? extends INodeItem> nodeMatches
        = (Stream<? extends INodeItem>) expression.accept(dynamicContext, focus).stream();

    Stream<? extends INodeItem> childMatches = focus.stream()
        .map(ItemUtils::checkItemIsNodeItemForStep)
        .flatMap(focusedNode -> {

          Stream<? extends INodeItem> matches;
          if (focusedNode instanceof ICycledAssemblyNodeItem) {
            // prevent stack overflow
            matches = Stream.empty();
          } else {
            assert focusedNode != null; // may be null?
            // create a stream of flags and model elements to check
            Stream<? extends INodeItem> flags = focusedNode.flags();
            Stream<? extends INodeItem> modelItems = focusedNode.modelItems();

            matches = search(
                expression,
                dynamicContext,
                ISequence.of(ObjectUtils.notNull(Stream.concat(flags, modelItems))));
          }
          return matches;
        });

    return ObjectUtils.notNull(Stream.concat(nodeMatches, childMatches).distinct());
  }
}
