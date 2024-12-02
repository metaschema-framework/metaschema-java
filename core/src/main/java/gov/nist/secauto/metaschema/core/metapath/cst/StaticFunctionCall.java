/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathError;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Executes a function call based on the provided function and multiple argument
 * expressions that are used to determine the function arguments.
 * <p>
 * This class handles static function calls where the name of the function is
 * known during static analysis (the parsing phase), as opposed to dynamic or
 * anonymous function calls where the name is not available or known until
 * execution.
 * <p>
 * Static functions are resolved during the parsing phase and must exist in the
 * function registry.
 */
public class StaticFunctionCall implements IExpression {
  @NonNull
  private final IFunction function;
  @NonNull
  private final List<IExpression> arguments;

  /**
   * Construct a new function call expression.
   *
   * @param function
   *          the function implementation
   * @param arguments
   *          the expressions used to provide arguments to the function call
   */
  public StaticFunctionCall(@NonNull IFunction function, @NonNull List<IExpression> arguments) {
    this.function = function;
    this.arguments = arguments;
  }

  /**
   * Retrieve the associated function.
   *
   * @return the function or {@code null} if no function matched the defined name
   *         and arguments
   * @throws StaticMetapathError
   *           if the function was not found
   */
  @NonNull
  public IFunction getFunction() {
    return function;
  }

  @Override
  public List<IExpression> getChildren() {
    return arguments;
  }

  @Override
  public Class<? extends IItem> getBaseResultType() {
    return getFunction().getResult().getType().getItemClass();
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[name=%s, arity=%d]", getClass().getName(), getFunction().getQName(),
        getFunction().arity());
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitStaticFunctionCall(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    List<ISequence<?>> arguments = ObjectUtils.notNull(this.arguments.stream()
        .map(expression -> expression.accept(dynamicContext, focus).contentsAsSequence())
        .collect(Collectors.toList()));

    IFunction function = getFunction();
    return function.execute(arguments, dynamicContext, focus);
  }
}
