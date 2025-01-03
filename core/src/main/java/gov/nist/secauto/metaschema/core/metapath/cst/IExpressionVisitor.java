/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.cst.items.ArraySequenceConstructor;
import gov.nist.secauto.metaschema.core.metapath.cst.items.ArraySquareConstructor;
import gov.nist.secauto.metaschema.core.metapath.cst.items.DecimalLiteral;
import gov.nist.secauto.metaschema.core.metapath.cst.items.EmptySequence;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Except;
import gov.nist.secauto.metaschema.core.metapath.cst.items.IntegerLiteral;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Intersect;
import gov.nist.secauto.metaschema.core.metapath.cst.items.MapConstructor;
import gov.nist.secauto.metaschema.core.metapath.cst.items.PostfixLookup;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Quantified;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Range;
import gov.nist.secauto.metaschema.core.metapath.cst.items.SequenceExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.items.SimpleMap;
import gov.nist.secauto.metaschema.core.metapath.cst.items.StringConcat;
import gov.nist.secauto.metaschema.core.metapath.cst.items.StringLiteral;
import gov.nist.secauto.metaschema.core.metapath.cst.items.UnaryLookup;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Union;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.And;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.GeneralComparison;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.If;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.Or;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.PredicateExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.ValueComparison;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Addition;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Division;
import gov.nist.secauto.metaschema.core.metapath.cst.math.IntegerDivision;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Modulo;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Multiplication;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Negate;
import gov.nist.secauto.metaschema.core.metapath.cst.math.Subtraction;
import gov.nist.secauto.metaschema.core.metapath.cst.path.ContextItem;
import gov.nist.secauto.metaschema.core.metapath.cst.path.FlagStep;
import gov.nist.secauto.metaschema.core.metapath.cst.path.KindNodeTest;
import gov.nist.secauto.metaschema.core.metapath.cst.path.ModelInstanceStep;
import gov.nist.secauto.metaschema.core.metapath.cst.path.NameNodeTest;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RelativeDoubleSlashPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RelativeSlashPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RootDoubleSlashPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RootSlashOnlyPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.RootSlashPath;
import gov.nist.secauto.metaschema.core.metapath.cst.path.Step;
import gov.nist.secauto.metaschema.core.metapath.cst.path.WildcardNodeTest;
import gov.nist.secauto.metaschema.core.metapath.cst.type.Cast;
import gov.nist.secauto.metaschema.core.metapath.cst.type.Castable;
import gov.nist.secauto.metaschema.core.metapath.cst.type.InstanceOf;
import gov.nist.secauto.metaschema.core.metapath.cst.type.Treat;

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
  RESULT visitFlagStep(@NonNull FlagStep expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitStaticFunctionCall(@NonNull StaticFunctionCall expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitDynamicFunctionCall(@NonNull DynamicFunctionCall expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitAnonymousFunctionCall(@NonNull AnonymousFunctionCall expr, @NonNull CONTEXT context);

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
  RESULT visitMetapath(@NonNull SequenceExpression expr, @NonNull CONTEXT context);

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
  RESULT visitModelInstanceStep(@NonNull ModelInstanceStep expr, @NonNull CONTEXT context);

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
  RESULT visitNameNodeTest(@NonNull NameNodeTest expr, @NonNull CONTEXT context);

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
  RESULT visitWildcardNodeTest(@NonNull WildcardNodeTest expr, @NonNull CONTEXT context);

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
  RESULT visitNamedFunctionReference(@NonNull NamedFunctionReference expr, @NonNull CONTEXT context);

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

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitInstanceOf(@NonNull InstanceOf expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitCast(@NonNull Cast expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitCastable(@NonNull Castable expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitTreat(@NonNull Treat expr, @NonNull CONTEXT context);

  /**
   * Visit the CST node.
   *
   * @param expr
   *          the CST node to visit
   * @param context
   *          the processing context
   * @return the visitation result or {@code null} if no result was produced
   */
  RESULT visitKindNodeTest(@NonNull KindNodeTest expr, @NonNull CONTEXT context);
}
