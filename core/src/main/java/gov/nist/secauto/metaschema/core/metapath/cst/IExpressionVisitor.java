/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.cst.comparison.GeneralComparison;
import gov.nist.secauto.metaschema.core.metapath.cst.comparison.ValueComparison;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Addition;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Division;
import gov.nist.secauto.metaschema.core.metapath.cst.math.IntegerDivision;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Modulo;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Multiplication;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Subtraction;
import gov.nist.secauto.metaschema.core.metapath.cst.path.Axis;
import gov.nist.secauto.metaschema.core.metapath.cst.path.ContextItem;
import gov.nist.secauto.metaschema.core.metapath.cst.path.Flag;
import gov.nist.secauto.metaschema.core.metapath.cst.path.ModelInstance;
import gov.nist.secauto.metaschema.core.metapath.cst.path.NameTest;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RelativeDoubleSlashPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RelativeSlashPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RootDoubleSlashPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RootSlashOnlyPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RootSlashPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.Step;
import gov.nist.secauto.metaschema.core.metapath.cst.path.Wildcard;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Used to support processing a Metapath expression based on the visitor
 * pattern. Each type of expression node in the Metapath abstract syntax tree
 * (AST) is represented as a "visit" method.
 *
 * @param <RESULT>
 *          the result of processing any node
 * @param <CONTEXT>
 *          additional state to pass between nodes visited
 */
@SuppressWarnings("PMD.ExcessivePublicCount")
public interface IExpressionVisitor<RESULT, CONTEXT> {

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitAddition(@NonNull Addition expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitAnd(@NonNull And expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitExcept(@NonNull Except expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitAxis(@NonNull Axis expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitStep(@NonNull Step expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitValueComparison(@NonNull ValueComparison expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitGeneralComparison(@NonNull GeneralComparison expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitContextItem(@NonNull ContextItem expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitDecimalLiteral(@NonNull DecimalLiteral expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitDivision(@NonNull Division expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitFlag(@NonNull Flag expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitFunctionCall(@NonNull StaticFunctionCall expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitIntegerDivision(@NonNull IntegerDivision expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitIntegerLiteral(@NonNull IntegerLiteral expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitIntersect(@NonNull Intersect expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitMetapath(@NonNull Metapath expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitModulo(@NonNull Modulo expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitModelInstance(@NonNull ModelInstance expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitMultiplication(@NonNull Multiplication expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitName(@NonNull NameTest expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitNegate(@NonNull Negate expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitOr(@NonNull Or expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitPredicate(@NonNull PredicateExpression expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitRelativeDoubleSlashPath(@NonNull RelativeDoubleSlashPath expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitRelativeSlashPath(@NonNull RelativeSlashPath expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitRootDoubleSlashPath(@NonNull RootDoubleSlashPath expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitRootSlashOnlyPath(@NonNull RootSlashOnlyPath expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitRootSlashPath(@NonNull RootSlashPath expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitStringConcat(@NonNull StringConcat expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitStringLiteral(@NonNull StringLiteral expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitSubtraction(@NonNull Subtraction expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitUnion(@NonNull Union expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitWildcard(@NonNull Wildcard expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitLet(@NonNull Let expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitVariableReference(@NonNull VariableReference expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitEmptySequence(@NonNull EmptySequence<?> expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitRange(@NonNull Range expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitIf(@NonNull If expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitQuantified(@NonNull Quantified expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitFor(@NonNull For expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitSimpleMap(@NonNull SimpleMap expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitMapConstructor(@NonNull MapConstructor expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitMapConstructorEntry(@NonNull MapConstructor.Entry expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitArray(@NonNull ArraySequenceConstructor expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitArray(@NonNull ArraySquareConstructor expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitPostfixLookup(@NonNull PostfixLookup expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitFunctionCallAccessor(@NonNull FunctionCallAccessor expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitUnaryLookup(@NonNull UnaryLookup expr, @NonNull CONTEXT context);
}
