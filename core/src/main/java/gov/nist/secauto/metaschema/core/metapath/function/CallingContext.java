
package gov.nist.secauto.metaschema.core.metapath.function;

import gov.nist.secauto.metaschema.core.metapath.ISequence;
import gov.nist.secauto.metaschema.core.metapath.item.IItem;

import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class CallingContext {
  @NonNull
  private final IFunction function;
  @Nullable
  private final IItem contextItem;
  @NonNull
  private final List<ISequence<?>> arguments;

  /**
   * Set up the execution context for this function.
   *
   * @param function
   *          the function
   * @param arguments
   *          the function arguments
   * @param contextItem
   *          the current node context
   */
  public CallingContext(
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
    return prime * result + Objects.hash(contextItem, arguments);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true; // NOPMD - readability
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false; // NOPMD - readability
    }
    CallingContext other = (CallingContext) obj;
    if (!getFunction().equals(other.getFunction())) {
      return false; // NOPMD - readability
    }
    return Objects.equals(arguments, other.arguments) && Objects.equals(contextItem, other.contextItem);
  }
}
