/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.metapath.cst;

import java.util.List;

import dev.metaschema.core.metapath.DynamicContext;
import dev.metaschema.core.metapath.IExpression;
import dev.metaschema.core.metapath.function.IFunction;
import dev.metaschema.core.metapath.item.ISequence;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.CollectionUtil;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The CST node for a Metapath
 * <a href="https://www.w3.org/TR/xpath-31/#id-named-function-ref">named
 * function reference</a>.
 */
public class NamedFunctionReference
    extends AbstractExpression {
  @NonNull
  private final IEnhancedQName name;
  private final int arity;

  /**
   * Construct a new Metapath named function reference CST node.
   *
   * @param text
   *          the parsed text of the expression
   * @param name
   *          the function name
   * @param arity
   *          the number of function arguments
   */
  public NamedFunctionReference(
      @NonNull String text,
      @NonNull IEnhancedQName name, int arity) {
    super(text);
    this.name = name;
    this.arity = arity;
  }

  /**
   * Get the function name.
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
  public String toCSTString() {
    return String.format("%s[name=%s, arity=%d]", getClass().getName(), name, arity);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitNamedFunctionReference(this, context);
  }

  @Override
  protected ISequence<?> evaluate(DynamicContext dynamicContext, ISequence<?> focus) {
    IFunction function = dynamicContext.lookupFunction(name, arity);
    return ISequence.of(function);
  }
}
