/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionService;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public class StaticFunctionCall implements IExpression {
  @NonNull
  private final List<IExpression> arguments;
  private final Lazy<IFunction> function;

  /**
   * Construct a new function call expression.
   *
   * @param name
   *          the function name
   * @param arguments
   *          the expressions used to provide arguments to the function call
   */
  public StaticFunctionCall(@NonNull QName name, @NonNull List<IExpression> arguments) {
    this.arguments = Objects.requireNonNull(arguments, "arguments");
    this.function = Lazy.lazy(() -> FunctionService.getInstance().getFunction(
        Objects.requireNonNull(name, "name"),
        arguments.size()));
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
    return function.get();
  }

  @Override
  public List<IExpression> getChildren() {
    return arguments;
  }

  @Override
  public Class<? extends IItem> getBaseResultType() {
    Class<? extends IItem> retval = getFunction().getResult().getType();
    if (retval == null) {
      retval = IItem.class;
    }
    return retval;
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
    List<ISequence<?>> arguments = ObjectUtils.notNull(getChildren().stream().map(expression -> {
      @NonNull ISequence<?> result = expression.accept(dynamicContext, focus);
      return result;
    }).collect(Collectors.toList()));

    IFunction function = getFunction();
    return function.execute(arguments, dynamicContext, focus);
  }
}
