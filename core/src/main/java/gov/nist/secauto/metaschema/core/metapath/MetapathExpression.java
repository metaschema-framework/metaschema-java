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
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.INumericItem;
import gov.nist.secauto.metaschema.core.metapath.type.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.type.TypeMetapathException;
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
    NUMBER(BigDecimal.class, sequence -> {
      INumericItem numeric = FunctionUtils.toNumeric(sequence, true);
      return numeric == null ? null : numeric.asDecimal();
    }),
    /**
     * The result is expected to be a {@link String} value.
     */
    STRING(String.class, sequence -> {
      IAnyAtomicItem item = sequence.atomize().getFirstItem(true);
      return item == null ? "" : item.asString();
    }),
    /**
     * The result is expected to be a {@link Boolean} value.
     */
    BOOLEAN(Boolean.class, sequence -> FnBoolean.fnBoolean(sequence).toBoolean()),
    /**
     * The result is expected to be an {@link ISequence} value.
     */
    SEQUENCE(ISequence.class, sequence -> sequence),
    /**
     * The result is expected to be an {@link IItem} value.
     */
    ITEM(IItem.class, sequence -> sequence.getFirstItem(true));

    @NonNull
    private final Class<?> clazz;
    private final ConversionFunction converter;

    ResultType(@NonNull Class<?> clazz, @NonNull ConversionFunction converter) {
      this.clazz = clazz;
      this.converter = converter;
    }

    /**
     * Get the expected class for the result type.
     *
     * @return the expected class
     *
     */
    @NonNull
    public Class<?> expectedClass() {
      return clazz;
    }

    /**
     * Convert the provided sequence to the expected type.
     *
     * @param <T>
     *          the Java type of the expected return value
     * @param sequence
     *          the Metapath result sequence to convert
     * @return the converted sequence as the expected type
     * @throws TypeMetapathException
     *           if the provided sequence is incompatible with the expected result
     *           type
     */
    @Nullable
    public <T> T convert(@NonNull ISequence<?> sequence) {
      try {
        return ObjectUtils.asNullableType(converter.convert(sequence));
      } catch (ClassCastException ex) {
        throw new InvalidTypeMetapathException(null,
            String.format("Unable to cast to expected result type '%s' using expected type '%s'.",
                name(),
                expectedClass().getName()),
            ex);
      }
    }
  }

  /**
   * The Metapath expression identifying the current context node.
   */
  @NonNull
  public static final MetapathExpression CONTEXT_NODE
      = new MetapathExpression(".", ContextItem.instance(), StaticContext.instance());
  private static final Logger LOGGER = LogManager.getLogger(MetapathExpression.class);

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

        ParseTree tree = ObjectUtils.notNull(parser.metapath());

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
   * @see ResultType#convert(ISequence)
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
   * @see ResultType#convert(ISequence)
   */
  @Nullable
  public <T> T evaluateAs(
      @Nullable IItem focus,
      @NonNull ResultType resultType) {
    ISequence<?> result = evaluate(focus);
    return resultType.convert(result);
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
   * @see ResultType#convert(ISequence)
   */
  @Nullable
  public <T> T evaluateAs(
      @Nullable IItem focus,
      @NonNull ResultType resultType,
      @NonNull DynamicContext dynamicContext) {
    ISequence<?> result = evaluate(focus, dynamicContext);
    return resultType.convert(result);
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

  @FunctionalInterface
  interface ConversionFunction {
    @Nullable
    Object convert(@NonNull ISequence<?> sequence);
  }
}
