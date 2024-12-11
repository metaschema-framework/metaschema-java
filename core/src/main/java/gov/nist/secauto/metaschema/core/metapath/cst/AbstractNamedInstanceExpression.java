/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.cst.path.AbstractPathExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.path.INodeTestExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.path.NameNodeTest;
import gov.nist.secauto.metaschema.core.metapath.cst.path.WildcardNodeTest;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

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
}
