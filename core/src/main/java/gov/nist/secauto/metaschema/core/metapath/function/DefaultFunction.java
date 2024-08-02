/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.DynamicContext;
import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.InvalidTypeMetapathException;
import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.metapath.function.library.FnData;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.TypeSystem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyAtomicItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IAnyUriItem;
import gov.nist.secauto.metaschema.core.metapath.item.atomic.IStringItem;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides a concrete implementation of a function call executor.
 */
public class DefaultFunction
    extends AbstractFunction {
  // private static final Logger logger =
  // LogManager.getLogger(AbstractFunction.class);

  @NonNull
  private final Set<FunctionProperty> properties;
  @NonNull
  private final ISequenceType result;
  @NonNull
  private final IFunctionExecutor handler;

  /**
   * Construct a new function signature.
   *
   * @param name
   *          the name of the function
   * @param properties
   *          the characteristics of the function
   * @param arguments
   *          the argument signatures or an empty list
   * @param result
   *          the type of the result
   * @param handler
   *          the handler to call to execute the function
   */
  @SuppressWarnings({ "null", "PMD.LooseCoupling" })
  DefaultFunction(
      @NonNull String name,
      @NonNull String namespace,
      @NonNull EnumSet<FunctionProperty> properties,
      @NonNull List<IArgument> arguments,
      @NonNull ISequenceType result,
      @NonNull IFunctionExecutor handler) {
    super(name, namespace, arguments);
    this.properties = Collections.unmodifiableSet(properties);
    this.result = result;
    this.handler = handler;
  }

  @Override
  public Set<FunctionProperty> getProperties() {
    return properties;
  }

  @Override
  public ISequenceType getResult() {
    return result;
  }
  //
  // @Override
  // public boolean isSupported(List<IExpression<?>> expressionArguments) {
  // boolean retval;
  // if (expressionArguments.isEmpty() && getArguments().isEmpty()) {
  // // no arguments
  // retval = true;
  // // } else if (arity() == 1 && expressionArguments.isEmpty()) {
  // // // the context item will be the argument
  // // // TODO: check the context item for type compatibility
  // // retval = true;
  // } else if ((expressionArguments.size() == getArguments().size())
  // || (isArityUnbounded() && expressionArguments.size() >
  // getArguments().size())) {
  // retval = true;
  // // check that argument requirements are satisfied
  // Iterator<IArgument> argumentIterator = getArguments().iterator();
  // Iterator<IExpression<?>> expressionIterator = expressionArguments.iterator();
  //
  // IArgument argument = null;
  // while (argumentIterator.hasNext()) {
  // argument = argumentIterator.next();
  // IExpression<?> expression = expressionIterator.hasNext() ?
  // expressionIterator.next() : null;
  //
  // if (expression != null) {
  // // is the expression supported by the argument?
  // retval = argument.isSupported(expression);
  // if (!retval) {
  // break;
  // }
  // } else {
  // // there are no more expression arguments. Make sure that the remaining
  // arguments are optional
  // if (!argument.getSequenceType().getOccurrence().isOptional()) {
  // retval = false;
  // break;
  // }
  // }
  // }
  //
  // if (retval && expressionIterator.hasNext()) {
  // if (isArityUnbounded()) {
  // // check remaining expressions against the last argument
  // while (expressionIterator.hasNext()) {
  // IExpression<?> expression = expressionIterator.next();
  // @SuppressWarnings("null")
  // boolean result = argument.isSupported(expression);
  // if (!result) {
  // retval = result;
  // break;
  // }
  // }
  // } else {
  // // there are extra expressions, which do not match the arguments
  // retval = false;
  // }
  // }
  // } else {
  // retval = false;
  // }
  // return retval;
  // }

  /**
   * Converts arguments in an attempt to align with the function's signature.
   *
   * @param function
   *          the function
   * @param parameters
   *          the argument parameters
   * @return the converted argument list
   */
  @NonNull
  public static List<ISequence<?>> convertArguments(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> parameters) {
    @NonNull List<ISequence<?>> retval = new ArrayList<>(parameters.size());

    Iterator<IArgument> argumentIterator = function.getArguments().iterator();
    Iterator<ISequence<?>> parametersIterator = parameters.iterator();

    IArgument argument = null;
    while (parametersIterator.hasNext()) {
      if (argumentIterator.hasNext()) {
        argument = argumentIterator.next();
      } else if (!function.isArityUnbounded()) {
        throw new InvalidTypeMetapathException(
            null,
            String.format("argument signature doesn't match '%s'", function.toSignature()));
      }

      ISequence<?> parameter = parametersIterator.next();
      assert argument != null;
      assert parameter != null;

      retval.add(convertArgument(argument, parameter));
    }
    return retval;
  }

  @NonNull
  private static ISequence<?> convertArgument(
      @NonNull IArgument argument,
      @NonNull ISequence<?> parameter) {
    // apply occurrence
    ISequence<?> retval = argument.getSequenceType().getOccurrence().getSequenceHandler().handle(parameter);

    // apply function conversion and type promotion to the parameter
    if (!retval.isEmpty()) {
      retval = convertSequence(argument, retval);

      // verify resulting values
      Class<? extends IItem> argumentClass = argument.getSequenceType().getType();
      for (IItem item : retval.getValue()) {
        Class<? extends IItem> itemClass = item.getClass();
        if (!argumentClass.isAssignableFrom(itemClass)) {
          throw new InvalidTypeMetapathException(
              item,
              String.format("The type '%s' is not a subtype of '%s'",
                  TypeSystem.getName(itemClass),
                  TypeSystem.getName(argumentClass)));
        }
      }
    }
    return retval;
  }

  /**
   * Based on XPath 3.1
   * <a href="https://www.w3.org/TR/xpath-31/#dt-function-conversion">function
   * conversion</a> rules.
   *
   * @param argument
   *          the function argument signature details
   * @param sequence
   *          the sequence to convert
   * @return the converted sequence
   */
  @NonNull
  protected static ISequence<?> convertSequence(@NonNull IArgument argument, @NonNull ISequence<?> sequence) {
    ISequenceType requiredSequenceType = argument.getSequenceType();
    Class<? extends IItem> requiredSequenceTypeClass = requiredSequenceType.getType();

    Stream<? extends IItem> stream = sequence.safeStream();

    if (IAnyAtomicItem.class.isAssignableFrom(requiredSequenceTypeClass)) {
      Stream<? extends IAnyAtomicItem> atomicStream = stream.flatMap(FnData::atomize);

      // if (IUntypedAtomicItem.class.isInstance(item)) { // NOPMD
      // // TODO: apply cast to atomic type
      // }

      if (IStringItem.class.equals(requiredSequenceTypeClass)) {
        // promote URIs to strings if a string is required
        atomicStream = atomicStream.map(item -> IAnyUriItem.class.isInstance(item) ? IStringItem.cast(item) : item);
      }

      stream = atomicStream;
    }

    stream = stream.peek(item -> {
      if (!requiredSequenceTypeClass.isInstance(item)) {
        throw new InvalidTypeMetapathException(
            item,
            String.format("The type '%s' is not a subtype of '%s'",
                item.getClass().getName(),
                requiredSequenceTypeClass.getName()));
      }
    });
    assert stream != null;

    return ISequence.of(stream);
  }

  @Override
  public ISequence<?> execute(
      @NonNull List<ISequence<?>> arguments,
      @NonNull DynamicContext dynamicContext,
      @NonNull ISequence<?> focus) {
    try {
      List<ISequence<?>> convertedArguments = convertArguments(this, arguments);

      IItem contextItem = isFocusDepenent() ? ObjectUtils.requireNonNull(focus.getFirstItem(true)) : null;

      CallingContext callingContext = null;
      ISequence<?> result = null;
      if (isDeterministic()) {
        // check cache
        callingContext = new CallingContext(arguments, contextItem);
        // TODO: implement something like computeIfAbsent
        // attempt to get the result from the cache
        result = dynamicContext.getCachedResult(callingContext);
      }

      if (result == null) {
        result = handler.execute(this, convertedArguments, dynamicContext, contextItem);

        if (callingContext != null) {
          // add result to cache
          dynamicContext.cacheResult(callingContext, result);
        }
      }

      // logger.info(String.format("Executed function '%s' with arguments '%s'
      // producing result '%s'",
      // toSignature(), convertedArguments.toString(), result.asList().toString()));
      return result;
    } catch (MetapathException ex) {
      throw new MetapathException(String.format("Unable to execute function '%s'", toSignature()), ex);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(getQName(), getArguments(), handler, properties, result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true; // NOPMD - readability
    }
    if (obj == null) {
      return false; // NOPMD - readability
    }
    if (getClass() != obj.getClass()) {
      return false; // NOPMD - readability
    }
    DefaultFunction other = (DefaultFunction) obj;
    return Objects.equals(getQName(), other.getQName())
        && Objects.equals(getArguments(), other.getArguments())
        && Objects.equals(handler, other.handler)
        && Objects.equals(properties, other.properties)
        && Objects.equals(result, other.result);
  }

  @Override
  public String toString() {
    return toSignature();
  }

  public final class CallingContext {
    @Nullable
    private final IItem contextItem;
    @NonNull
    private final List<ISequence<?>> arguments;

    /**
     * Set up the execution context for this function.
     *
     * @param arguments
     *          the function arguments
     * @param contextItem
     *          the current node context
     */
    private CallingContext(@NonNull List<ISequence<?>> arguments, @Nullable IItem contextItem) {
      this.contextItem = contextItem;
      this.arguments = arguments;
    }

    /**
     * Get the function instance associated with the calling context.
     *
     * @return the function instance
     */
    @NonNull
    public DefaultFunction getFunction() {
      return DefaultFunction.this;
    }

    /**
     * Get the node item focus associated with the calling context.
     *
     * @return the function instance
     */
    @Nullable
    public IItem getContextItem() {
      return contextItem;
    }

    /**
     * Get the arguments associated with the calling context.
     *
     * @return the arguments
     */
    @NonNull
    public List<ISequence<?>> getArguments() {
      return arguments;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getFunction().hashCode();
      result = prime * result + Objects.hash(contextItem, arguments);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true; // NOPMD - readability
      }
      if (obj == null) {
        return false; // NOPMD - readability
      }
      if (getClass() != obj.getClass()) {
        return false; // NOPMD - readability
      }
      CallingContext other = (CallingContext) obj;
      if (!getFunction().equals(other.getFunction())) {
        return false; // NOPMD - readability
      }
      return Objects.equals(arguments, other.arguments) && Objects.equals(contextItem, other.contextItem);
    }
  }
}
