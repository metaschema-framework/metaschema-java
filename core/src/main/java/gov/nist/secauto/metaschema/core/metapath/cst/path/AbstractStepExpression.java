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
 * A Metapath path expression that references a named instance.
 * <p>
 * Based on the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-steps">step</a> syntax.
 *
 * @param <RESULT_TYPE>
 *          the Java type of the referenced node item
 */
public abstract class AbstractStepExpression<RESULT_TYPE extends INodeItem>
    extends AbstractPathExpression<RESULT_TYPE> {
  @NonNull
  private final INodeTestExpression test;

  /**
   * Construct a new expression that finds children that match the provided
   * {@code test} expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param test
   *          the expression to use to determine a match
   */
  public AbstractStepExpression(@NonNull String text, @NonNull INodeTestExpression test) {
    super(text);
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
   * Get a stream of matching child node items for the provided {@code focus}.
   *
   * @param focus
   *          the node item to get child items for
   * @return the stream of matching node items
   */
  @NonNull
  protected Stream<? extends RESULT_TYPE> match(@NonNull INodeItem focus) {
    Stream<? extends RESULT_TYPE> retval;

    INodeTestExpression test = getTest();
    if (test instanceof NameNodeTest) {
      IEnhancedQName name = ((NameNodeTest) getTest()).getName();
      retval = getChildNodesWithName(focus, name);
    } else {
      // match all items
      retval = test.filterStream(getChildNodes(focus));
    }
    return retval;
  }

  /**
   * Get a stream of child node items for the provided {@code focus} that match
   * the provided {@code name}.
   *
   * @param focus
   *          the node item to get child items for
   * @param name
   *          the qualified name used to match child items
   * @return the matching child items
   */
  @NonNull
  protected abstract Stream<? extends RESULT_TYPE> getChildNodesWithName(
      @NonNull INodeItem focus,
      @NonNull IEnhancedQName name);

  /**
   * Get a stream of child node items for the provided {@code focus}.
   *
   * @param focus
   *          the node item to get child items for
   * @return the child items
   */
  @NonNull
  protected abstract Stream<? extends RESULT_TYPE> getChildNodes(@NonNull INodeItem focus);
}
