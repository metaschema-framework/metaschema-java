/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath;

import gov.nist.secauto.metaschema.core.metapath.antlr.FailingErrorListener;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10;
import gov.nist.secauto.metaschema.core.metapath.antlr.Metapath10Lexer;
import gov.nist.secauto.metaschema.core.metapath.antlr.ParseTreePrinter;
import gov.nist.secauto.metaschema.core.metapath.cst.BuildCSTVisitor;
import gov.nist.secauto.metaschema.core.metapath.cst.CSTPrinter;
import gov.nist.secauto.metaschema.core.metapath.cst.IExpression;
import gov.nist.secauto.metaschema.core.metapath.cst.path.ContextItem;
import gov.nist.secauto.metaschema.core.metapath.function.FunctionUtils;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnBoolean;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnData;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IDecimalItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Supports compiling and executing Metapath expressions.
 */
@SuppressWarnings({
    "PMD.CouplingBetweenObjects" // necessary since this class aggregates functionality
})
public class MetapathExpression {
  /**
   * Identifies the expected type for a Metapath evaluation result.
   */
  public enum ResultType {
    /**
     * The result is expected to be a {@link BigDecimal} value.
     */
    NUMBER,
    /**
     * The result is expected to be a {@link String} value.
     */
    STRING,
    /**
     * The result is expected to be a {@link Boolean} value.
     */
    BOOLEAN,
    /**
     * The result is expected to be an {@link ISequence} value.
     */
    SEQUENCE,
    /**
     * The result is expected to be an {@link IItem} value.
     */
    ITEM;
  }

  private static final Logger LOGGER = LogManager.getLogger(MetapathExpression.class);

  /**
   * The Metapath expression identifying the current context node.
   */
  @NonNull
  public static final MetapathExpression CONTEXT_NODE
      = new MetapathExpression(".", ContextItem.instance(), StaticContext.instance());

  @NonNull
  private final String path;
  @NonNull
  private final IExpression expression;
  @NonNull
  private final StaticContext staticContext;

  /**
   * Compiles a Metapath expression string.
   *
   * @param path
   *          the metapath expression
   * @return the compiled expression object
   * @throws MetapathException
   *           if an error occurred while compiling the Metapath expression
   */
  @NonNull
  public static MetapathExpression compile(@NonNull String path) {
    return compile(path, StaticContext.instance());
  }

  /**
   * Compiles a Metapath expression string using the provided static context.
   *
   * @param path
   *          the metapath expression
   * @param context
   *          the static evaluation context
   * @return the compiled expression object
   * @throws MetapathException
   *           if an error occurred while compiling the Metapath expression
   */
  @NonNull
  public static MetapathExpression compile(@NonNull String path, @NonNull StaticContext context) {
    @NonNull
    MetapathExpression retval;
    if (".".equals(path)) {
      retval = CONTEXT_NODE;
    } else {
      try {
        Metapath10Lexer lexer = new Metapath10Lexer(CharStreams.fromString(path));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new FailingErrorListener());

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Metapath10 parser = new Metapath10(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new FailingErrorListener());
        parser.setErrorHandler(new DefaultErrorStrategy() {

          @Override
          public void sync(Parser recognizer) {
            // disable
          }
        });

        ParseTree tree = ObjectUtils.notNull(parser.expr());

        if (LOGGER.isDebugEnabled()) {
          try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (PrintStream ps = new PrintStream(os, true, StandardCharsets.UTF_8)) {
              ParseTreePrinter printer = new ParseTreePrinter(ps);
              printer.print(tree, Metapath10.ruleNames);
              ps.flush();
            }
            LOGGER.atDebug().log(String.format("Metapath AST:%n%s", os.toString(StandardCharsets.UTF_8)));
          } catch (IOException ex) {
            LOGGER.atError().withThrowable(ex).log("An unexpected error occurred while closing the steam.");
          }
        }

        IExpression expr = new BuildCSTVisitor(context).visit(tree);

        if (LOGGER.isDebugEnabled()) {
          LOGGER.atDebug().log(String.format("Metapath CST:%n%s", CSTPrinter.toString(expr)));
        }
        retval = new MetapathExpression(path, expr, context);
      } catch (MetapathException | ParseCancellationException ex) {
        String msg = String.format("Unable to compile Metapath '%s'", path);
        LOGGER.atError().withThrowable(ex).log(msg);
        throw new StaticMetapathException(StaticMetapathException.INVALID_PATH_GRAMMAR, msg, ex);
      }
    }
    return retval;
  }

  /**
   * Construct a new Metapath expression.
   *
   * @param path
   *          the Metapath as a string
   * @param expr
   *          the Metapath as a compiled abstract syntax tree (AST)
   * @param staticContext
   *          the static evaluation context
   */
  protected MetapathExpression(
      @NonNull String path,
      @NonNull IExpression expr,
      @NonNull StaticContext staticContext) {
    this.path = path;
    this.expression = expr;
    this.staticContext = staticContext;
  }

  /**
   * Get the original Metapath expression as a string.
   *
   * @return the expression
   */
  @NonNull
  public String getPath() {
    return path;
  }

  /**
   * Get the compiled abstract syntax tree (AST) representation of the Metapath.
   *
   * @return the Metapath AST
   */
  @NonNull
  protected IExpression getASTNode() {
    return expression;
  }

  /**
   * Get the static context used to compile this Metapath.
   *
   * @return the static context
   */
  @NonNull
  protected StaticContext getStaticContext() {
    return staticContext;
  }

  @Override
  public String toString() {
    return CSTPrinter.toString(getASTNode());
  }

  /**
   * Evaluate this Metapath expression without a specific focus. The required
   * result type will be determined by the {@code resultType} argument.
   *
   * @param <T>
   *          the expected result type
   * @param resultType
   *          the type of result to produce
   * @return the converted result
   * @throws TypeMetapathException
   *           if the provided sequence is incompatible with the requested result
   *           type
   * @throws MetapathException
   *           if an error occurred during evaluation
   * @see #toResultType(ISequence, ResultType)
   */
  @Nullable
  public <T> T evaluateAs(@NonNull ResultType resultType) {
    return evaluateAs(null, resultType);
  }

  /**
   * Evaluate this Metapath expression using the provided {@code focus} as the
   * initial evaluation context. The required result type will be determined by
   * the {@code resultType} argument.
   *
   * @param <T>
   *          the expected result type
   * @param focus
   *          the outer focus of the expression
   * @param resultType
   *          the type of result to produce
   * @return the converted result
   * @throws TypeMetapathException
   *           if the provided sequence is incompatible with the requested result
   *           type
   * @throws MetapathException
   *           if an error occurred during evaluation
   * @see #toResultType(ISequence, ResultType)
   */
  @Nullable
  public <T> T evaluateAs(
      @Nullable IItem focus,
      @NonNull ResultType resultType) {
    ISequence<?> result = evaluate(focus);
    return toResultType(result, resultType);
  }

  /**
   * Evaluate this Metapath expression using the provided {@code focus} as the
   * initial evaluation context. The specific result type will be determined by
   * the {@code resultType} argument.
   * <p>
   * This variant allow for reuse of a provided {@code dynamicContext}.
   *
   * @param <T>
   *          the expected result type
   * @param focus
   *          the outer focus of the expression
   * @param resultType
   *          the type of result to produce
   * @param dynamicContext
   *          the dynamic context to use for evaluation
   * @return the converted result
   * @throws TypeMetapathException
   *           if the provided sequence is incompatible with the requested result
   *           type
   * @throws MetapathException
   *           if an error occurred during evaluation
   * @see #toResultType(ISequence, ResultType)
   */
  @Nullable
  public <T> T evaluateAs(
      @Nullable IItem focus,
      @NonNull ResultType resultType,
      @NonNull DynamicContext dynamicContext) {
    ISequence<?> result = evaluate(focus, dynamicContext);
    return toResultType(result, resultType);
  }

  /**
   * Converts the provided {@code sequence} to the requested {@code resultType}.
   * <p>
   * The {@code resultType} determines the returned result, which is derived from
   * the evaluation result sequence, as follows:
   * <ul>
   * <li>BOOLEAN - the effective boolean result is produced using
   * {@link FnBoolean#fnBoolean(ISequence)}.</li>
   * <li>NODE - the first result item in the sequence is returned.</li>
   * <li>NUMBER - the sequence is cast to a number using
   * {@link IDecimalItem#cast(IAnyAtomicItem)}.</li>
   * <li>SEQUENCE - the evaluation result sequence.</li>
   * <li>STRING - the string value of the first result item in the sequence.</li>
   * </ul>
   *
   * @param <T>
   *          the requested return value
   * @param sequence
   *          the sequence to convert
   * @param resultType
   *          the type of result to produce
   * @return the converted result
   * @throws TypeMetapathException
   *           if the provided sequence is incompatible with the requested result
   *           type
   */
  @SuppressWarnings({ "PMD.NullAssignment", "PMD.CyclomaticComplexity" }) // for readability
  @Nullable
  protected <T> T toResultType(@NonNull ISequence<?> sequence, @NonNull ResultType resultType) {
    Object result;
    switch (resultType) {
    case BOOLEAN:
      result = FnBoolean.fnBoolean(sequence).toBoolean();
      break;
    case ITEM:
      result = sequence.getFirstItem(true);
      break;
    case NUMBER:
      INumericItem numeric = FunctionUtils.toNumeric(sequence, true);
      result = numeric == null ? null : numeric.asDecimal();
      break;
    case SEQUENCE:
      result = sequence;
      break;
    case STRING:
      IAnyAtomicItem item = FnData.fnData(sequence).getFirstItem(true);
      result = item == null ? "" : item.asString();
      break;
    default:
      throw new InvalidTypeMetapathException(null, String.format("unsupported result type '%s'", resultType.name()));
    }

    return ObjectUtils.asNullableType(result);
  }

  /**
   * Evaluate this Metapath expression without a specific focus.
   *
   * @param <T>
   *          the type of items contained in the resulting sequence
   * @return a sequence of Metapath items representing the result of the
   *         evaluation
   * @throws MetapathException
   *           if an error occurred during evaluation
   */
  @NonNull
  public <T extends IItem> ISequence<T> evaluate() {
    return evaluate((IItem) null);
  }

  /**
   * Evaluate this Metapath expression using the provided {@code focus} as the
   * initial evaluation context.
   *
   * @param <T>
   *          the type of items contained in the resulting sequence
   * @param focus
   *          the outer focus of the expression
   * @return a sequence of Metapath items representing the result of the
   *         evaluation
   * @throws MetapathException
   *           if an error occurred during evaluation
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public <T extends IItem> ISequence<T> evaluate(
      @Nullable IItem focus) {
    return (ISequence<T>) evaluate(focus, new DynamicContext(getStaticContext()));
  }

  /**
   * Evaluate this Metapath expression using the provided {@code focus} as the
   * initial evaluation context.
   * <p>
   * This variant allow for reuse of a provided {@code dynamicContext}.
   *
   * @param <T>
   *          the type of items contained in the resulting sequence
   * @param focus
   *          the outer focus of the expression
   * @param dynamicContext
   *          the dynamic context to use for evaluation
   * @return a sequence of Metapath items representing the result of the
   *         evaluation
   * @throws MetapathException
   *           if an error occurred during evaluation
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public <T extends IItem> ISequence<T> evaluate(
      @Nullable IItem focus,
      @NonNull DynamicContext dynamicContext) {
    try {
      return (ISequence<T>) getASTNode().accept(dynamicContext, ISequence.of(focus));
    } catch (MetapathException ex) { // NOPMD - intentional
      throw new MetapathException(
          String.format("An error occurred while evaluating the expression '%s'. %s",
              getPath(),
              ex.getLocalizedMessage()),
          ex);
    }
  }
}
