/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst.logic;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.cst.AbstractNAryExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnBoolean;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IBooleanItem;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of
 * <a href="https://www.w3.org/TR/xpath-31/#id-logical-expressions">And
 * expression</a> supporting conditional evaluation.
 * <p>
 * Determines the logical conjunction of the result of evaluating a list of
 * expressions. The boolean result of each expression is determined by applying
 * {@link FnBoolean#fnBooleanAsPrimitive(ISequence)} to each function's
 * {@link ISequence} result.
 * <p>
 * This implementation will short-circuit and return {@code false} when the
 * first expression evaluates to {@code false}, otherwise it will return
 * {@code true}.
 */
public class And // NOPMD - intentional name
    extends AbstractNAryExpression
    implements IBooleanLogicExpression {

  /**
   * Construct a new "and" logical expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param expressions
   *          the expressions to evaluate
   *
   */
  public And(@NonNull String text, @NonNull List<IExpression> expressions) {
    super(text, expressions);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitAnd(this, context);
  }

  @Override
  public ISequence<? extends IBooleanItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    boolean retval = true;
    for (IExpression child : getChildren()) {
      ISequence<?> result = child.accept(dynamicContext, focus);
      if (!FnBoolean.fnBooleanAsPrimitive(result)) {
        retval = false;
        break;
      }
    }
    return ISequence.of(IBooleanItem.valueOf(retval));
  }
}
