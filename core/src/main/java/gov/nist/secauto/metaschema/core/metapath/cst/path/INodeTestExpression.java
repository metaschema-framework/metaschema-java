/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A Metapath path expression that tests a node based on a set of conditions.
 * <p>
 * Based on the XPath 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#node-tests">node test</a> syntax.
 */
public interface INodeTestExpression extends IExpression {
  @SuppressWarnings("null")
  @Override
  default List<? extends IExpression> getChildren() {
    return Collections.emptyList();
  }

  @Override
  default Class<INodeItem> getBaseResultType() {
    return INodeItem.class;
  }

  @Override
  default Class<INodeItem> getStaticResultType() {
    return getBaseResultType();
  }

  /**
   * Check the provided stream of items to determine if each item matches this
   * test. All items that match are returned.
   * <p>
   * This is an intermediate stream operation.
   *
   * @param <T>
   *          the item Java type
   * @param stream
   *          the items to check if they match
   * @return the matching items
   */
  @NonNull
  default <T extends INodeItem> Stream<T> filterStream(@NonNull Stream<T> stream) {
    return ObjectUtils.notNull(stream.filter(this::match));
  }

  /**
   * Check the provided item to determine if it matches this test.
   *
   * @param item
   *          the item to check for a match
   * @return {@code true} if the item matches or {@code false} otherwise
   */
  boolean match(@NonNull INodeItem item);
}
