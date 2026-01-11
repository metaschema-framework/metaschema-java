/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import dev.metaschema.core.model.constraint.IConstraint;
import dev.metaschema.core.model.constraint.IConstraint.Level;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This annotation defines a report condition in the context of the containing
 * annotation.
 * <p>
 * Report constraints generate findings when their test expression evaluates to
 * {@code true}, which is the opposite of expect constraints.
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Report {
  /**
   * An optional identifier for the constraint, which must be unique to only this
   * constraint.
   *
   * @return the identifier if provided or an empty string otherwise
   */
  @NonNull
  String id() default "";

  /**
   * An optional formal name for the constraint.
   *
   * @return the formal name if provided or an empty string otherwise
   */
  @NonNull
  String formalName() default "";

  /**
   * An optional description of the constraint.
   *
   * @return the description if provided or an empty string otherwise
   */
  @NonNull
  String description() default "";

  /**
   * The significance of a violation of this constraint.
   * <p>
   * The default level for report constraints is {@link Level#INFORMATIONAL},
   * which differs from expect constraints that default to {@link Level#ERROR}.
   *
   * @return the level
   */
  @NonNull
  Level level() default IConstraint.Level.INFORMATIONAL;

  /**
   * An optional metapath that points to the target flag or field value that the
   * constraint applies to. If omitted the target will be ".", which means the
   * target is the value of the {@link BoundFlag}, {@link BoundField} or
   * {@link BoundFieldValue} annotation the constraint appears on. In the prior
   * case, this annotation may only appear on a {@link BoundField} if the field
   * has no flags, which results in a {@link BoundField} annotation on a field
   * instance with a scalar, data type value.
   *
   * @return the target metapath
   */
  @NonNull
  String target() default ".";

  /**
   * An optional set of properties associated with this constraint.
   *
   * @return the properties or an empty array with no properties
   */
  Property[] properties() default {};

  /**
   * A metapath that is expected to evaluate to {@code true} when a finding should
   * be reported.
   * <p>
   * This is the opposite of expect constraints - report constraints fire when the
   * test is true.
   *
   * @return a metapath expression
   */
  @NonNull
  String test();

  /**
   * The message to emit when the constraint is violated.
   *
   * @return the message or an empty string otherwise
   */
  @NonNull
  String message() default "";

  /**
   * Any remarks about the constraint, encoded as an escaped Markdown string.
   *
   * @return an encoded markdown string or an empty string if no remarks are
   *         provided
   */
  @NonNull
  String remarks() default "";
}
