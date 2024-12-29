/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.function.IFunction;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-named-function-ref">variable
 * reference</a>.
 */
public class NamedFunctionReference implements IExpression {
  @NonNull
  private final IEnhancedQName name;
  private final int arity;

  /**
   * Construct a new Metapath variable reference CST node.
   *
   * @param name
   *          the function name
   * @param arity
   *          the number of function arguments
   */
  public NamedFunctionReference(@NonNull IEnhancedQName name, int arity) {
    this.name = name;
    this.arity = arity;
  }

  /**
   * Get the variable name.
   *
   * @return the name of the referenced function
   */
  @NonNull
  public IEnhancedQName getName() {
    return name;
  }

  /**
   * Get the expected number of function arguments for this lookup.
   *
   * @return the number of arguments
   */
  public int getArity() {
    return arity;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return CollectionUtil.emptyList();
  }

  @SuppressWarnings("null")
  @Override
  public String toASTString() {
    return String.format("%s[name=%s, arity=%d]", getClass().getName(), name, arity);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitNamedFunctionReference(this, context);
  }

  @Override
  public ISequence<IFunction> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    IFunction function = dynamicContext.getFunction(name, arity);
    return ISequence.of(function);
  }
}
