/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.function.impl.AbstractFunction;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Executes an unnamed function call based on a client provided Metapath
 * expression that is declared inline within a Metapath expression.
 */
public class AnonymousFunctionCall
    extends AbstractFunction
    implements IExpression, IFunction {
  @NonNull
  private static final Set<FunctionProperty> PROPERTIES = ObjectUtils.notNull(EnumSet.of(
      FunctionProperty.DETERMINISTIC));
  @NonNull
  private final ISequenceType result;
  @NonNull
  private final IExpression body;

  /**
   * Construct a new function call expression.
   *
   * @param arguments
   *          the parameter declarations for the function call
   * @param result
   *          the expected result of the function call
   * @param body
   *          the Metapath expression that implements the logic of the function
   */
  public AnonymousFunctionCall(
      @NonNull List<IArgument> arguments,
      @NonNull ISequenceType result,
      @NonNull IExpression body) {
    super("(anonymous)-" + UUID.randomUUID().toString(), "", arguments);
    this.result = result;
    this.body = body;
  }

  @Override
  public List<IExpression> getChildren() {
    return ObjectUtils.notNull(List.of(body));
  }

  @Override
  public Class<? extends IItem> getBaseResultType() {
    return IFunction.class;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitAnonymousFunctionCall(this, context);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.of(this);
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[arguments=%s,return=%s]",
        getClass().getName(), getName(),
        getArguments(),
        result.toSignature());
  }

  @Override
  public Set<FunctionProperty> getProperties() {
    return PROPERTIES;
  }

  @Override
  public ISequenceType getResult() {
    return result;
  }

  @Override
  @NonNull
  protected ISequence<?> executeInternal(
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      @Nullable IItem focus) {

    DynamicContext subContext = dynamicContext.subContext();
    if (arguments.size() != getArguments().size()) {
      throw new IllegalArgumentException("Number of arguments does not match the number of parameters.");
    }

    Iterator<? extends ISequence<?>> args = arguments.iterator();
    Iterator<IArgument> params = getArguments().iterator();
    while (args.hasNext() && params.hasNext()) {
      ISequence<?> sequence = args.next();
      IArgument param = params.next();

      subContext.bindVariableValue(param.getName(), ObjectUtils.notNull(sequence));
    }

    return body.accept(subContext, ISequence.of(focus));
  }
}
