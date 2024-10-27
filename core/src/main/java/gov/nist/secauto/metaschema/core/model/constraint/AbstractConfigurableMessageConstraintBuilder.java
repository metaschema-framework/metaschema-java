/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * Provides builder methods for the core data elements of an
 * {@link IConstraint}.
 * <p>
 * The base class of all constraint builders.
 *
 * @param <T>
 *          the Java type of the implementing builder
 * @param <R>
 *          the Java type of the resulting built object
 * @since 2.0.0
 */
public abstract class AbstractConfigurableMessageConstraintBuilder<
    T extends AbstractConfigurableMessageConstraintBuilder<T, R>,
    R extends IConfigurableMessageConstraint>
    extends AbstractConstraintBuilder<T, R> {
  private String message;

  /**
   * A message to emit when the constraint is violated. Allows embedded Metapath
   * expressions using the syntax {@code \{ metapath \}}.
   *
   * @param message
   *          the message if defined or {@code null} otherwise
   * @return this builder
   */
  @NonNull
  public T message(@NonNull String message) {
    this.message = message;
    return getThis();
  }

  /**
   * Get the constraint message provided to the builder.
   *
   * @return the message or {@code null} if no message is set
   */
  @Nullable
  protected String getMessage() {
    return message;
  }
}
