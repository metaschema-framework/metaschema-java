/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the XPATH 3.1
 * <a href="https://www.w3.org/TR/xpath-31/#id-eval-function-call">function call
 * accessor</a>.
 */
public class FunctionCallAccessor
    extends AbstractExpression {
  @NonNull
  private final IExpression base;
  @NonNull
  private final List<IExpression> arguments;

  /**
   * Construct a new functional call accessor.
   *
   * @param text
   *          the parsed text of the expression
   * @param base
   *          the expression whose result is used as the map or array to perform
   *          the lookup on
   * @param arguments
   *          the function call argument expressions
   */
  public FunctionCallAccessor(@NonNull String text, @NonNull IExpression base, @NonNull List<IExpression> arguments) {
    super(text);
    this.base = base;
    this.arguments = arguments;
  }

  /**
   * Get the base sub-expression.
   *
   * @return the sub-expression
   */
  @NonNull
  public IExpression getBase() {
    return base;
  }

  /**
   * Retrieve the argument to use for the lookup.
   *
   * @return the argument
   */
  @NonNull
  public List<IExpression> getArguments() {
    return arguments;
  }

  @SuppressWarnings("null")
  @Override
  public List<IExpression> getChildren() {
    return Stream.concat(Stream.of(getBase()), getArguments().stream())
        .collect(Collectors.toUnmodifiableList());
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<?> target = getBase().accept(dynamicContext, focus);
    IItem collection = target.getFirstItem(true);

    if (!(collection instanceof IFunction)) {
      throw new InvalidTypeMetapathException(collection, "The base expression did not evaluate to a function.");
    }

    return ((IFunction) collection).execute(ObjectUtils.notNull(getArguments().stream()
        .map(expr -> expr.accept(dynamicContext, focus))
        .collect(Collectors.toUnmodifiableList())), dynamicContext, focus);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(@NonNull IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitFunctionCallAccessor(this, context);
  }
}
