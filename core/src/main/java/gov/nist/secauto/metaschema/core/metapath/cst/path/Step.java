/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.path;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.ExpressionUtils;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An immutable expression that combines the evaluation of a sub-expression,
 * with the evaluation of a series of predicate expressions that filter the
 * result of the evaluation.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Step
    extends AbstractExpression {

  @NonNull
  private final Axis axisExpression;
  @NonNull
  private final INodeTestExpression stepExpression;
  @NonNull
  private final Class<? extends IItem> staticResultType;

  /**
   * Construct a new stepExpression expression.
   *
   * @param axis
   *          the axis to evaluate against
   * @param step
   *          the sub-expression to evaluate before filtering with the predicates
   */
  @SuppressWarnings("null")
  public Step(@NonNull Axis axis, @NonNull INodeTestExpression step) {
    this.axisExpression = axis;
    this.stepExpression = step;
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(IItem.class, List.of(step));
  }

  /**
   * Get the axis to use for the step.
   *
   * @return the step axis to use
   */
  @NonNull
  public Axis getAxis() {
    return axisExpression;
  }

  /**
   * Get the step expression's sub-expression.
   *
   * @return the sub-expression
   */
  @NonNull
  public INodeTestExpression getStep() {
    return stepExpression;
  }

  @Override
  public Class<? extends IItem> getStaticResultType() {
    return staticResultType;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return ObjectUtils.notNull(List.of(getAxis(), getStep()));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitStep(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<? extends INodeItem> axisResult = getAxis().accept(dynamicContext, focus);
    return getStep().accept(dynamicContext, axisResult);
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[axis=%s]", getClass().getName(), getAxis().name());
  }
}
