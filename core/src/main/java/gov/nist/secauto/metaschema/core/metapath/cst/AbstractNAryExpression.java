/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An immutable expression that has a number of sub-expression children.
 */
public abstract class AbstractNAryExpression
    extends AbstractExpression {
  @NonNull
  private final List<IExpression> children;

  /**
   * Construct a new n-ary expression.
   *
   * @param children
   *          the sub-expression children
   */
  public AbstractNAryExpression(@NonNull List<IExpression> children) {
    this.children = Objects.requireNonNull(children);
  }

  @Override
  public List<IExpression> getChildren() {
    return children;
  }
}
