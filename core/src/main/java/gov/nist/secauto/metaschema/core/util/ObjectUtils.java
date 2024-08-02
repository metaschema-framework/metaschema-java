/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.util;

import java.util.Objects;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class ObjectUtils {
  private ObjectUtils() {
    // disable construction
  }

  /**
   * Assert that the provided object is not {@code null}.
   * <p>
   * This method sets the expectation that the provided object is not {@code null}
   * in cases where a non-null value is required.
   *
   * @param <T>
   *          the object type
   * @param obj
   *          the object
   * @return the object
   */
  @NonNull
  public static <T> T notNull(@Nullable T obj) {
    assert obj != null;
    return obj;
  }

  /**
   * Require a non-null value.
   *
   * @param <T>
   *          the type of the reference
   * @param obj
   *          the object reference to check for nullity
   * @return {@code obj} if not {@code null}
   * @throws NullPointerException
   *           if {@code obj} is {@code null}
   */
  @NonNull
  @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
  public static <T> T requireNonNull(@Nullable T obj) {
    if (obj == null) {
      throw new NullPointerException(); // NOPMD
    }
    return obj;
  }

  /**
   * Require a non-null value.
   *
   * @param <T>
   *          the type of the reference
   * @param obj
   *          the object reference to check for nullity
   * @param message
   *          detail message to be used in the event that a {@code
   *                NullPointerException} is thrown
   * @return {@code obj} if not {@code null}
   * @throws NullPointerException
   *           if {@code obj} is {@code null}
   */
  @NonNull
  @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
  public static <T> T requireNonNull(@Nullable T obj, @NonNull String message) {
    if (obj == null) {
      throw new NullPointerException(message); // NOPMD
    }
    return obj;
  }

  /**
   * A filter used to remove null items from a stream.
   *
   * @param <T>
   *          the item type
   * @param item
   *          the item to filter
   * @return the item as a steam or an empty stream if the item is {@code null}
   */
  @SuppressWarnings("null")
  @NonNull
  public static <T> Stream<T> filterNull(T item) {
    return Objects.nonNull(item) ? Stream.of(item) : Stream.empty();
  }

  /**
   * Cast the provided object as the requested return type.
   *
   * @param <T>
   *          the Java type to cast the object to
   * @param obj
   *          the object to cast
   * @return the object cast to the requested type
   * @throws ClassCastException
   *           if the object cannot be cast to the requested type
   */
  @SuppressWarnings("unchecked")
  @NonNull
  public static <T> T asType(@NonNull Object obj) {
    return (T) obj;
  }

  /**
   * Cast the provided object as the requested return type.
   * <p>
   * If the object is {@code null}, the returned value will be {@code null}.
   *
   * @param <T>
   *          the Java type to cast the object to
   * @param obj
   *          the object to cast, which may be {@code null}
   * @return the object cast to the requested type, or {@code null} if the
   *         provided object is {@code null}
   * @throws ClassCastException
   *           if the object cannot be cast to the requested type
   */
  @SuppressWarnings("unchecked")
  @Nullable
  public static <T> T asNullableType(@Nullable Object obj) {
    return (T) obj;
  }
}
