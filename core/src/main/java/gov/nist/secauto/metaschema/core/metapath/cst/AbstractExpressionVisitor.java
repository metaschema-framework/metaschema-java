/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.cst.items.ArraySequenceConstructor;
import gov.nist.secauto.metaschema.core.metapath.cst.items.ArraySquareConstructor;
import gov.nist.secauto.metaschema.core.metapath.cst.items.DecimalLiteral;
import gov.nist.secauto.metaschema.core.metapath.cst.items.EmptySequence;
import gov.nist.secauto.metaschema.core.metapath.cst.items.IntegerLiteral;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Intersect;
import gov.nist.secauto.metaschema.core.metapath.cst.items.MapConstructor;
import gov.nist.secauto.metaschema.core.metapath.cst.items.PostfixLookup;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Quantified;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Range;
import gov.nist.secauto.metaschema.core.metapath.cst.items.SimpleMap;
import gov.nist.secauto.metaschema.core.metapath.cst.items.StringConcat;
import gov.nist.secauto.metaschema.core.metapath.cst.items.StringLiteral;
import gov.nist.secauto.metaschema.core.metapath.cst.items.UnaryLookup;
import gov.nist.secauto.metaschema.core.metapath.cst.items.Union;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.And;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.Except;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.GeneralComparison;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.If;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.Negate;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.Or;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.PredicateExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.logic.ValueComparison;
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
import gov.nist.secauto.metaschema.core.metapath.cst.type.Cast;
import gov.nist.secauto.metaschema.core.metapath.cst.type.Castable;
import gov.nist.secauto.metaschema.core.metapath.cst.type.InstanceOf;
import gov.nist.secauto.metaschema.core.metapath.cst.type.Treat;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides base support for processing a Metapath expression based on the
 * visitor pattern.
 *
 * @param <RESULT>
 *          the result of processing any node
 * @param <CONTEXT>
 *          additional state to pass between nodes visited
 */
@SuppressWarnings({ "PMD.CouplingBetweenObjects", "PMD.ExcessivePublicCount" })
public abstract class AbstractExpressionVisitor<RESULT, CONTEXT> implements IExpressionVisitor<RESULT, CONTEXT> {

  /**
   * This dispatch method will visit the provided {@code expression}.
   *
   * @param expression
   *          the expression to visit
   * @param context
   *          the visitor context
   * @return the result
   */
  protected RESULT visit(@NonNull IExpression expression, @NonNull CONTEXT context) {
    return expression.accept(this, context);
  }

  /**
   * Visit each child expression of the provided {@code expr}, aggregating the
   * results.
   *
   * @param expr
   *          the expression whoose children should be visited
   * @param context
   *          used to pass additional state
   * @return the aggegated result
   */
  protected RESULT visitChildren(@NonNull IExpression expr, @NonNull CONTEXT context) {
    RESULT result = defaultResult();

    for (IExpression childExpr : expr.getChildren()) {
      assert childExpr != null;
      if (!shouldVisitNextChild(expr, childExpr, result, context)) {
        break;
      }

      RESULT childResult = childExpr.accept(this, context);
      result = aggregateResult(result, childResult, context);
    }

    return result;
  }

  /**
   * Determines if a given {@code childExpr} should be visited.
   *
   * @param parent
   *          the parent expression of the child
   * @param child
   *          the child expression that can be visited
   * @param result
   *          the current result of evaluating any previous children
   * @param context
   *          additional state to pass between nodes visited
   * @return {@code true} if the child should be visited, or {@code false}
   *         otherwise
   */
  protected boolean shouldVisitNextChild(
      @NonNull IExpression parent,
      @NonNull IExpression child,
      @Nullable RESULT result,
      @NonNull CONTEXT context) {
    // allow visitation of the child
    return true;
  }

  /**
   * Aggregates the results produced by a visitation with an existing result into
   * a single result.
   *
   * @param result
   *          the existing result
   * @param nextResult
   *          the new result produced by a visitation
   * @param context
   *          the state passed to the last visitation
   * @return the aggregate result
   */
  @Nullable
  protected abstract RESULT aggregateResult(
      @Nullable RESULT result,
      @Nullable RESULT nextResult,
      @NonNull CONTEXT context);

  /**
   * Get the default result.
   *
   * @return the default result
   */
  protected abstract RESULT defaultResult();

  @Override
  public RESULT visitAddition(Addition expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitAnd(And expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitAxis(@NonNull Axis expr, @NonNull CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitStep(Step expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitValueComparison(ValueComparison expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitGeneralComparison(GeneralComparison expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitContextItem(ContextItem expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitDecimalLiteral(DecimalLiteral expr, CONTEXT context) {
    return defaultResult();
  }

  @Override
  public RESULT visitDivision(Division expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitExcept(@NonNull Except expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitFlag(Flag expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitStaticFunctionCall(StaticFunctionCall expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitDynamicFunctionCall(DynamicFunctionCall expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitAnonymousFunctionCall(AnonymousFunctionCall expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitIntegerDivision(IntegerDivision expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitIntegerLiteral(IntegerLiteral expr, CONTEXT context) {
    return defaultResult();
  }

  @Override
  public RESULT visitIntersect(@NonNull Intersect expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitMetapath(Metapath expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitModulo(Modulo expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitModelInstance(ModelInstance expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitMultiplication(Multiplication expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitName(NameTest expr, CONTEXT context) {
    return defaultResult();
  }

  @Override
  public RESULT visitNegate(Negate expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitOr(Or expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitPredicate(PredicateExpression expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitRelativeDoubleSlashPath(RelativeDoubleSlashPath expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitRelativeSlashPath(RelativeSlashPath expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitRootDoubleSlashPath(RootDoubleSlashPath expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitRootSlashOnlyPath(RootSlashOnlyPath expr, CONTEXT context) {
    return defaultResult();
  }

  @Override
  public RESULT visitRootSlashPath(RootSlashPath expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitStringConcat(StringConcat expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitStringLiteral(StringLiteral expr, CONTEXT context) {
    return defaultResult();
  }

  @Override
  public RESULT visitSubtraction(Subtraction expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitUnion(Union expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitWildcard(Wildcard expr, CONTEXT context) {
    return defaultResult();
  }

  @Override
  public RESULT visitLet(Let expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitVariableReference(VariableReference expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitEmptySequence(EmptySequence<?> expr, CONTEXT context) {
    return defaultResult();
  }

  @Override
  public RESULT visitRange(Range expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitIf(If expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitQuantified(Quantified expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitFor(For expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitSimpleMap(SimpleMap expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitMapConstructor(MapConstructor expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitMapConstructorEntry(MapConstructor.Entry expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitArray(ArraySequenceConstructor expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitArray(ArraySquareConstructor expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitPostfixLookup(PostfixLookup expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitFunctionCallAccessor(FunctionCallAccessor expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitUnaryLookup(UnaryLookup expr, CONTEXT context) {
    return defaultResult();
  }

  @Override
  public RESULT visitInstanceOf(InstanceOf expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitCast(Cast expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitCastable(Castable expr, CONTEXT context) {
    return visitChildren(expr, context);
  }

  @Override
  public RESULT visitTreat(Treat expr, CONTEXT context) {
    return visitChildren(expr, context);
  }
}
