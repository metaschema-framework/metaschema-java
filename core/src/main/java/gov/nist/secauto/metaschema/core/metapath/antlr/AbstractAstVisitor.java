/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.antlr; // NOPMD requires a large number of public methods

import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AbbrevforwardstepContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AbbrevreversestepContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AdditiveexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AndexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ArgumentContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ArgumentlistContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ArrayconstructorContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ArrowexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ArrowfunctionspecifierContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.AxisstepContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ComparisonexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ContextitemexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.CurlyarrayconstructorContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.EnclosedexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.EqnameContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ExprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ExprsingleContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ForexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ForwardaxisContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ForwardstepContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.FunctioncallContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.GeneralcompContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.IfexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.IntersectexceptexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.KeyspecifierContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.LetexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.LiteralContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.LookupContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.MapconstructorContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.MapconstructorentryContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.MapkeyexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.MapvalueexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.MetapathContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.MultiplicativeexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.NametestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.NodetestContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.NumericliteralContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.OrexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ParenthesizedexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.PathexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.PostfixexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.PredicateContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.PredicatelistContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.PrimaryexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.QuantifiedexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.RangeexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.RelativepathexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ReverseaxisContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ReversestepContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.SimpleforbindingContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.SimpleforclauseContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.SimpleletbindingContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.SimpleletclauseContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.SimplemapexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.SquarearrayconstructorContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.StepexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.StringconcatexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.UnaryexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.UnarylookupContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.UnionexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ValuecompContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ValueexprContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.VarnameContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.VarrefContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.WildcardContext;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This abstract class supports processing an ANTLR-based abstract syntax tree
 * by walking the tree using a visitor pattern.
 *
 * @param <R>
 *          the Java type of the result produced through visitation
 */
@SuppressWarnings({ "PMD.ExcessivePublicCount", "PMD.CyclomaticComplexity" })
public abstract class AbstractAstVisitor<R>
    extends Metapath10BaseVisitor<R> {
  private static final String ERR_NO_DELEGATION
      = "This method should never be called directly as it is handled by the parent expression.";
  private static final String ERR_SINGLE_CHILD = "A single child expression was expected.";

  /**
   * This dispatch method will call the node handler on a leaf node or if multiple
   * child expressions exist. Otherwise, it will delegate to the single child
   * expression.
   *
   * @param <T>
   *          the visitor context type
   * @param ctx
   *          the visitor context
   * @param handler
   *          the node handler
   * @return the result
   */
  protected <T extends RuleContext> R handle(T ctx, @NonNull Function<T, R> handler) {
    T context = ObjectUtils.requireNonNull(ctx);

    R retval;
    if (context.getChildCount() == 1 && context.getChild(0) instanceof ParserRuleContext) {
      // delegate to the child expression, since this expression doesn't require any
      // action
      retval = context.getChild(0).accept(this);
    } else {
      retval = handler.apply(context);
    }
    return retval;
  }

  /**
   * This dispatch method expects a single child expression which will be called.
   * Other cases will result in an exception.
   *
   * @param <T>
   *          the visitor context type
   * @param ctx
   *          the visitor context
   * @return the result
   * @throws IllegalStateException
   *           if there was not a single child expression
   */
  protected <T extends RuleContext> R delegateToChild(@NonNull T ctx) {
    if (ctx.getChildCount() == 1) {
      return ctx.getChild(0).accept(this);
    }
    throw new IllegalStateException(ERR_SINGLE_CHILD);
  }

  // ============================================================
  // Expressions - https://www.w3.org/TR/xpath-31/#id-expressions
  // ============================================================

  @Override
  public R visitMetapath(MetapathContext ctx) {
    assert ctx != null;
    return ctx.expr().accept(this);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleExpr(@NonNull ExprContext ctx);

  @Override
  public R visitExpr(ExprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleExpr);
  }

  @Override
  public R visitExprsingle(ExprsingleContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  // ============================================================================
  // Primary Expressions - https://www.w3.org/TR/xpath-31/#id-primary-expressions
  // ============================================================================

  @Override
  public R visitPrimaryexpr(PrimaryexprContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  // =================================================================
  // Literal Expressions - https://www.w3.org/TR/xpath-31/#id-literals
  // =================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleStringLiteral(@NonNull LiteralContext ctx);

  @Override
  public R visitLiteral(LiteralContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleStringLiteral);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleNumericLiteral(@NonNull NumericliteralContext ctx);

  @Override
  public R visitNumericliteral(NumericliteralContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleNumericLiteral);
  }

  // ==================================================================
  // Variable References - https://www.w3.org/TR/xpath-31/#id-variables
  // ==================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleVarref(@NonNull VarrefContext ctx);

  @Override
  public R visitVarref(VarrefContext ctx) {
    assert ctx != null;
    return handleVarref(ctx);
  }

  @Override
  public R visitVarname(VarnameContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  // ====================================================
  // Parenthesized Expressions -
  // https://www.w3.org/TR/xpath-31/#id-paren-expressions
  // ====================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleEmptyParenthesizedexpr(@NonNull ParenthesizedexprContext ctx);

  @Override
  public R visitParenthesizedexpr(ParenthesizedexprContext ctx) {
    assert ctx != null;
    ExprContext expr = ctx.expr();
    return expr == null ? handleEmptyParenthesizedexpr(ctx) : visit(expr);
  }

  // ==========================================================
  // Context Item Expression -
  // https://www.w3.org/TR/xpath-31/#id-context-item-expression
  // ==========================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleContextitemexpr(@NonNull ContextitemexprContext ctx);

  @Override
  public R visitContextitemexpr(ContextitemexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleContextitemexpr);
  }

  // =========================================================================
  // Static Function Calls - https://www.w3.org/TR/xpath-31/#id-function-calls
  // =========================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleFunctioncall(@NonNull FunctioncallContext ctx);

  @Override
  public R visitFunctioncall(FunctioncallContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleFunctioncall);
  }

  @Override
  public R visitArgumentlist(ArgumentlistContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitArgument(ArgumentContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // =======================================================================
  // Enclosed Expressions - https://www.w3.org/TR/xpath-31/#id-enclosed-expr
  // =======================================================================

  @Override
  public R visitEnclosedexpr(EnclosedexprContext ctx) {
    ExprContext expr = ctx.expr();
    return expr == null ? null : expr.accept(this);
  }

  // =========================================================================
  // Filter Expressions - https://www.w3.org/TR/xpath-31/#id-filter-expression
  // =========================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handlePostfixexpr(@NonNull PostfixexprContext ctx);

  @Override
  public R visitPostfixexpr(PostfixexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handlePostfixexpr);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handlePredicate(@NonNull PredicateContext ctx);

  @Override
  public R visitPredicate(PredicateContext ctx) {
    assert ctx != null;
    return handlePredicate(ctx);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleLookup(@NonNull LookupContext ctx);

  @Override
  public R visitLookup(LookupContext ctx) {
    assert ctx != null;
    return handleLookup(ctx);
  }

  // ======================================================================
  // Path Expressions - https://www.w3.org/TR/xpath-31/#id-path-expressions
  // ======================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handlePathexpr(@NonNull PathexprContext ctx);

  @Override
  public R visitPathexpr(PathexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handlePathexpr);
  }

  // ============================================================
  // RelativePath Expressions -
  // https://www.w3.org/TR/xpath-31/#id-relative-path-expressions
  // ============================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleRelativepathexpr(@NonNull RelativepathexprContext ctx);

  @Override
  public R visitRelativepathexpr(RelativepathexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleRelativepathexpr);
  }

  // ================================================
  // Steps - https://www.w3.org/TR/xpath-31/#id-steps
  // ================================================

  @Override
  public R visitStepexpr(StepexprContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleForwardstep(@NonNull ForwardstepContext ctx);

  @Override
  public R visitForwardstep(ForwardstepContext ctx) {
    assert ctx != null;
    // this will either call the handler or forward for AbbrevforwardstepContext
    return handle(ctx, this::handleForwardstep);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleReversestep(@NonNull ReversestepContext ctx);

  @Override
  public R visitReversestep(ReversestepContext ctx) {
    assert ctx != null;
    // this will either call the handler or forward for AbbrevreversestepContext
    return handle(ctx, this::handleReversestep);
  }

  // ======================================================================
  // Predicates within Steps - https://www.w3.org/TR/xpath-31/#id-predicate
  // ======================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleAxisstep(@NonNull AxisstepContext ctx);

  @Override
  public R visitAxisstep(AxisstepContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleAxisstep);
  }

  @Override
  public R visitPredicatelist(PredicatelistContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // ===========================================
  // Axes - https://www.w3.org/TR/xpath-31/#axes
  // ===========================================

  @Override
  public R visitForwardaxis(ForwardaxisContext ctx) {
    // should never be called, since this is handled by handleForwardstep
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitReverseaxis(ReverseaxisContext ctx) {
    // should never be called, since this is handled by handleReversestep
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // =======================================================
  // Node Tests - https://www.w3.org/TR/xpath-31/#node-tests
  // =======================================================

  @Override
  public R visitNodetest(NodetestContext ctx) {
    // should never be called, since this is handled by the calling context
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitNametest(NametestContext ctx) {
    // should never be called, since this is handled by the calling context
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitEqname(EqnameContext ctx) {
    // should never be called, since this is handled by the calling context
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  @NonNull
  protected abstract R handleWildcard(@NonNull WildcardContext ctx);

  @Override
  public R visitWildcard(WildcardContext ctx) {
    assert ctx != null;
    return handleWildcard(ctx);
  }

  // ===========================================================
  // Abbreviated Syntax - https://www.w3.org/TR/xpath-31/#abbrev
  // ===========================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleAbbrevforwardstep(@NonNull AbbrevforwardstepContext ctx);

  @Override
  public R visitAbbrevforwardstep(AbbrevforwardstepContext ctx) {
    assert ctx != null;
    return handleAbbrevforwardstep(ctx);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleAbbrevreversestep(@NonNull AbbrevreversestepContext ctx);

  @Override
  public R visitAbbrevreversestep(AbbrevreversestepContext ctx) {
    assert ctx != null;
    return handleAbbrevreversestep(ctx);
  }

  // ======================================================================
  // Constructing Sequences - https://www.w3.org/TR/xpath-31/#construct_seq
  // ======================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleRangeexpr(@NonNull RangeexprContext ctx);

  @Override
  public R visitRangeexpr(RangeexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleRangeexpr);
  }

  // ========================================================================
  // Combining Node Sequences - https://www.w3.org/TR/xpath-31/#combining_seq
  // ========================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleUnionexpr(@NonNull UnionexprContext ctx);

  @Override
  public R visitUnionexpr(UnionexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleUnionexpr);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleIntersectexceptexpr(@NonNull IntersectexceptexprContext ctx);

  @Override
  public R visitIntersectexceptexpr(IntersectexceptexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleIntersectexceptexpr);
  }

  // ======================================================================
  // Arithmetic Expressions - https://www.w3.org/TR/xpath-31/#id-arithmetic
  // ======================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleAdditiveexpr(@NonNull AdditiveexprContext ctx);

  @Override
  public R visitAdditiveexpr(AdditiveexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleAdditiveexpr);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleMultiplicativeexpr(@NonNull MultiplicativeexprContext ctx);

  @Override
  public R visitMultiplicativeexpr(MultiplicativeexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleMultiplicativeexpr);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleUnaryexpr(@NonNull UnaryexprContext ctx);

  @Override
  public R visitUnaryexpr(UnaryexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleUnaryexpr);
  }

  @Override
  public R visitValueexpr(ValueexprContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  // =====================================================
  // String Concatenation Expressions -
  // https://www.w3.org/TR/xpath-31/#id-string-concat-expr
  // =====================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleStringconcatexpr(@NonNull StringconcatexprContext ctx);

  @Override
  public R visitStringconcatexpr(StringconcatexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleStringconcatexpr);
  }

  // =======================================================================
  // Comparison Expressions - https://www.w3.org/TR/xpath-31/#id-comparisons
  // =======================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleComparisonexpr(@NonNull ComparisonexprContext ctx);

  @Override
  public R visitComparisonexpr(ComparisonexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleComparisonexpr);
  }

  @Override
  public R visitValuecomp(ValuecompContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitGeneralcomp(GeneralcompContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // ============================================================================
  // Logical Expressions - https://www.w3.org/TR/xpath-31/#id-logical-expressions
  // ============================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleOrexpr(@NonNull OrexprContext ctx);

  @Override
  public R visitOrexpr(OrexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleOrexpr);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleAndexpr(@NonNull AndexprContext ctx);

  @Override
  public R visitAndexpr(AndexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleAndexpr);
  }

  // ====================================================================
  // For Expressions - https://www.w3.org/TR/xpath-31/#id-for-expressions
  // ====================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleForexpr(@NonNull ForexprContext ctx);

  @Override
  public R visitForexpr(ForexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleForexpr);
  }

  @Override
  public R visitSimpleforclause(SimpleforclauseContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitSimpleforbinding(SimpleforbindingContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // ====================================================================
  // Let Expressions - https://www.w3.org/TR/xpath-31/#id-let-expressions
  // ====================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleLet(@NonNull LetexprContext ctx);

  @Override
  public R visitLetexpr(LetexprContext ctx) {
    assert ctx != null;
    return handleLet(ctx);
  }

  @Override
  public R visitSimpleletclause(SimpleletclauseContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitSimpleletbinding(SimpleletbindingContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // ======================================================================
  // Map Constructors - https://www.w3.org/TR/xpath-31/#id-map-constructors
  // ======================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleMapConstructor(@NonNull MapconstructorContext ctx);

  @Override
  public R visitMapconstructor(MapconstructorContext ctx) {
    assert ctx != null;
    return handleMapConstructor(ctx);
  }

  @Override
  public R visitMapconstructorentry(MapconstructorentryContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitMapkeyexpr(MapkeyexprContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  @Override
  public R visitMapvalueexpr(MapvalueexprContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  // ==============================================================
  // Array Constructors - https://www.w3.org/TR/xpath-31/#id-arrays
  // ==============================================================

  @Override
  public R visitArrayconstructor(ArrayconstructorContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleArrayConstructor(@NonNull SquarearrayconstructorContext ctx);

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleArrayConstructor(@NonNull CurlyarrayconstructorContext ctx);

  @Override
  public R visitSquarearrayconstructor(SquarearrayconstructorContext ctx) {
    assert ctx != null;
    return handleArrayConstructor(ctx);
  }

  @Override
  public R visitCurlyarrayconstructor(CurlyarrayconstructorContext ctx) {
    assert ctx != null;
    return handleArrayConstructor(ctx);
  }

  @Override
  public R visitKeyspecifier(KeyspecifierContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleUnarylookup(@NonNull UnarylookupContext ctx);

  @Override
  public R visitUnarylookup(UnarylookupContext ctx) {
    assert ctx != null;
    return handleUnarylookup(ctx);
  }

  // =========================================================================
  // Conditional Expressions - https://www.w3.org/TR/xpath-31/#id-conditionals
  // =========================================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleIfexpr(@NonNull IfexprContext ctx);

  @Override
  public R visitIfexpr(IfexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleIfexpr);
  }

  /*
   * =============================================================================
   * ===== Quantified Expressions -
   * https://www.w3.org/TR/xpath-31/#id-quantified-expressions
   * =============================================================================
   * =====
   */

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleQuantifiedexpr(@NonNull QuantifiedexprContext ctx);

  @Override
  public R visitQuantifiedexpr(QuantifiedexprContext ctx) {
    assert ctx != null;
    return handleQuantifiedexpr(ctx);
  }

  /*
   * =========================================================================
   * Simple map operator (!) - https://www.w3.org/TR/xpath-31/#id-map-operator
   * =========================================================================
   */

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleSimplemapexpr(@NonNull SimplemapexprContext ctx);

  @Override
  public R visitSimplemapexpr(SimplemapexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleSimplemapexpr);
  }

  /*
   * ======================================================================= Arrow
   * operator (=>) - https://www.w3.org/TR/xpath-31/#id-arrow-operator
   * =======================================================================
   */

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleArrowexpr(@NonNull ArrowexprContext ctx);

  @Override
  public R visitArrowexpr(ArrowexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleArrowexpr);
  }

  @Override
  public R visitArrowfunctionspecifier(ArrowfunctionspecifierContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }
}
