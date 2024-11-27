/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.antlr; // NOPMD requires a large number of public methods

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
  public R visitMetapath(Metapath10.MetapathContext ctx) {
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
  protected abstract R handleExpr(@NonNull Metapath10.ExprContext ctx);

  @Override
  public R visitExpr(Metapath10.ExprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleExpr);
  }

  @Override
  public R visitExprsingle(Metapath10.ExprsingleContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  // ============================================================================
  // Primary Expressions - https://www.w3.org/TR/xpath-31/#id-primary-expressions
  // ============================================================================

  @Override
  public R visitPrimaryexpr(Metapath10.PrimaryexprContext ctx) {
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
  protected abstract R handleStringLiteral(@NonNull Metapath10.LiteralContext ctx);

  @Override
  public R visitLiteral(Metapath10.LiteralContext ctx) {
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
  protected abstract R handleNumericLiteral(@NonNull Metapath10.NumericliteralContext ctx);

  @Override
  public R visitNumericliteral(Metapath10.NumericliteralContext ctx) {
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
  protected abstract R handleVarref(@NonNull Metapath10.VarrefContext ctx);

  @Override
  public R visitVarref(Metapath10.VarrefContext ctx) {
    assert ctx != null;
    return handleVarref(ctx);
  }

  @Override
  public R visitVarname(Metapath10.VarnameContext ctx) {
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
  protected abstract R handleEmptyParenthesizedexpr(@NonNull Metapath10.ParenthesizedexprContext ctx);

  @Override
  public R visitParenthesizedexpr(Metapath10.ParenthesizedexprContext ctx) {
    assert ctx != null;
    Metapath10.ExprContext expr = ctx.expr();
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
  protected abstract R handleContextitemexpr(@NonNull Metapath10.ContextitemexprContext ctx);

  @Override
  public R visitContextitemexpr(Metapath10.ContextitemexprContext ctx) {
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
  protected abstract R handleFunctioncall(@NonNull Metapath10.FunctioncallContext ctx);

  @Override
  public R visitFunctioncall(Metapath10.FunctioncallContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleFunctioncall);
  }

  @Override
  public R visitArgumentlist(Metapath10.ArgumentlistContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitArgument(Metapath10.ArgumentContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // ============================================================
  // https://www.w3.org/TR/xpath-31/#doc-xpath31-NamedFunctionRef
  // ============================================================

  @Override
  public R visitNamedfunctionref(Metapath10.NamedfunctionrefContext ctx) {
    throw new UnsupportedOperationException("expression not supported");
  }

  // ==============================================
  // https://www.w3.org/TR/xpath-31/#id-inline-func
  // ==============================================

  @Override
  public R visitFunctionitemexpr(Metapath10.FunctionitemexprContext ctx) {
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
  protected abstract R handleInlinefunctionexpr(@NonNull Metapath10.InlinefunctionexprContext ctx);

  @Override
  public R visitInlinefunctionexpr(Metapath10.InlinefunctionexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleInlinefunctionexpr);
  }

  // =======================================================================
  // Enclosed Expressions - https://www.w3.org/TR/xpath-31/#id-enclosed-expr
  // =======================================================================

  @Override
  public R visitEnclosedexpr(Metapath10.EnclosedexprContext ctx) {
    Metapath10.ExprContext expr = ctx.expr();
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
  protected abstract R handlePostfixexpr(@NonNull Metapath10.PostfixexprContext ctx);

  @Override
  public R visitPostfixexpr(Metapath10.PostfixexprContext ctx) {
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
  protected abstract R handlePredicate(@NonNull Metapath10.PredicateContext ctx);

  @Override
  public R visitPredicate(Metapath10.PredicateContext ctx) {
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
  protected abstract R handleLookup(@NonNull Metapath10.LookupContext ctx);

  @Override
  public R visitLookup(Metapath10.LookupContext ctx) {
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
  protected abstract R handlePathexpr(@NonNull Metapath10.PathexprContext ctx);

  @Override
  public R visitPathexpr(Metapath10.PathexprContext ctx) {
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
  protected abstract R handleRelativepathexpr(@NonNull Metapath10.RelativepathexprContext ctx);

  @Override
  public R visitRelativepathexpr(Metapath10.RelativepathexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleRelativepathexpr);
  }

  // ================================================
  // Steps - https://www.w3.org/TR/xpath-31/#id-steps
  // ================================================

  @Override
  public R visitStepexpr(Metapath10.StepexprContext ctx) {
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
  protected abstract R handleForwardstep(@NonNull Metapath10.ForwardstepContext ctx);

  @Override
  public R visitForwardstep(Metapath10.ForwardstepContext ctx) {
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
  protected abstract R handleReversestep(@NonNull Metapath10.ReversestepContext ctx);

  @Override
  public R visitReversestep(Metapath10.ReversestepContext ctx) {
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
  protected abstract R handleAxisstep(@NonNull Metapath10.AxisstepContext ctx);

  @Override
  public R visitAxisstep(Metapath10.AxisstepContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleAxisstep);
  }

  @Override
  public R visitPredicatelist(Metapath10.PredicatelistContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // ===========================================
  // Axes - https://www.w3.org/TR/xpath-31/#axes
  // ===========================================

  @Override
  public R visitForwardaxis(Metapath10.ForwardaxisContext ctx) {
    // should never be called, since this is handled by handleForwardstep
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitReverseaxis(Metapath10.ReverseaxisContext ctx) {
    // should never be called, since this is handled by handleReversestep
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  // =======================================================
  // Node Tests - https://www.w3.org/TR/xpath-31/#node-tests
  // =======================================================

  @Override
  public R visitNodetest(Metapath10.NodetestContext ctx) {
    // should never be called, since this is handled by the calling context
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitNametest(Metapath10.NametestContext ctx) {
    // should never be called, since this is handled by the calling context
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitEqname(Metapath10.EqnameContext ctx) {
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
  protected abstract R handleWildcard(@NonNull Metapath10.WildcardContext ctx);

  @Override
  public R visitWildcard(Metapath10.WildcardContext ctx) {
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
  protected abstract R handleAbbrevforwardstep(@NonNull Metapath10.AbbrevforwardstepContext ctx);

  @Override
  public R visitAbbrevforwardstep(Metapath10.AbbrevforwardstepContext ctx) {
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
  protected abstract R handleAbbrevreversestep(@NonNull Metapath10.AbbrevreversestepContext ctx);

  @Override
  public R visitAbbrevreversestep(Metapath10.AbbrevreversestepContext ctx) {
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
  protected abstract R handleRangeexpr(@NonNull Metapath10.RangeexprContext ctx);

  @Override
  public R visitRangeexpr(Metapath10.RangeexprContext ctx) {
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
  protected abstract R handleUnionexpr(@NonNull Metapath10.UnionexprContext ctx);

  @Override
  public R visitUnionexpr(Metapath10.UnionexprContext ctx) {
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
  protected abstract R handleIntersectexceptexpr(@NonNull Metapath10.IntersectexceptexprContext ctx);

  @Override
  public R visitIntersectexceptexpr(Metapath10.IntersectexceptexprContext ctx) {
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
  protected abstract R handleAdditiveexpr(@NonNull Metapath10.AdditiveexprContext ctx);

  @Override
  public R visitAdditiveexpr(Metapath10.AdditiveexprContext ctx) {
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
  protected abstract R handleMultiplicativeexpr(@NonNull Metapath10.MultiplicativeexprContext ctx);

  @Override
  public R visitMultiplicativeexpr(Metapath10.MultiplicativeexprContext ctx) {
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
  protected abstract R handleUnaryexpr(@NonNull Metapath10.UnaryexprContext ctx);

  @Override
  public R visitUnaryexpr(Metapath10.UnaryexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleUnaryexpr);
  }

  @Override
  public R visitValueexpr(Metapath10.ValueexprContext ctx) {
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
  protected abstract R handleStringconcatexpr(@NonNull Metapath10.StringconcatexprContext ctx);

  @Override
  public R visitStringconcatexpr(Metapath10.StringconcatexprContext ctx) {
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
  protected abstract R handleComparisonexpr(@NonNull Metapath10.ComparisonexprContext ctx);

  @Override
  public R visitComparisonexpr(Metapath10.ComparisonexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleComparisonexpr);
  }

  @Override
  public R visitValuecomp(Metapath10.ValuecompContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitGeneralcomp(Metapath10.GeneralcompContext ctx) {
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
  protected abstract R handleOrexpr(@NonNull Metapath10.OrexprContext ctx);

  @Override
  public R visitOrexpr(Metapath10.OrexprContext ctx) {
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
  protected abstract R handleAndexpr(@NonNull Metapath10.AndexprContext ctx);

  @Override
  public R visitAndexpr(Metapath10.AndexprContext ctx) {
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
  protected abstract R handleForexpr(@NonNull Metapath10.ForexprContext ctx);

  @Override
  public R visitForexpr(Metapath10.ForexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleForexpr);
  }

  @Override
  public R visitSimpleforclause(Metapath10.SimpleforclauseContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitSimpleforbinding(Metapath10.SimpleforbindingContext ctx) {
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
  protected abstract R handleLet(@NonNull Metapath10.LetexprContext ctx);

  @Override
  public R visitLetexpr(Metapath10.LetexprContext ctx) {
    assert ctx != null;
    return handleLet(ctx);
  }

  @Override
  public R visitSimpleletclause(Metapath10.SimpleletclauseContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitSimpleletbinding(Metapath10.SimpleletbindingContext ctx) {
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
  protected abstract R handleMapConstructor(@NonNull Metapath10.MapconstructorContext ctx);

  @Override
  public R visitMapconstructor(Metapath10.MapconstructorContext ctx) {
    assert ctx != null;
    return handleMapConstructor(ctx);
  }

  @Override
  public R visitMapconstructorentry(Metapath10.MapconstructorentryContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitMapkeyexpr(Metapath10.MapkeyexprContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  @Override
  public R visitMapvalueexpr(Metapath10.MapvalueexprContext ctx) {
    assert ctx != null;
    return delegateToChild(ctx);
  }

  // ==============================================================
  // Array Constructors - https://www.w3.org/TR/xpath-31/#id-arrays
  // ==============================================================

  @Override
  public R visitArrayconstructor(Metapath10.ArrayconstructorContext ctx) {
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
  protected abstract R handleArrayConstructor(@NonNull Metapath10.SquarearrayconstructorContext ctx);

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleArrayConstructor(@NonNull Metapath10.CurlyarrayconstructorContext ctx);

  @Override
  public R visitSquarearrayconstructor(Metapath10.SquarearrayconstructorContext ctx) {
    assert ctx != null;
    return handleArrayConstructor(ctx);
  }

  @Override
  public R visitCurlyarrayconstructor(Metapath10.CurlyarrayconstructorContext ctx) {
    assert ctx != null;
    return handleArrayConstructor(ctx);
  }

  @Override
  public R visitKeyspecifier(Metapath10.KeyspecifierContext ctx) {
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
  protected abstract R handleUnarylookup(@NonNull Metapath10.UnarylookupContext ctx);

  @Override
  public R visitUnarylookup(Metapath10.UnarylookupContext ctx) {
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
  protected abstract R handleIfexpr(@NonNull Metapath10.IfexprContext ctx);

  @Override
  public R visitIfexpr(Metapath10.IfexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleIfexpr);
  }

  /*
   * ==================================================================================
   * Quantified Expressions - https://www.w3.org/TR/xpath-31/#id-quantified-expressions
   * ==================================================================================
   */

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleQuantifiedexpr(@NonNull Metapath10.QuantifiedexprContext ctx);

  @Override
  public R visitQuantifiedexpr(Metapath10.QuantifiedexprContext ctx) {
    assert ctx != null;
    return handleQuantifiedexpr(ctx);
  }

  /*
   * ============================================================
   * instance of - https://www.w3.org/TR/xpath-31/#id-instance-of
   * ============================================================
   */

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleInstanceofexpr(@NonNull Metapath10.InstanceofexprContext ctx);

  @Override
  public R visitInstanceofexpr(Metapath10.InstanceofexprContext ctx) {
    assert ctx != null;
    return handle(ctx, context -> handleInstanceofexpr(ctx));
  }

  // ==============================================
  // cast - https://www.w3.org/TR/xpath-31/#id-cast
  // ==============================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleCastexpr(@NonNull Metapath10.CastexprContext ctx);

  @Override
  public R visitCastexpr(Metapath10.CastexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleCastexpr);
  }

  // ======================================================
  // castable - https://www.w3.org/TR/xpath-31/#id-castable
  // ======================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleCastableexpr(@NonNull Metapath10.CastableexprContext ctx);

  @Override
  public R visitCastableexpr(Metapath10.CastableexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleCastableexpr);
  }

  // ================================================
  // treat - https://www.w3.org/TR/xpath-31/#id-treat
  // ================================================

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleTreatexpr(@NonNull Metapath10.TreatexprContext ctx);

  @Override
  public R visitTreatexpr(Metapath10.TreatexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleTreatexpr);
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
  protected abstract R handleSimplemapexpr(@NonNull Metapath10.SimplemapexprContext ctx);

  @Override
  public R visitSimplemapexpr(Metapath10.SimplemapexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleSimplemapexpr);
  }

  /*
   * =======================================================================
   * Arrow operator (=>) - https://www.w3.org/TR/xpath-31/#id-arrow-operator
   * =======================================================================
   */

  /**
   * Handle the provided expression.
   *
   * @param ctx
   *          the provided expression context
   * @return the result
   */
  protected abstract R handleArrowexpr(@NonNull Metapath10.ArrowexprContext ctx);

  @Override
  public R visitArrowexpr(Metapath10.ArrowexprContext ctx) {
    assert ctx != null;
    return handle(ctx, this::handleArrowexpr);
  }

  @Override
  public R visitArrowfunctionspecifier(Metapath10.ArrowfunctionspecifierContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  /*
   * ==========================================================
   * The following are handled inline by other expression types
   * ==========================================================
   */
  @Override
  public R visitFunctiontest(Metapath10.FunctiontestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitAnyfunctiontest(Metapath10.AnyfunctiontestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitTypedfunctiontest(Metapath10.TypedfunctiontestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitMaptest(Metapath10.MaptestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitAnymaptest(Metapath10.AnymaptestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitTypedmaptest(Metapath10.TypedmaptestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitArraytest(Metapath10.ArraytestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitAnyarraytest(Metapath10.AnyarraytestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitTypedarraytest(Metapath10.TypedarraytestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitParenthesizeditemtype(Metapath10.ParenthesizeditemtypeContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitKindtest(Metapath10.KindtestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitAnykindtest(Metapath10.AnykindtestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitDocumenttest(Metapath10.DocumenttestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitTexttest(Metapath10.TexttestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitFlagtest(Metapath10.FlagtestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitFlagnameorwildcard(Metapath10.FlagnameorwildcardContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitFlagname(Metapath10.FlagnameContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitFieldtest(Metapath10.FieldtestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitAssemblytest(Metapath10.AssemblytestContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitElementnameorwildcard(Metapath10.ElementnameorwildcardContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitElementdeclaration(Metapath10.ElementdeclarationContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitElementname(Metapath10.ElementnameContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitSingletype(Metapath10.SingletypeContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitSequencetype(Metapath10.SequencetypeContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitItemtype(Metapath10.ItemtypeContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitOccurrenceindicator(Metapath10.OccurrenceindicatorContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitAtomicoruniontype(Metapath10.AtomicoruniontypeContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitSimpletypename(Metapath10.SimpletypenameContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitTypename_(Metapath10.Typename_Context ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitParamlist(Metapath10.ParamlistContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitParam(Metapath10.ParamContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitFunctionbody(Metapath10.FunctionbodyContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }

  @Override
  public R visitTypedeclaration(Metapath10.TypedeclarationContext ctx) {
    // should never be called, since this is handled by the parent expression
    throw new IllegalStateException(ERR_NO_DELEGATION);
  }
}
