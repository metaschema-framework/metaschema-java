/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public class StaticFunctionCall implements IExpression {
  @NonNull
  private final List<IExpression> arguments;
  @NonNull
  private final Lazy<IFunction> functionSupplier;

  /**
   * Construct a new function call expression.
   *
   * @param functionSupplier
   *          the function implementation supplier
   * @param arguments
   *          the expressions used to provide arguments to the function call
   */
  public StaticFunctionCall(@NonNull Supplier<IFunction> functionSupplier, @NonNull List<IExpression> arguments) {
    this.arguments = arguments;
    this.functionSupplier = ObjectUtils.notNull(Lazy.lazy(functionSupplier));
  }

  /**
   * Retrieve the associated function.
   *
   * @return the function or {@code null} if no function matched the defined name
   *         and arguments
   * @throws StaticMetapathException
   *           if the function was not found
   */
  public IFunction getFunction() {
    return ObjectUtils.notNull(functionSupplier.get());
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
    return String.format("%s[name=%s]", getClass().getName(), getFunction().getQName());
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitFunctionCall(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    List<ISequence<?>> arguments = ObjectUtils.notNull(getChildren().stream()
        .map(expression -> expression.accept(dynamicContext, focus)).collect(Collectors.toList()));

    IFunction function = getFunction();
    return function.execute(arguments, dynamicContext, focus);
  }
}
