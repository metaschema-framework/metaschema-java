/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Identifies a Metapath expression referencing a value that is used in
 * generating a key as part of a {@link IsUnique}, {@link Index}, or
 * {@link IndexHasKey} constraint annotation.
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface KeyField {
  /**
   * An optional metapath that points to the target flag or field value that the
   * key applies to. If omitted the target will be ".", which means the target is
   * the value of the {@link BoundFlag}, {@link BoundField} or
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
   * Retrieve an optional pattern to use to retrieve the value. If
   * non-{@code null}, the first capturing group is used to retrieve the value.
   * This must be a pattern that can compile using
   * {@link Pattern#compile(String)}.
   *
   * @return a pattern string or an empty string if no pattern is provided
   */
  @NonNull
  String pattern() default "";

  /**
   * Any remarks about the key field, encoded as an escaped Markdown string.
   *
   * @return an encoded markdown string or an empty string if no remarks are
   *         provided
   */
  @NonNull
  String remarks() default "";
}
