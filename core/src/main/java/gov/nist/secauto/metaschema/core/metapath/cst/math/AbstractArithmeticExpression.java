/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.math;

import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractBinaryExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.ExpressionUtils;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An immutable binary expression that supports arithmetic evaluation.
 * <p>
 * The result type is determined through static analysis of the sub-expressions,
 * which may result in a more specific type that is a sub-class of the base
 * result type.
 *
 * @param <RESULT_TYPE>
 *          the base result type of the arithmetic evaluation, representing the
 *          atomic value produced by this expression
 */
public abstract class AbstractArithmeticExpression<RESULT_TYPE extends IAnyAtomicItem>
    extends AbstractBinaryExpression<IExpression, IExpression> {

  @NonNull
  private final Class<? extends RESULT_TYPE> staticResultType;

  /**
   * Construct a new arithmetic expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param left
   *          the left side of the arithmetic operation
   * @param right
   *          the right side of the arithmetic operation
   * @param baseType
   *          the base result type of the expression result
   */
  @SuppressWarnings("null")
  public AbstractArithmeticExpression(
      @NonNull String text,
      @NonNull IExpression left,
      @NonNull IExpression right,
      @NonNull Class<RESULT_TYPE> baseType) {
    super(text, left, right);
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(baseType, List.of(left, right));
  }

  @Override
  public abstract Class<RESULT_TYPE> getBaseResultType();

  @Override
  public Class<? extends RESULT_TYPE> getStaticResultType() {
    return staticResultType;
  }
}
