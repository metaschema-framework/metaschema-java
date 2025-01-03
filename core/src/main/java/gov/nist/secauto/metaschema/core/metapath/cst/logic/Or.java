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

import java.util.Arrays;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * An implementation of
 * <a href="https://www.w3.org/TR/xpath-31/#id-logical-expressions">Or
 * expression</a> supporting conditional evaluation.
 * <p>
 * Determines the logical conjunction of the result of evaluating a list of
 * expressions. The boolean result of each expression is determined by applying
 * {@link FnBoolean#fnBooleanAsPrimitive(ISequence)} to each function's
 * {@link ISequence} result.
 * <p>
 * This implementation will short-circuit and return {@code true} when the first
 * expression evaluates to {@code true}, otherwise it will return {@code false}.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Or
    extends AbstractNAryExpression
    implements IBooleanLogicExpression {

  /**
   * Construct a new "or" logical expression.
   *
   * @param text
   *          the parsed text of the expression
   * @param expressions
   *          the expressions to evaluate
   *
   */
  @SuppressWarnings("null")
  public Or(@NonNull String text, @NonNull IExpression... expressions) {
    this(text, Arrays.asList(expressions));
  }

  /**
   * Determines the logical disjunction of the result of evaluating a list of
   * expressions. The boolean result of each expression is determined by applying
   * {@link FnBoolean#fnBooleanAsPrimitive(ISequence)} to each function's
   * {@link ISequence} result.
   *
   * @param text
   *          the parsed text of the expression
   * @param expressions
   *          the list of expressions
   */
  public Or(@NonNull String text, @NonNull List<IExpression> expressions) {
    super(text, expressions);
  }

  @Override
  public <RESULT, CONTEXT> RESULT accept(IExpressionVisitor<RESULT, CONTEXT> visitor, CONTEXT context) {
    return visitor.visitOr(this, context);
  }

  @Override
  public ISequence<? extends IBooleanItem> accept(DynamicContext dynamicContext, ISequence<?> focus) {
    boolean retval = false;
    for (IExpression child : getChildren()) {
      ISequence<?> result = child.accept(dynamicContext, focus);
      if (FnBoolean.fnBooleanAsPrimitive(result)) {
        retval = true;
        break;
      }
    }
    return ISequence.of(IBooleanItem.valueOf(retval));
  }
}
