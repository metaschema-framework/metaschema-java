/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.comparison;

import gov.nist.secauto.metaschema.core.metapath.cst.AbstractBinaryExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IBooleanLogicExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.function.ComparisonFunctions;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A common base class for all comparison nodes, which consist of two
 * expressions representing the left and right sides of the comparison, and a
 * comparison operator.
 */
public abstract class AbstractComparison // NOPMD - unavoidable
    extends AbstractBinaryExpression<IExpression, IExpression>
    implements IBooleanLogicExpression {

  @NonNull
  private final ComparisonFunctions.Operator operator;

  /**
   * Construct an expression that compares the result of the {@code right}
   * expression with the result of the {@code left} expression using the specified
   * {@code operator}.
   *
   * @param left
   *          the expression to compare against
   * @param operator
   *          the comparison operator
   * @param right
   *          the expression to compare with
   */
  public AbstractComparison(@NonNull IExpression left, @NonNull ComparisonFunctions.Operator operator,
      @NonNull IExpression right) {
    super(left, right);
    this.operator = ObjectUtils.requireNonNull(operator, "operator");
  }

  /**
   * Get the comparison operator.
   *
   * @return the operator
   */
  @NonNull
  public ComparisonFunctions.Operator getOperator() {
    return operator;
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[operator=%s]", getClass().getName(), operator);
  }

}
