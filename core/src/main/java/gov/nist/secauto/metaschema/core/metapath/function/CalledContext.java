/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.item.IItem;
import gov.nist.secauto.metaschema.core.metapath.item.ISequence;

import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Represents an immutable execution context for function calls in Metapath
 * expressions.
 * <p>
 * This class is designed to support both named and anonymous functions by
 * maintaining the function instance, its arguments, and the current context
 * item. It ensures thread-safety through immutability and is primarily used
 * during the evaluation of Metapath expressions and for caching the function
 * results.
 */
public final class CalledContext {
  @NonNull
  private final IFunction function;
  @Nullable
  private final IItem contextItem;
  @NonNull
  private final List<ISequence<?>> arguments;

  /**
   * Creates an immutable execution context for a function call.
   *
   * @param function
   *          the function to be executed
   * @param arguments
   *          the list of evaluated arguments as sequences, must match function's
   *          arity
   * @param contextItem
   *          the optional context item representing the current node in scope
   */
  public CalledContext(
      @NonNull IFunction function,
      @NonNull List<ISequence<?>> arguments,
      @Nullable IItem contextItem) {
    this.function = function;
    this.contextItem = contextItem;
    this.arguments = arguments;
  }

  /**
   * Get the function instance associated with the calling context.
   *
   * @return the function instance
   */
  @NonNull
  public IFunction getFunction() {
    return function;
  }

  /**
   * Get the node item focus associated with the calling context.
   *
   * @return the context item, or null if no context is set
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
    return prime * result + Objects.hash(contextItem, arguments);
  }

  @SuppressWarnings("PMD.OnlyOneReturn")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    CalledContext other = (CalledContext) obj;
    if (!getFunction().equals(other.getFunction())) {
      return false;
    }
    return Objects.equals(function, other.function)
        && Objects.equals(arguments, other.arguments)
        && Objects.equals(contextItem, other.contextItem);
  }
}
