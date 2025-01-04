/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of
 * <a href="https://www.w3.org/TR/xpath-31/#id-let-expressions">Let
 * expression</a> supporting variable value binding.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Let
    extends AbstractExpression {
  @NonNull
  private final VariableDeclaration variable;
  @NonNull
  private final IExpression returnExpression;

  /**
   * Construct a new Let CST expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param name
   *          the variable name
   * @param boundExpression
   *          the expression bound to the variable
   * @param returnExpression
   *          the inner expression to evaluate with the variable in-scope
   */
  public Let(
      @NonNull String text,
      @NonNull IEnhancedQName name,
      @NonNull IExpression boundExpression,
      @NonNull IExpression returnExpression) {
    super(text);
    this.variable = new VariableDeclaration(name, boundExpression);
    this.returnExpression = returnExpression;
  }

  /**
   * Get the variable to evaluate with the variable in-scope.
   *
   * @return the inner expression
   */
  @NonNull
  public VariableDeclaration getVariable() {
    return variable;
  }

  /**
   * Get the inner expression to evaluate with the variable in-scope.
   *
   * @return the inner expression
   */
  @NonNull
  public IExpression getReturnExpression() {
    return returnExpression;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return ObjectUtils.notNull(
        List.of(returnExpression));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitLet(this, context);
  }

  @Override
  protected ISequence<?> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    DynamicContext subDynamicContext = dynamicContext.subContext();

    getVariable().bind(dynamicContext, focus, subDynamicContext);

    return getReturnExpression().accept(subDynamicContext, focus);
  }

  /**
   * A Metapath expression that binds a variable name to an expresssion.
   */
  public static class VariableDeclaration {
    @NonNull
    private final IEnhancedQName name;
    @NonNull
    private final IExpression boundExpression;

    /**
     * Construct a new variable declaration, binding the provided variable name to
     * the bound expression.
     *
     * @param name
     *          trhe variable name
     * @param boundExpression
     *          the bound expression
     */
    public VariableDeclaration(@NonNull IEnhancedQName name, @NonNull IExpression boundExpression) {
      this.name = name;
      this.boundExpression = boundExpression;
    }

    /**
     * Get the variable name.
     *
     * @return the variable name
     */
    @NonNull
    public IEnhancedQName getName() {
      return name;
    }

    /**
     * Get the expression bound to the variable.
     *
     * @return the bound expression
     */
    @NonNull
    public IExpression getBoundExpression() {
      return boundExpression;
    }

    /**
     * Bind the variable name to the evaluation result of the bound expression.
     *
     * @param evaluationDynamicContext
     *          the {@link DynamicContext} used to evaluate the bound expression
     * @param focus
     *          the evaluation focus to use to evaluate the bound expression
     * @param boundDynamicContext
     *          the {@link DynamicContext} the variable is bound to
     */
    public void bind(
        @NonNull DynamicContext evaluationDynamicContext,
        @NonNull ISequence<?> focus,
        @NonNull DynamicContext boundDynamicContext) {

      ISequence<?> result = getBoundExpression().accept(evaluationDynamicContext, focus)
          // ensure this sequence is list backed
          .reusable();
      boundDynamicContext.bindVariableValue(getName(), result);
    }
  }
}
