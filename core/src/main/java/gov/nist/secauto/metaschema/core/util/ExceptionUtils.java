/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides a means for throwing important checked exceptions over non-checked
 * methods, e.g. lambda invocations.
 * <p>
 * This capability should be used with care, and generally in limited
 * circumstances.
 */
public final class ExceptionUtils {
  /**
   * Wrap a checked exception in an unchecked {@link WrappedException}.
   *
   * @param ex
   *          the exception to wrap
   * @return a new wrapped exception containing the provided exception
   */
  @NonNull
  public static WrappedException wrap(@NonNull Throwable ex) {
    return new WrappedException(Objects.requireNonNull(ex, "ex"));
  }

  /**
   * Wrap a checked exception in an unchecked {@link WrappedException}.
   * <p>
   * This method is identical to {@link #wrap(Throwable)} but named to indicate
   * intent when used in throw statements.
   *
   * @param ex
   *          the exception to wrap
   * @return a new wrapped exception containing the provided exception
   */
  @NonNull
  public static WrappedException wrapAndThrow(@NonNull Throwable ex) {
    return wrap(ex);
  }

  /**
   * Unwrap a previously wrapped exception.
   *
   * @param ex
   *          the wrapped exception to unwrap
   * @return the original exception that was wrapped
   */
  @NonNull
  public static Throwable unwrap(
      @NonNull WrappedException ex) {
    return ex.unwrap();
  }

  /**
   * Unwrap a previously wrapped exception, casting it to the expected type.
   *
   * @param <E>
   *          the expected exception type
   * @param ex
   *          the wrapped exception to unwrap
   * @param wrappedExceptionClass
   *          the class of the expected exception type
   * @return the original exception cast to the expected type
   * @throws IllegalArgumentException
   *           if the wrapped exception is not of the expected type
   */
  @NonNull
  public static <E extends Throwable> E unwrap(
      @NonNull WrappedException ex,
      @NonNull Class<E> wrappedExceptionClass) {
    return ex.unwrap(wrappedExceptionClass);
  }

  /**
   * A runtime exception that wraps a checked exception, allowing it to be thrown
   * from contexts that do not allow checked exceptions (such as lambda
   * expressions).
   */
  public static final class WrappedException
      extends RuntimeException {

    /**
     * the serial version UID.
     */
    private static final long serialVersionUID = 2L;

    /**
     * Construct a new wrapped exception.
     *
     * @param cause
     *          the exception to wrap
     */
    public WrappedException(@NonNull Throwable cause) {
      super(cause);
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
      throw new UnsupportedOperationException("must set cause in constructor");
    }

    /**
     * Get the wrapped exception.
     *
     * @return the original exception that was wrapped
     */
    @NonNull
    public Throwable unwrap() {
      return ObjectUtils.notNull(getCause());
    }

    /**
     * Get the wrapped exception, casting it to the expected type.
     *
     * @param <E>
     *          the expected exception type
     * @param wrappedExceptionClass
     *          the class of the expected exception type
     * @return the original exception cast to the expected type
     * @throws IllegalArgumentException
     *           if the wrapped exception is not of the expected type
     */
    @NonNull
    public <E extends Throwable> E unwrap(@NonNull Class<E> wrappedExceptionClass) {
      Throwable cause = unwrap();
      if (wrappedExceptionClass.isInstance(cause)) {
        E unwrappedEx = wrappedExceptionClass.cast(cause);
        unwrappedEx.addSuppressed(this);
        return unwrappedEx;
      }
      throw new IllegalArgumentException(
          String.format("Wrapped exception '%s' did not match expected type '%s'.",
              cause.getClass().getName(),
              wrappedExceptionClass.getName()));
    }

    /**
     * Unwrap and throw the original exception.
     *
     * @throws Throwable
     *           the original wrapped exception
     */
    public void unwrapAndThrow() throws Throwable {
      throw unwrap();
    }

    /**
     * Unwrap and throw the original exception, cast to the expected type.
     *
     * @param <E>
     *          the expected exception type
     * @param wrappedExceptionClass
     *          the class of the expected exception type
     * @throws E
     *           the original wrapped exception cast to the expected type
     */
    public <E extends Throwable> void unwrapAndThrow(@NonNull Class<E> wrappedExceptionClass) throws E {
      throw unwrap(wrappedExceptionClass);
    }
  }

  private ExceptionUtils() {
    // prevent construction
  }
}
