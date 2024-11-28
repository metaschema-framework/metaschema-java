/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Executes a function call based on a specifier expression that is used to
 * determine the function and multiple argument expressions that are used to
 * determine the function arguments.
 */
public class DynamicFunctionCall implements IExpression {
  @NonNull
  private final IExpression functionIdentifier;
  @NonNull
  private final List<IExpression> arguments;

  /**
   * Construct a new function call expression.
   *
   * @param functionIdentifier
   *          the function expression, identifying either a function or function
   *          name
   * @param arguments
   *          the expressions used to provide arguments to the function call
   */
  public DynamicFunctionCall(@NonNull IExpression functionIdentifier, @NonNull List<IExpression> arguments) {
    this.functionIdentifier = functionIdentifier;
    this.arguments = arguments;
  }

  @Override
  public List<IExpression> getChildren() {
    return ObjectUtils.notNull(Stream.concat(
        Stream.of(functionIdentifier),
        arguments.stream())
        .collect(Collectors.toUnmodifiableList()));
  }

  @Override
  public Class<? extends IItem> getBaseResultType() {
    return IItem.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitDynamicFunctionCall(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    List<ISequence<?>> arguments = ObjectUtils.notNull(this.arguments.stream()
        .map(expression -> expression.accept(dynamicContext, focus)).collect(Collectors.toList()));

    IItem specifier = functionIdentifier.accept(dynamicContext, focus).getFirstItem(true);
    IFunction function;
    if (specifier instanceof IFunction) {
      function = (IFunction) specifier;
    } else if (specifier != null) {
      function = dynamicContext.getStaticContext().lookupFunction(
          specifier.toAtomicItem().asString(),
          arguments.size());
    } else {
      throw new StaticMetapathException(
          StaticMetapathException.NO_FUNCTION_MATCH,
          "Unable to get function name. The error specifier is an empty sequence.");
    }
    return function.execute(arguments, dynamicContext, focus);
  }
}
