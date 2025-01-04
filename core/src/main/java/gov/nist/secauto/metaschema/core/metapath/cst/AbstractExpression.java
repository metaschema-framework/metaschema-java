/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.IExpression;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A common base class for Metapath expression implementations, providing common
 * utility functions.
 */
public abstract class AbstractExpression implements IExpression {
  @NonNull
  private final String text;

  /**
   * Construct a new expression.
   *
   * @param text
   *          the parsed text of the expression
   */
  public AbstractExpression(@NonNull String text) {
    this.text = text;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return CSTPrinter.toString(this);
  }

  @Override
  public ISequence<?> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    dynamicContext.pushExecutionStack(this);
    try {
      return evaluate(dynamicContext, focus);
    } finally {
      dynamicContext.popExecutionStack(this);
    }
  }

  /**
   * Evaluate this expression, producing a sequence result.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @param focus
   *          the outer focus of the expression
   * @return the result of evaluation
   */
  @NonNull
  protected abstract ISequence<? extends IItem> evaluate(
      @NonNull DynamicContext dynamicContext,
      @NonNull ISequence<?> focus);
}
