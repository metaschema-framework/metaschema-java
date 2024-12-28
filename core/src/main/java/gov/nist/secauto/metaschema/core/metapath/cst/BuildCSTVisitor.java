/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.cst;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.metapath.StaticMetapathException;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10.ParamContext;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10Lexer;
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
import gov.nist.secauto.metaschema.core.metapath.cst.logic.IBooleanLogicExpression;
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
import gov.nist.secauto.metaschema.core.metapath.cst.path.INodeTestExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.path.IWildcardMatcher;
import gov.nist.secauto.metaschema.core.metapath.cst.path.KindNodeTest;
import gov.nist.secauto.metaschema.core.metapath.cst.path.ModelInstance;
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
import gov.nist.secauto.metaschema.core.metapath.cst.type.TypeTestSupport;
import gov.nist.secauto.metaschema.core.metapath.function.ComparisonFunctions;
import gov.nist.secauto.metaschema.core.metapath.function.IArgument;
import gov.nist.secauto.metaschema.core.metapath.impl.AbstractKeySpecifier;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IIntegerItem;
import gov.nist.secauto.metaschema.core.metapath.item.function.IKeySpecifier;
import gov.nist.secauto.metaschema.core.metapath.type.IAtomicOrUnionType;
import gov.nist.secauto.metaschema.core.metapath.type.IItemType;
import gov.nist.secauto.metaschema.core.metapath.type.ISequenceType;
import gov.nist.secauto.metaschema.core.metapath.type.Occurrence;
import gov.nist.secauto.metaschema.core.qname.IEnhancedQName;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Supports converting a Metapath abstract syntax tree (AST) generated by
 * <a href="https://www.antlr.org/">ANTLRv4</a> into a compact syntax tree
 * (CST).
 */
@SuppressWarnings({
    "PMD.GodClass", "PMD.CyclomaticComplexity", // acceptable complexity
    "PMD.CouplingBetweenObjects" // needed
})
// TODO: Support node comparisons
// https://www.w3.org/TR/xpath-31/#id-node-comparisons
public class BuildCSTVisitor
    extends AbstractCSTVisitorBase {
  @NonNull
  private static final ISequenceType DEFAULT_FUNCTION_SEQUENCE_TYPE
      = ISequenceType.of(IItemType.item(), Occurrence.ZERO_OR_MORE);

  @NonNull
  private final StaticContext context;

  /**
   * Construct a new compact syntax tree generating visitor.
   *
   * @param context
   *          the static Metapath evaluation context
   */
  public BuildCSTVisitor(@NonNull StaticContext context) {
    this.context = context;
  }

  // ============================================================
  // Expressions - https://www.w3.org/TR/xpath-31/#id-expressions
  // ============================================================

  /**
   * Get the static Metapath evaluation context.
   *
   * @return the context
   */
  @NonNull
  protected StaticContext getContext() {
    return context;
  }

  @Override
  protected IExpression handleExpr(Metapath10.ExprContext ctx) {
    return handleNAiryCollection(ctx, children -> {
      assert children != null;
      return new Metapath(children);
    });
  }

  // =================================================================
  // Literal Expressions - https://www.w3.org/TR/xpath-31/#id-literals
  // =================================================================

  @Override
  protected IExpression handleStringLiteral(Metapath10.LiteralContext ctx) {
    ParseTree tree = ctx.getChild(0);
    return new StringLiteral(ObjectUtils.notNull(tree.getText()));
  }

  @Override
  protected IExpression handleNumericLiteral(Metapath10.NumericliteralContext ctx) {
    ParseTree tree = ctx.getChild(0);
    Token token = (Token) tree.getPayload();
    IExpression retval;
    switch (token.getType()) {
    case Metapath10Lexer.IntegerLiteral:
      retval = new IntegerLiteral(new BigInteger(token.getText()));
      break;
    case Metapath10Lexer.DecimalLiteral:
    case Metapath10Lexer.DoubleLiteral:
      retval = new DecimalLiteral(new BigDecimal(token.getText()));
      break;
    default:
      throw new UnsupportedOperationException(token.getText());
    }
    return retval;
  }

  // ==================================================================
  // Variable References - https://www.w3.org/TR/xpath-31/#id-variables
  // ==================================================================

  @Override
  protected IExpression handleVarref(Metapath10.VarrefContext ctx) {
    return new VariableReference(
        getContext().parseVariableName(
            ObjectUtils.notNull(ctx.varname().eqname().getText())));
  }

  // ====================================================================
  // For Expressions - https://www.w3.org/TR/xpath-31/#id-for-expressions
  // ====================================================================

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  @Override
  protected IExpression handleForexpr(Metapath10.ForexprContext ctx) {
    Metapath10.SimpleforclauseContext simpleForClause = ctx.simpleforclause();

    // for SimpleForBinding ("," SimpleForBinding)*
    int bindingCount = simpleForClause.getChildCount() / 2;

    @NonNull
    IExpression retval = ObjectUtils.notNull(ctx.exprsingle().accept(this));

    // step through in reverse
    for (int idx = bindingCount - 1; idx >= 0; idx--) {
      Metapath10.SimpleforbindingContext simpleForBinding = simpleForClause.simpleforbinding(idx);

      Metapath10.VarnameContext varName = simpleForBinding.varname();
      Metapath10.ExprsingleContext exprSingle = simpleForBinding.exprsingle();

      IExpression boundExpression = exprSingle.accept(this);
      assert boundExpression != null;

      IEnhancedQName qname = getContext().parseVariableName(
          ObjectUtils.notNull(varName.eqname().getText()));

      Let.VariableDeclaration variable = new Let.VariableDeclaration(qname, boundExpression);

      retval = new For(variable, retval);
    }
    return retval;
  }

  // ====================================================================
  // Let Expressions - https://www.w3.org/TR/xpath-31/#id-let-expressions
  // ====================================================================

  @Override
  protected IExpression handleLet(Metapath10.LetexprContext context) {
    @NonNull
    IExpression retval = ObjectUtils.notNull(context.exprsingle().accept(this));

    Metapath10.SimpleletclauseContext letClause = context.simpleletclause();
    List<Metapath10.SimpleletbindingContext> clauses = letClause.simpleletbinding();

    ListIterator<Metapath10.SimpleletbindingContext> reverseListIterator = clauses.listIterator(clauses.size());
    while (reverseListIterator.hasPrevious()) {
      Metapath10.SimpleletbindingContext simpleCtx = reverseListIterator.previous();

      IExpression boundExpression = simpleCtx.exprsingle().accept(this);
      assert boundExpression != null;

      IEnhancedQName varName = getContext().parseVariableName(
          ObjectUtils.notNull(simpleCtx.varname().eqname().getText()));

      retval = new Let(varName, boundExpression, retval); // NOPMD intended
    }
    return retval;
  }

  // ======================================================================
  // Map Constructors - https://www.w3.org/TR/xpath-31/#id-map-constructors
  // ======================================================================

  @Override
  protected MapConstructor handleMapConstructor(Metapath10.MapconstructorContext context) {
    return context.getChildCount() == 3
        // empty
        ? new MapConstructor(CollectionUtil.emptyList())
        // with members
        : nairyToCollection(context, 3, 2,
            (ctx, idx) -> {
              int pos = (idx - 3) / 2;
              Metapath10.MapconstructorentryContext entry = ctx.mapconstructorentry(pos);
              assert entry != null;
              return new MapConstructor.Entry(
                  ObjectUtils.notNull(entry.mapkeyexpr().accept(this)),
                  ObjectUtils.notNull(entry.mapvalueexpr().accept(this)));
            },
            children -> {
              assert children != null;
              return new MapConstructor(children);
            });
  }

  // ==============================================================
  // Array Constructors - https://www.w3.org/TR/xpath-31/#id-arrays
  // ==============================================================

  @Override
  protected IExpression handleArrayConstructor(Metapath10.SquarearrayconstructorContext context) {
    return context.getChildCount() == 2
        // empty
        ? new ArraySquareConstructor(CollectionUtil.emptyList())
        // with members
        : nairyToCollection(context, 1, 2,
            (ctx, idx) -> {
              int pos = (idx - 1) / 2;
              ParseTree tree = ctx.exprsingle(pos);
              return visit(tree);
            },
            children -> {
              assert children != null;
              return new ArraySquareConstructor(children);
            });
  }

  @Override
  protected IExpression handleArrayConstructor(Metapath10.CurlyarrayconstructorContext ctx) {
    return new ArraySequenceConstructor(visit(ctx.enclosedexpr()));
  }

  // ===============================================
  // Unary Lookup -
  // https://www.w3.org/TR/xpath-31/#id-unary-lookup
  // ===============================================

  @NonNull
  private IKeySpecifier toKeySpecifier(@NonNull Metapath10.KeyspecifierContext specifier) {
    IKeySpecifier keySpecifier;
    if (specifier.parenthesizedexpr() != null) {
      keySpecifier = AbstractKeySpecifier.newParenthesizedExprKeySpecifier(
          ObjectUtils.requireNonNull(specifier.parenthesizedexpr().accept(this)));
    } else if (specifier.NCName() != null) {
      keySpecifier = AbstractKeySpecifier.newNameKeySpecifier(
          ObjectUtils.requireNonNull(specifier.NCName().getText()));
    } else if (specifier.IntegerLiteral() != null) {
      keySpecifier = AbstractKeySpecifier.newIntegerLiteralKeySpecifier(
          IIntegerItem.valueOf(ObjectUtils.requireNonNull(specifier.IntegerLiteral().getText())));
    } else if (specifier.STAR() != null) {
      keySpecifier = AbstractKeySpecifier.newWildcardKeySpecifier();
    } else {
      throw new UnsupportedOperationException("unknown key specifier");
    }
    return keySpecifier;
  }

  @Override
  protected IExpression handleUnarylookup(Metapath10.UnarylookupContext ctx) {
    return new UnaryLookup(toKeySpecifier(ObjectUtils.requireNonNull(ctx.keyspecifier())));
  }

  // ====================================================
  // Parenthesized Expressions -
  // https://www.w3.org/TR/xpath-31/#id-paren-expressions
  // ====================================================

  @Override
  protected IExpression handleEmptyParenthesizedexpr(Metapath10.ParenthesizedexprContext ctx) {
    return EmptySequence.instance();
  }

  // ==========================================================
  // Context Item Expression -
  // https://www.w3.org/TR/xpath-31/#id-context-item-expression
  // ==========================================================

  @Override
  protected IExpression handleContextitemexpr(Metapath10.ContextitemexprContext ctx) {
    return ContextItem.instance();
  }

  // =========================================================================
  // Static Function Calls - https://www.w3.org/TR/xpath-31/#id-function-calls
  // =========================================================================

  /**
   * Parse a list of arguments.
   *
   * @param context
   *          the argument list AST
   * @return a stream of CST expressions for each argument, in the original
   *         argument order
   */
  @NonNull
  protected Stream<IExpression> parseArgumentList(@NonNull Metapath10.ArgumentlistContext context) {
    int numChildren = context.getChildCount();

    Stream<IExpression> retval;
    if (numChildren == 2) {
      // just the OP CP tokens, which is an empty list
      retval = Stream.empty();
    } else {
      retval = context.argument().stream()
          .map(argument -> argument.exprsingle().accept(this));
    }
    assert retval != null;

    return retval;
  }

  @Override
  protected IExpression handleFunctioncall(Metapath10.FunctioncallContext ctx) {
    List<IExpression> arguments = ObjectUtils.notNull(
        parseArgumentList(ObjectUtils.notNull(ctx.argumentlist()))
            .collect(Collectors.toUnmodifiableList()));

    return new StaticFunctionCall(
        () -> getContext().lookupFunction(
            ObjectUtils.notNull(ctx.eqname().getText()),
            arguments.size()),
        arguments);
  }

  // ============================================================
  // https://www.w3.org/TR/xpath-31/#doc-xpath31-NamedFunctionRef
  // ============================================================

  @Override
  public IExpression visitNamedfunctionref(Metapath10.NamedfunctionrefContext ctx) {
    // Ensure that the default function namespace is used, if needed
    IEnhancedQName qname = getContext().parseFunctionName(ObjectUtils.notNull(ctx.eqname().getText()));
    int arity = IIntegerItem.valueOf(ObjectUtils.requireNonNull(ctx.IntegerLiteral().getText()))
        .asInteger().intValueExact();
    return new NamedFunctionReference(qname, arity);
  }

  // ==============================================
  // https://www.w3.org/TR/xpath-31/#id-inline-func
  // ==============================================

  @Override
  public IExpression handleInlinefunctionexpr(Metapath10.InlinefunctionexprContext context) {
    // parse the param list
    List<IArgument> parameters = ObjectUtils.notNull(context.paramlist() == null
        ? CollectionUtil.emptyList()
        : nairyToList(
            ObjectUtils.notNull(context.paramlist()),
            0,
            2,
            (ctx, idx) -> {
              int pos = (idx - 1) / 2;
              ParamContext tree = ctx.param(pos);
              return IArgument.of(
                  getContext().parseVariableName(ObjectUtils.notNull(tree.eqname().getText())),
                  tree.typedeclaration() == null
                      ? DEFAULT_FUNCTION_SEQUENCE_TYPE
                      : TypeTestSupport.parseSequenceType(
                          ObjectUtils.notNull(tree.typedeclaration().sequencetype()),
                          getContext()));
            }));

    // parse the result type
    ISequenceType resultSequenceType = context.sequencetype() == null
        ? DEFAULT_FUNCTION_SEQUENCE_TYPE
        : TypeTestSupport.parseSequenceType(
            ObjectUtils.notNull(context.sequencetype()),
            getContext());

    // parse the function body
    IExpression body = visit(context.functionbody().enclosedexpr());

    return new AnonymousFunctionCall(parameters, resultSequenceType, body);
  }

  // =========================================================================
  // Filter Expressions - https://www.w3.org/TR/xpath-31/#id-filter-expression
  // =========================================================================

  /**
   * Parse a predicate AST.
   *
   * @param predicate
   *          the predicate expression
   * @return the CST expression generated for the predicate
   */
  @NonNull
  protected IExpression parsePredicate(@NonNull Metapath10.PredicateContext predicate) {
    // the expression is always the second child
    return visit(predicate.getChild(1));
  }

  /**
   * Parse a series of predicate ASTs.
   *
   * @param context
   *          the parse tree node containing the predicates
   * @param staringChild
   *          the first child node corresponding to a predicate
   * @return the list of CST predicate expressions in the same order as the
   *         original predicate list
   */
  @NonNull
  protected List<IExpression> parsePredicates(@NonNull ParseTree context, int staringChild) {
    int numChildren = context.getChildCount();
    int numPredicates = numChildren - staringChild;

    List<IExpression> predicates;
    if (numPredicates == 0) {
      // no predicates
      predicates = CollectionUtil.emptyList();
    } else if (numPredicates == 1) {
      // single predicate
      Metapath10.PredicateContext predicate
          = ObjectUtils.notNull((Metapath10.PredicateContext) context.getChild(staringChild));
      predicates = CollectionUtil.singletonList(parsePredicate(predicate));
    } else {
      // multiple predicates
      predicates = new ArrayList<>(numPredicates);
      for (int i = staringChild; i < numChildren; i++) {
        Metapath10.PredicateContext predicate = ObjectUtils.notNull((Metapath10.PredicateContext) context.getChild(i));
        predicates.add(parsePredicate(predicate));
      }
    }
    return predicates;
  }

  @Override
  protected IExpression handlePostfixexpr(Metapath10.PostfixexprContext context) {
    return handleGroupedNAiry(
        context,
        0,
        1,
        (ctx, idx, left) -> {
          assert left != null;

          ParseTree tree = ctx.getChild(idx);
          IExpression result;
          if (tree instanceof Metapath10.ArgumentlistContext) {
            // map or array access using function call syntax
            result = new FunctionCallAccessor(
                left,
                ObjectUtils.notNull(parseArgumentList((Metapath10.ArgumentlistContext) tree)
                    .collect(Collectors.toUnmodifiableList())));
          } else if (tree instanceof Metapath10.PredicateContext) {
            result = new PredicateExpression(
                left,
                CollectionUtil.singletonList(parsePredicate((Metapath10.PredicateContext) tree)));
          } else if (tree instanceof Metapath10.LookupContext) {
            result = new PostfixLookup(
                left,
                toKeySpecifier(ObjectUtils.notNull(((Metapath10.LookupContext) tree).keyspecifier())));
          } else {
            result = visit(tree);
          }
          return result;
        });
  }

  // ======================================================================
  // Path Expressions - https://www.w3.org/TR/xpath-31/#id-path-expressions
  // ======================================================================

  @Override
  protected IExpression handlePredicate(Metapath10.PredicateContext ctx) {
    return parsePredicate(ctx);
  }

  @Override
  protected IExpression handleLookup(Metapath10.LookupContext ctx) {
    throw new UnsupportedOperationException("needs to be implemented");
  }

  @Override
  protected IExpression handlePathexpr(Metapath10.PathexprContext ctx) {
    int numChildren = ctx.getChildCount();

    IExpression retval;
    ParseTree tree = ctx.getChild(0);
    if (tree instanceof TerminalNode) {
      int type = ((TerminalNode) tree).getSymbol().getType();
      switch (type) {
      case Metapath10Lexer.SLASH:
        // a slash expression with optional path
        if (numChildren == 2) {
          // the optional path
          retval = new RootSlashPath(visit(ctx.getChild(1)));
        } else {
          retval = new RootSlashOnlyPath();
        }
        break;
      case Metapath10Lexer.SS:
        // a double slash expression with path
        retval = new RootDoubleSlashPath(visit(ctx.getChild(1)));
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) tree).getSymbol().getText());
      }
    } else {
      // a relative expression or something else
      retval = visit(tree);
    }
    return retval;
  }

  // ============================================================
  // RelativePath Expressions -
  // https://www.w3.org/TR/xpath-31/#id-relative-path-expressions
  // ============================================================

  @Override
  protected IExpression handleRelativepathexpr(Metapath10.RelativepathexprContext context) {
    return handleGroupedNAiry(context, 0, 2, (ctx, idx, left) -> {
      assert left != null;

      ParseTree operatorTree = ctx.getChild(idx);
      IExpression right = visit(ctx.getChild(idx + 1));

      int type = ((TerminalNode) operatorTree).getSymbol().getType();

      IExpression retval;
      switch (type) {
      case Metapath10Lexer.SLASH:
        retval = new RelativeSlashPath(left, right);
        break;
      case Metapath10Lexer.SS:
        retval = new RelativeDoubleSlashPath(left, right);
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) operatorTree).getSymbol().getText());
      }
      return retval;
    });
  }

  // ================================================
  // Steps - https://www.w3.org/TR/xpath-31/#id-steps
  // ================================================

  @Override
  protected IExpression handleForwardstep(Metapath10.ForwardstepContext ctx) {
    Metapath10.AbbrevforwardstepContext abbrev = ctx.abbrevforwardstep();

    Step retval;
    if (abbrev == null) {
      assert ctx.getChildCount() == 2;

      Token token = (Token) ctx.forwardaxis().getChild(0).getPayload();

      Axis axis;
      switch (token.getType()) {
      case Metapath10Lexer.KW_SELF:
        axis = Axis.SELF;
        break;
      case Metapath10Lexer.KW_CHILD:
        axis = Axis.CHILDREN;
        break;
      case Metapath10Lexer.KW_DESCENDANT:
        axis = Axis.DESCENDANT;
        break;
      case Metapath10Lexer.KW_DESCENDANT_OR_SELF:
        axis = Axis.DESCENDANT_OR_SELF;
        break;
      case Metapath10Lexer.KW_FLAG:
        axis = Axis.FLAG;
        break;
      case Metapath10Lexer.KW_FOLLOWING_SIBLING:
        axis = Axis.FOLLOWING_SIBLING;
        break;
      case Metapath10Lexer.KW_FOLLOWING:
        axis = Axis.FOLLOWING;
        break;
      default:
        throw new UnsupportedOperationException(token.getText());
      }
      retval = new Step(axis,
          parseNodeTest(ctx.nodetest(), false));
    } else {
      retval = new Step(
          Axis.CHILDREN,
          parseNodeTest(ctx.nodetest(), abbrev.AT() != null));
    }
    return retval;
  }

  @Override
  protected IExpression handleReversestep(Metapath10.ReversestepContext ctx) {
    assert ctx.getChildCount() == 2;

    Token token = (Token) ctx.reverseaxis().getChild(0).getPayload();

    Axis axis;
    switch (token.getType()) {
    case Metapath10Lexer.KW_PARENT:
      axis = Axis.PARENT;
      break;
    case Metapath10Lexer.KW_ANCESTOR:
      axis = Axis.ANCESTOR;
      break;
    case Metapath10Lexer.KW_ANCESTOR_OR_SELF:
      axis = Axis.ANCESTOR_OR_SELF;
      break;
    case Metapath10Lexer.KW_PRECEDING_SIBLING:
      axis = Axis.PRECEDING_SIBLING;
      break;
    case Metapath10Lexer.KW_PRECEDING:
      axis = Axis.PRECEDING;
      break;
    default:
      throw new UnsupportedOperationException(token.getText());
    }
    return new Step(axis, parseNodeTest(ctx.nodetest(), false));
  }

  // =======================================================
  // Node Tests - https://www.w3.org/TR/xpath-31/#node-tests
  // =======================================================

  /**
   * Parse an antlr node test expression.
   *
   * @param ctx
   *          the antrl context
   * @param flag
   *          if the context is within a flag's scope
   * @return the resulting expression
   */
  @NonNull
  protected INodeTestExpression parseNodeTest(Metapath10.NodetestContext ctx, boolean flag) {
    INodeTestExpression retval;
    if (ctx.kindtest() != null) {
      IItemType itemType = TypeTestSupport.parseKindTest(
          ObjectUtils.notNull(ctx.kindtest()),
          getContext());
      retval = new KindNodeTest(itemType);
    } else {
      Metapath10.NametestContext nameTestCtx = ctx.nametest();
      retval = parseNameTest(nameTestCtx, flag);
    }
    return retval;
  }

  /**
   * Parse an antlr name test expression.
   *
   * @param ctx
   *          the antrl context
   * @param flag
   *          if the context is within a flag's scope
   * @return the resulting expression
   */
  @NonNull
  protected INodeTestExpression parseNameTest(Metapath10.NametestContext ctx, boolean flag) {
    ParseTree testType = ObjectUtils.requireNonNull(ctx.getChild(0));

    StaticContext staticContext = getContext();

    INodeTestExpression retval;
    if (testType instanceof Metapath10.EqnameContext) {
      String name = ObjectUtils.notNull(ctx.eqname().getText());
      IEnhancedQName qname = flag
          ? staticContext.parseFlagName(name)
          : staticContext.parseModelName(name);

      if (!flag
          && qname.getNamespace().isEmpty()
          && staticContext.isUseWildcardWhenNamespaceNotDefaulted()) {
        // Use a wildcard namespace
        retval = new WildcardNodeTest(IWildcardMatcher.anyNamespace(ObjectUtils.notNull(qname.getLocalName())));
      } else {
        retval = new NameNodeTest(qname);
      }
    } else { // wildcard
      retval = handleWildcard((Metapath10.WildcardContext) testType);
    }
    return retval;
  }

  @Override
  protected WildcardNodeTest handleWildcard(Metapath10.WildcardContext ctx) {
    IWildcardMatcher matcher = null;
    if (ctx.STAR() == null) {
      if (ctx.CS() != null) {
        // specified prefix, any local-name
        String prefix = ObjectUtils.notNull(ctx.NCName().getText());
        String namespace = getContext().lookupNamespaceForPrefix(prefix);
        if (namespace == null) {
          throw new IllegalStateException(String.format("Prefix '%s' did not map to a namespace.", prefix));
        }
        matcher = IWildcardMatcher.anyLocalName(namespace);
      } else if (ctx.SC() != null) {
        // any prefix, specified local-name
        matcher = IWildcardMatcher.anyNamespace(ObjectUtils.notNull(ctx.NCName().getText()));
      } else {
        // specified braced namespace, any local-name
        String bracedUriLiteral = ctx.BracedURILiteral().getText();
        String namespace = ObjectUtils.notNull(bracedUriLiteral.substring(2, bracedUriLiteral.length() - 1));
        matcher = IWildcardMatcher.anyLocalName(namespace);
      }
    } // star needs no matcher: any prefix, any local-name

    return new WildcardNodeTest(matcher);
  }

  // ======================================================================
  // Predicates within Steps - https://www.w3.org/TR/xpath-31/#id-predicate
  // ======================================================================

  @Override
  protected IExpression handleAxisstep(Metapath10.AxisstepContext ctx) {
    IExpression step = visit(ctx.getChild(0));
    ParseTree predicateTree = ctx.getChild(1);
    assert predicateTree != null;

    List<IExpression> predicates = parsePredicates(predicateTree, 0);

    return predicates.isEmpty() ? step : new PredicateExpression(step, predicates);
  }

  // ===========================================================
  // Abbreviated Syntax - https://www.w3.org/TR/xpath-31/#abbrev
  // ===========================================================

  @Override
  protected IExpression handleAbbrevforwardstep(Metapath10.AbbrevforwardstepContext ctx) {
    int numChildren = ctx.getChildCount();

    IExpression retval;
    if (numChildren == 1) {
      retval = new ModelInstance(parseNodeTest(ctx.nodetest(), false));
    } else {
      // this is an AT test
      retval = new Flag(parseNodeTest(ctx.nodetest(), true));
    }
    return retval;
  }

  @Override
  protected IExpression handleAbbrevreversestep(Metapath10.AbbrevreversestepContext ctx) {
    return Axis.PARENT;
  }

  // ======================================================================
  // Constructing Sequences - https://www.w3.org/TR/xpath-31/#construct_seq
  // ======================================================================

  @Override
  protected IExpression handleRangeexpr(Metapath10.RangeexprContext ctx) {
    assert ctx.getChildCount() == 3;

    IExpression left = visit(ctx.getChild(0));
    IExpression right = visit(ctx.getChild(2));

    return new Range(left, right);
  }

  // ========================================================================
  // Combining Node Sequences - https://www.w3.org/TR/xpath-31/#combining_seq
  // ========================================================================

  @Override
  protected IExpression handleUnionexpr(Metapath10.UnionexprContext ctx) {
    return handleNAiryCollection(ctx, children -> {
      assert children != null;
      return new Union(children);
    });
  }

  @Override
  protected IExpression handleIntersectexceptexpr(Metapath10.IntersectexceptexprContext context) {
    return handleGroupedNAiry(context, 0, 2, (ctx, idx, left) -> {
      assert left != null;

      ParseTree operatorTree = ctx.getChild(idx);
      IExpression right = visit(ctx.getChild(idx + 1));

      int type = ((TerminalNode) operatorTree).getSymbol().getType();

      IExpression retval;
      switch (type) {
      case Metapath10Lexer.KW_INTERSECT:
        retval = new Intersect(left, right);
        break;
      case Metapath10Lexer.KW_EXCEPT:
        retval = new Except(left, right);
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) operatorTree).getSymbol().getText());
      }
      return retval;
    });
  }

  // ======================================================================
  // Arithmetic Expressions - https://www.w3.org/TR/xpath-31/#id-arithmetic
  // ======================================================================

  @Override
  protected IExpression handleAdditiveexpr(Metapath10.AdditiveexprContext context) {
    return handleGroupedNAiry(context, 0, 2, (ctx, idx, left) -> {
      ParseTree operatorTree = ctx.getChild(idx);
      ParseTree rightTree = ctx.getChild(idx + 1);
      IExpression right = rightTree.accept(this);

      assert left != null;
      assert right != null;

      int type = ((TerminalNode) operatorTree).getSymbol().getType();

      IExpression retval;
      switch (type) {
      case Metapath10Lexer.PLUS:
        retval = new Addition(left, right);
        break;
      case Metapath10Lexer.MINUS:
        retval = new Subtraction(left, right);
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) operatorTree).getSymbol().getText());
      }
      return retval;
    });
  }

  @Override
  protected IExpression handleMultiplicativeexpr(Metapath10.MultiplicativeexprContext context) {
    return handleGroupedNAiry(context, 0, 2, (ctx, idx, left) -> {
      assert left != null;

      ParseTree operatorTree = ctx.getChild(idx);
      IExpression right = visit(ctx.getChild(idx + 1));

      assert right != null;

      int type = ((TerminalNode) operatorTree).getSymbol().getType();
      IExpression retval;
      switch (type) {
      case Metapath10Lexer.STAR:
        retval = new Multiplication(left, right);
        break;
      case Metapath10Lexer.KW_DIV:
        retval = new Division(left, right);
        break;
      case Metapath10Lexer.KW_IDIV:
        retval = new IntegerDivision(left, right);
        break;
      case Metapath10Lexer.KW_MOD:
        retval = new Modulo(left, right);
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) operatorTree).getSymbol().getText());
      }
      return retval;
    });
  }

  @Override
  protected IExpression handleUnaryexpr(Metapath10.UnaryexprContext ctx) {
    int numChildren = ctx.getChildCount();
    int negateCount = 0;

    int idx = 0;
    for (; idx < numChildren - 1; idx++) {
      ParseTree tree = ctx.getChild(idx);
      int type = ((TerminalNode) tree).getSymbol().getType();
      switch (type) {
      case Metapath10Lexer.PLUS:
        break;
      case Metapath10Lexer.MINUS:
        negateCount++;
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) tree).getSymbol().getText());
      }
    }

    IExpression retval = visit(ctx.getChild(idx));
    if (negateCount % 2 != 0) {
      retval = new Negate(retval);
    }
    return retval;
  }

  // =====================================================
  // String Concatenation Expressions -
  // https://www.w3.org/TR/xpath-31/#id-string-concat-expr
  // =====================================================

  @Override
  protected IExpression handleStringconcatexpr(Metapath10.StringconcatexprContext ctx) {
    return handleNAiryCollection(ctx, children -> {
      assert children != null;
      return new StringConcat(children);
    });
  }

  // =======================================================================
  // Comparison Expressions - https://www.w3.org/TR/xpath-31/#id-comparisons
  // =======================================================================

  @Override
  protected IExpression handleComparisonexpr(Metapath10.ComparisonexprContext ctx) { // NOPMD - ok
    assert ctx.getChildCount() == 3;

    IExpression left = visit(ctx.getChild(0));
    IExpression right = visit(ctx.getChild(2));

    // the operator
    ParseTree operatorTree = ctx.getChild(1);
    Object payload = operatorTree.getPayload();

    ComparisonFunctions.Operator operator;
    IBooleanLogicExpression retval;
    if (payload instanceof Metapath10.GeneralcompContext) {
      Metapath10.GeneralcompContext compContext = (Metapath10.GeneralcompContext) payload;
      int type = ((TerminalNode) compContext.getChild(0)).getSymbol().getType();
      switch (type) {
      case Metapath10Lexer.EQ:
        operator = ComparisonFunctions.Operator.EQ;
        break;
      case Metapath10Lexer.NE:
        operator = ComparisonFunctions.Operator.NE;
        break;
      case Metapath10Lexer.LT:
        operator = ComparisonFunctions.Operator.LT;
        break;
      case Metapath10Lexer.LE:
        operator = ComparisonFunctions.Operator.LE;
        break;
      case Metapath10Lexer.GT:
        operator = ComparisonFunctions.Operator.GT;
        break;
      case Metapath10Lexer.GE:
        operator = ComparisonFunctions.Operator.GE;
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) compContext.getChild(0)).getSymbol().getText());
      }
      retval = new GeneralComparison(left, operator, right);
    } else if (payload instanceof Metapath10.ValuecompContext) {
      Metapath10.ValuecompContext compContext = (Metapath10.ValuecompContext) payload;
      int type = ((TerminalNode) compContext.getChild(0)).getSymbol().getType();
      switch (type) {
      case Metapath10Lexer.KW_EQ:
        operator = ComparisonFunctions.Operator.EQ;
        break;
      case Metapath10Lexer.KW_NE:
        operator = ComparisonFunctions.Operator.NE;
        break;
      case Metapath10Lexer.KW_LT:
        operator = ComparisonFunctions.Operator.LT;
        break;
      case Metapath10Lexer.KW_LE:
        operator = ComparisonFunctions.Operator.LE;
        break;
      case Metapath10Lexer.KW_GT:
        operator = ComparisonFunctions.Operator.GT;
        break;
      case Metapath10Lexer.KW_GE:
        operator = ComparisonFunctions.Operator.GE;
        break;
      default:
        throw new UnsupportedOperationException(((TerminalNode) compContext.getChild(0)).getSymbol().getText());
      }
      retval = new ValueComparison(left, operator, right);
    } else {
      throw new UnsupportedOperationException();
    }
    return retval;
  }

  // ============================================================================
  // Logical Expressions - https://www.w3.org/TR/xpath-31/#id-logical-expressions
  // ============================================================================

  @Override
  protected IExpression handleOrexpr(Metapath10.OrexprContext ctx) {
    return handleNAiryCollection(ctx, children -> {
      assert children != null;
      return new Or(children);
    });
  }

  @Override
  protected IExpression handleAndexpr(Metapath10.AndexprContext ctx) {
    return handleNAiryCollection(ctx, children -> {
      assert children != null;
      return new And(children);
    });
  }

  // =========================================================================
  // Conditional Expressions - https://www.w3.org/TR/xpath-31/#id-conditionals
  // =========================================================================

  @Override
  protected IExpression handleIfexpr(Metapath10.IfexprContext ctx) {
    IExpression testExpr = visit(ctx.expr());
    IExpression thenExpr = visit(ctx.exprsingle(0));
    IExpression elseExpr = visit(ctx.exprsingle(1));

    return new If(testExpr, thenExpr, elseExpr);
  }

  // =========================================================
  // Quantified Expressions -
  // https://www.w3.org/TR/xpath-31/#id-quantified-expressions
  // =========================================================

  @Override
  protected IExpression handleQuantifiedexpr(Metapath10.QuantifiedexprContext ctx) {
    Quantified.Quantifier quantifier;
    int type = ((TerminalNode) ctx.getChild(0)).getSymbol().getType();
    switch (type) {
    case Metapath10Lexer.KW_SOME:
      quantifier = Quantified.Quantifier.SOME;
      break;
    case Metapath10Lexer.KW_EVERY:
      quantifier = Quantified.Quantifier.EVERY;
      break;
    default:
      throw new UnsupportedOperationException(((TerminalNode) ctx.getChild(0)).getSymbol().getText());
    }

    int numVars = (ctx.getChildCount() - 2) / 5; // children - "satisfies expr" / ", $ varName in expr"
    Map<IEnhancedQName, IExpression> vars = new LinkedHashMap<>(); // NOPMD ordering needed
    int offset = 0;
    for (; offset < numVars; offset++) {
      // $
      IEnhancedQName varName = getContext().parseVariableName(ObjectUtils.notNull(
          ctx.varname(offset).eqname().getText()));

      // in
      IExpression varExpr = visit(ctx.exprsingle(offset));

      vars.put(varName, varExpr);
    }

    IExpression satisfies = visit(ctx.exprsingle(offset));

    return new Quantified(quantifier, vars, satisfies);
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
  @Override
  protected IExpression handleInstanceofexpr(@NonNull Metapath10.InstanceofexprContext ctx) {
    IExpression left = visit(ctx.treatexpr());
    ISequenceType sequenceType = TypeTestSupport.parseSequenceType(
        ObjectUtils.notNull(ctx.sequencetype()),
        getContext());
    return new InstanceOf(left, sequenceType);
  }

  // ==============================================
  // cast - https://www.w3.org/TR/xpath-31/#id-cast
  // ==============================================

  @Override
  protected IExpression handleCastexpr(Metapath10.CastexprContext ctx) {
    IExpression left = visit(ctx.arrowexpr());

    Metapath10.SingletypeContext singleType = ObjectUtils.notNull(ctx.singletype());

    boolean allowEmptySequence = singleType.QM() != null;

    IAtomicOrUnionType<?> type = getTypeForCast(ObjectUtils.notNull(singleType.simpletypename().getText()));

    return new Cast(left, type, allowEmptySequence);
  }

  // ==================================================
  // castable - https://www.w3.org/TR/xpath-31/#id-cast
  // ==================================================

  @NonNull
  private IAtomicOrUnionType<?> getTypeForCast(@NonNull String name) {
    IAtomicOrUnionType<?> type;
    try {
      type = getContext().lookupAtomicType(name);
    } catch (StaticMetapathException ex) {
      if (StaticMetapathException.UNKNOWN_TYPE == ex.getCode()) {
        throw new StaticMetapathException(StaticMetapathException.CAST_UNKNOWN_TYPE, ex);
      }
      throw ex;
    }

    if (IItemType.anyAtomic().equals(type)) {
      throw new StaticMetapathException(
          StaticMetapathException.CAST_ANY_ATOMIC,
          String.format("Type cannot be '%s',", IItemType.anyAtomic()));
    }
    return type;
  }

  @Override
  protected IExpression handleCastableexpr(Metapath10.CastableexprContext ctx) {
    IExpression left = visit(ctx.castexpr());

    Metapath10.SingletypeContext singleType = ObjectUtils.notNull(ctx.singletype());

    boolean allowEmptySequence = singleType.QM() != null;

    IAtomicOrUnionType<?> type = getTypeForCast(ObjectUtils.notNull(singleType.simpletypename().getText()));

    return new Castable(left, type, allowEmptySequence);
  }

  // ================================================
  // treat - https://www.w3.org/TR/xpath-31/#id-treat
  // ================================================

  @Override
  protected IExpression handleTreatexpr(Metapath10.TreatexprContext ctx) {
    IExpression left = visit(ctx.castableexpr());

    ISequenceType sequenceType = TypeTestSupport.parseSequenceType(
        ObjectUtils.notNull(ctx.sequencetype()),
        getContext());

    return new Treat(left, sequenceType);
  }

  // =========================================================================
  // Simple map operator (!) - https://www.w3.org/TR/xpath-31/#id-map-operator
  // =========================================================================

  @Override
  protected IExpression handleSimplemapexpr(Metapath10.SimplemapexprContext context) {
    return handleGroupedNAiry(context, 0, 2, (ctx, idx, left) -> {
      assert left != null;

      // the next child is "!"
      assert "!".equals(ctx.getChild(idx).getText());
      IExpression right = ObjectUtils.notNull(ctx.getChild(idx + 1).accept(this));

      return new SimpleMap(left, right);
    });
  }

  // =======================================================================
  // Arrow operator (=>) - https://www.w3.org/TR/xpath-31/#id-arrow-operator
  // =======================================================================

  @Override
  protected IExpression handleArrowexpr(Metapath10.ArrowexprContext context) {
    return handleGroupedNAiry(context, 0, 3, (ctx, idx, left) -> {
      // the next child is "=>"
      assert "=>".equals(ctx.getChild(idx).getText());

      int offset = (idx - 1) / 3;

      Metapath10.ArgumentlistContext argumentCtx = ctx.getChild(Metapath10.ArgumentlistContext.class, offset);

      try (Stream<IExpression> args = Stream.concat(
          Stream.of(left),
          parseArgumentList(ObjectUtils.notNull(argumentCtx)))) {
        assert args != null;

        // prepend the focus
        List<IExpression> arguments = ObjectUtils.notNull(args
            .collect(Collectors.toUnmodifiableList()));

        Metapath10.ArrowfunctionspecifierContext arrowCtx
            = ctx.getChild(Metapath10.ArrowfunctionspecifierContext.class, offset);
        if (arrowCtx.eqname() != null) {
          // named function
          return new StaticFunctionCall(
              () -> getContext().lookupFunction(ObjectUtils.notNull(arrowCtx.eqname().getText()), arguments.size()),
              arguments);
        }

        IExpression result;
        if (arrowCtx.varref() != null) {
          // function instance or name reference
          result = new VariableReference(getContext().parseVariableName(
              ObjectUtils.notNull(arrowCtx.varref().varname().eqname().getText())));
        } else if (arrowCtx.parenthesizedexpr() != null) {
          // function expression
          result = visit(arrowCtx.parenthesizedexpr().expr());
        } else {
          throw new StaticMetapathException(
              StaticMetapathException.INVALID_PATH_GRAMMAR,
              String.format("Unable to get function name using arrow specifier '%s'.", arrowCtx.getText()));
        }

        return new DynamicFunctionCall(
            result,
            arguments);
      }
    });
  }
}
