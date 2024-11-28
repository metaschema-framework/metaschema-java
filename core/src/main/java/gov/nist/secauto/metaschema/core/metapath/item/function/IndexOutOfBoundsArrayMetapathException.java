
package gov.nist.secauto.metaschema.core.metapath.item.function;

import gov.nist.secauto.metaschema.core.metapath.item.function.impl.ArrayMetapathException;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * <a href=
 * "https://www.w3.org/TR/xpath-functions-31/#ERRFOAY0001">err:FOAY0001</a>:
 * This error is raised when an integer used to select a member of an array is
 * outside the range of values for that array.
 */
public class IndexOutOfBoundsArrayMetapathException
    extends ArrayMetapathException {

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and no cause.
   *
   * @param item
   *          the array item involved
   * @param message
   *          the exception message
   */
  public IndexOutOfBoundsArrayMetapathException(@NonNull IArrayItem<?> item, String message) {
    super(INDEX_OUT_OF_BOUNDS, item, message);
  }

  /**
   * Constructs a new exception with the provided {@code code}, {@code message},
   * and {@code cause}.
   *
   * @param item
   *          the array item involved
   * @param message
   *          the exception message
   * @param cause
   *          the original exception cause
   */
  public IndexOutOfBoundsArrayMetapathException(@NonNull IArrayItem<?> item, String message,
      Throwable cause) {
    super(INDEX_OUT_OF_BOUNDS, item, message, cause);
  }

  /**
   * Constructs a new exception with the provided {@code code}, no message, and
   * the {@code cause}.
   *
   * @param item
   *          the array item involved
   * @param cause
   *          the original exception cause
   */
  public IndexOutOfBoundsArrayMetapathException(@NonNull IArrayItem<?> item, Throwable cause) {
    super(INDEX_OUT_OF_BOUNDS, item, cause);
  }
}
