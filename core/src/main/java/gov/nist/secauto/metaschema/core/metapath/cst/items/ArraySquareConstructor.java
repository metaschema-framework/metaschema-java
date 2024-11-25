/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.items;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IArrayItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of the
 * <a href="https://www.w3.org/TR/xpath-31/#id-array-constructors">Array Square
 * Constructor</a> supporting the creation of a Metapath {@link IArrayItem}.
 */
public class ArraySquareConstructor implements IExpression {
  @NonNull
  private final List<IExpression> children;

  /**
   * Construct a new array constructor expression that uses the provided
   * expression to initialize the array.
   * <p>
   * Each resulting array member contains the value of the corresponding argument
   * expression.
   *
   * @param children
   *          the expressions used to produce the array members
   */
  public ArraySquareConstructor(@NonNull List<IExpression> children) {
    this.children = children;
  }

  @Override
  public List<? extends IExpression> getChildren() {
    return children;
  }

  @Override
  public ISequence<? extends IItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    return ISequence.of(getChildren().stream()
        .map(expr -> expr.accept(dynamicContext, focus))
        .map(ISequence::toCollectionValue)
        .collect(IArrayItem.toArrayItem()));
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitArray(this, context);
  }
}
