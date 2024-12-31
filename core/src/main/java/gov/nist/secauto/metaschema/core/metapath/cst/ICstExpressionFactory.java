/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.cst.Let.VariableDeclaration;
import gov.nist.secauto.metaschema.core.metapath.cst.items.ArraySequenceConstructor;
import gov.nist.secauto.metaschema.core.metapath.cst.items.ArraySquareConstructor;
import gov.nist.secauto.metaschema.core.metapath.cst.items.DecimalLiteral;
import gov.nist.secauto.metaschema.core.metapath.cst.items.EmptySequence;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Except;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.And;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.If;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Addition;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import java.math.BigDecimal;
import java.util.List;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A factory interface that supports creating new Metapath CST nodes.
 */
public interface ICstExpressionFactory {
  /**
   * Construct an "and" logical expression.
   *
   * @param expressions
   *          the expressions to evaluate
   * @return the expression
   */
  @NonNull
  And newAnd(@NonNull List<IExpression> expressions);

  /**
   * Construct an array constructor expression that uses the provided expression
   * to initialize the array.
   *
   * @param expression
   *          the expression used to produce the array members
   * @return the expression
   */
  @NonNull
  ArraySequenceConstructor newArraySequenceConstructor(@Nullable IExpression expression);

  /**
   * Construct an array constructor expression that uses the provided expression
   * to initialize the array.
   * <p>
   * Each resulting array member contains the value of the corresponding argument
   * expression.
   *
   * @param children
   *          the expressions used to produce the array members
   * @return the expression
   */
  @NonNull
  ArraySquareConstructor newArraySquareConstructor(@NonNull List<IExpression> children);

  /**
   * Construct an expression that always returns the same decimal value.
   *
   * @param value
   *          the literal value
   * @return the expression
   */
  @NonNull
  DecimalLiteral newDecimalLiteral(@NonNull BigDecimal value);

  /**
   * Construct an expression that returns an empty sequence.
   *
   * @param <T>
   *          the Java type of the resulting empty sequence
   * @return the expression
   */
  @NonNull
  <T extends IItem> EmptySequence<T> newEmptySequence();

  /**
   * Construct a except filter expression, which removes the items resulting from
   * the filter expression from the items expression.
   *
   * @param itemsExpression
   *          an expression indicating the items to filter
   * @param filterExpression
   *          an expression indicating the items to omit
   * @return the expression
   */
  @NonNull
  Except newExcept(@NonNull IExpression itemsExpression, @NonNull IExpression filterExpression);

  /**
   * Construct a new let expression using the provided variable and return clause.
   *
   * @param variable
   *          the variable declaration
   * @param returnExpr
   *          the return clause that makes use of variables for evaluation
   * @return the expression
   */
  @NonNull
  For newFor(@NonNull VariableDeclaration variable, @NonNull IExpression returnExpr);

  /**
   * Construct a new functional call accessor.
   *
   * @param base
   *          the expression whose result is used as the map or array to perform
   *          the lookup on
   * @param keyOrIndex
   *          the value to find, which will be the key for a map or the index for
   *          an array
   * @return the expression
   */
  @NonNull
  FunctionCallAccessor newFunctionCallAccessor(@NonNull IExpression base, @NonNull IExpression keyOrIndex);

  /**
   * Construct a new conditional expression.
   *
   * @param testExpression
   *          the first expression to evaluate
   * @param thenExpression
   *          the expression to evaluate if the test is {@code true}
   * @param elseExpression
   *          the expression to evaluate if the test is {@code false}
   * @return the expression
   */
  @NonNull
  If newIf(
      @NonNull IExpression testExpression,
      @NonNull IExpression thenExpression,
      @NonNull IExpression elseExpression);

  /**
   * Construct a new additive expression that sums two atomic data items.
   *
   * @param left
   *          an expression whose result is summed
   * @param right
   *          an expression whose result is summed
   * @return the expression
   */
  Addition newAddition(
      @NonNull IExpression left,
      @NonNull IExpression right);
}
