/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * An implementation of the
 * <a href="https://www.w3.org/TR/xpath-31/#id-array-constructors">Array Curly
 * Constructor</a> supporting the creation of a Metapath {@link IArrayItem}.
 */
public class ArraySequenceConstructor implements IExpression {
  @Nullable
  private final IExpression expr;

  /**
   * Construct a new array constructor expression that uses the provided
   * expression to initialize the array.
   *
   * @param expression
   *          the expression used to produce the array members
   */
  public ArraySequenceConstructor(@Nullable IExpression expression) {
    this.expr = expression;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Class<IArrayItem> getBaseResultType() {
    return IArrayItem.class;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Class<IArrayItem> getStaticResultType() {
    return IArrayItem.class;
  }

  @SuppressWarnings("null")
  @Override
  public List<? extends IExpression> getChildren() {
    return List.of(expr);
  }

  @Override
  public ISequence<IArrayItem<?>> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    ISequence<IArrayItem<?>> retval;
    if (expr != null) {
      IArrayItem<?> array = IArrayItem.ofCollection(expr.accept(dynamicContext, focus));
      retval = ISequence.of(array);
    } else {
      retval = ISequence.of();
    }
    return retval;
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitArray(this, context);
  }
}
