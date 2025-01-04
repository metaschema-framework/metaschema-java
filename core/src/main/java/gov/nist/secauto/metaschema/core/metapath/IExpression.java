/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * The common interface of all Metapath expression nodes.
 * <p>
 * Metapath expression nodes represent the different types of expressions that
 * can appear in a Metapath query, forming a composite structure that can be
 * traversed and evaluated.
 *
 * @since 1.0.0
 * @see gov.nist.secauto.metaschema.core.metapath.cst.IExpressionVisitor
 */
public interface IExpression {
  /**
   * Get the text for the expression.
   *
   * @return the expression text
   */
  @NonNull
  String getText();

  /**
   * Retrieve the child expressions associated with this expression.
   *
   * @return a list of expressions, which may be empty
   */
  @NonNull
  List<? extends IExpression> getChildren();

  /**
   * The minimum expected result type to be produced when evaluating the
   * expression. The result may be a sub-class or sub-interface of this value.
   *
   * @return the base result type
   */
  @NonNull
  default Class<? extends IItem> getBaseResultType() {
    return IItem.class;
  }

  /**
   * The expected result type produced by evaluating the expression. The result
   * must be the same or a sub-class or sub-interface of the value provided by
   * {@link #getBaseResultType()}.
   * <p>
   * This method can be overloaded to provide static analysis of the expression to
   * determine a more specific result type.
   *
   * @return the result type
   */
  @NonNull
  default Class<? extends IItem> getStaticResultType() {
    return getBaseResultType();
  }

  /**
   * Produce a string representation of this expression including the expression's
   * name.
   * <p>
   * This method can be overloaded to provide a more appropriate representation of
   * the expression.
   *
   * @return a string representing the data elements of the expression
   */
  @SuppressWarnings("null")
  @NonNull
  default String toCSTString() {
    return String.format("%s[]", getClass().getName());
  }

  /**
   * Provides a double dispatch callback for visitor handling.
   *
   * @param dynamicContext
   *          the dynamic evaluation context
   * @param focus
   *          the outer focus of the expression
   * @return the result of evaluation
   */
  @NonNull
  ISequence<? extends IItem> accept(@NonNull DynamicContext dynamicContext, @NonNull ISequence<?> focus);

  /**
   * Provides a double dispatch callback for visitor handling.
   *
   * @param <RESULT>
   *          the type of the evaluation result
   * @param <CONTEXT>
   *          the type of the visitor context
   * @param visitor
   *          the visitor calling this method
   * @param context
   *          the visitor context
   * @return the result of evaluation
   */
  <RESULT, CONTEXT> RESULT accept(@NonNull IExpressionVisitor<RESULT, CONTEXT> visitor, @NonNull CONTEXT context);
}
