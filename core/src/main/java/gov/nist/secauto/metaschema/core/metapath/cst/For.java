/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.Let.VariableDeclaration;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.LinkedList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the
 * <a href="https://www.w3.org/TR/xpath-31/#id-for-expressions">For
 * expression</a> supporting variable-based iteration.
 */
@SuppressWarnings("PMD.ShortClassName")
public class For
    extends AbstractExpression {
  @NonNull
  private final Let.VariableDeclaration variable;
  @NonNull
  private final IExpression returnExpression;

  /**
   * Construct a new for expression using the provided variable and return clause.
   *
   * @param text
   *          the parsed text of the expression
   * @param variable
   *          the variable declaration
   * @param returnExpr
   *          the return clause that makes use of variables for evaluation
   */
  public For(
      @NonNull String text,
      @NonNull VariableDeclaration variable,
      @NonNull IExpression returnExpr) {
    super(text);
    this.variable = variable;
    this.returnExpression = returnExpr;
  }

  /**
   * Get the variable declaration.
   *
   * @return the variable declaration expression
   */
  @NonNull
  protected Let.VariableDeclaration getVariable() {
    return variable;
  }

  /**
   * Get the return expression.
   *
   * @return the return expression
   */
  @NonNull
  protected IExpression getReturnExpression() {
    return returnExpression;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return ObjectUtils.notNull(
        List.of(getVariable().getBoundExpression(), returnExpression));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitFor(this, context);
  }

  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    Let.VariableDeclaration variable = getVariable();
    ISequence<?> variableResult = variable.getBoundExpression().accept(dynamicContext, focus);

    DynamicContext subDynamicContext = dynamicContext.subContext();

    List<IItem> retval = new LinkedList<>();
    for (IItem item : variableResult) {
      subDynamicContext.bindVariableValue(variable.getName(), ISequence.of(item));
      retval.addAll(getReturnExpression().accept(subDynamicContext, focus));
    }
    return ISequence.ofCollection(retval);
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[variable=%s]", getClass().getName(), getVariable().getName());
  }
}
