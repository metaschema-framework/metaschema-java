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
import gov.nist.secauto.metaschema.core.metapath.item.ItemUtils;
import gov.nist.secauto.metaschema.core.metapath.item.node.INodeItem;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

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
  @Nullable
  private final INodeTestExpression stepExpression;
  @NonNull
  private final Class<? extends IItem> staticResultType;

  /**
   * Construct a new step expression expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param axis
   *          the axis to evaluate against
   */
  public Step(@NonNull String text, @NonNull Axis axis) {
    this(text, axis, null);
  }

  /**
   * Construct a new step expression expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param axis
   *          the axis to evaluate against
   * @param step
   *          the optional sub-expression to evaluate before filtering with the
   *          predicates
   */
  public Step(@NonNull String text, @NonNull Axis axis, @Nullable INodeTestExpression step) {
    super(text);
    this.axisExpression = axis;
    this.stepExpression = step;
    this.staticResultType = ExpressionUtils.analyzeStaticResultType(IItem.class, step == null
        ? CollectionUtil.emptyList()
        : CollectionUtil.singletonList(step));
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
   * @return the sub-expression or {@code null} if there is no sub-expression
   */
  @Nullable
  public INodeTestExpression getStep() {
    return stepExpression;
  }

  @Override
  public Class<? extends IItem> getStaticResultType() {
    return staticResultType;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    IExpression step = getStep();
    return step == null ? CollectionUtil.emptyList() : CollectionUtil.singletonList(step);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitStep(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    Axis axis = getAxis();

    ISequence<? extends INodeItem> axisResult;
    if (focus.isEmpty()) {
      axisResult = ISequence.empty();
    } else {
      axisResult = ISequence.of(ObjectUtils.notNull(focus.stream()
          .map(ItemUtils::checkItemIsNodeItemForStep)
          .flatMap(item -> {
            assert item != null;
            return axis.execute(item);
          }).distinct()));
    }

    IExpression step = getStep();
    return step == null ? axisResult : step.accept(dynamicContext, axisResult);
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[axis=%s]",
        getClass().getName(),
        getAxis().name());
  }
}
