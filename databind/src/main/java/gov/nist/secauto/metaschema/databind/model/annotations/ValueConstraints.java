/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

@Documented
@Retention(RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ValueConstraints {
  /**
   * Get the let statements for the type of field this annotation is applied to.
   *
   * @return the let statements or an empty array if no let statements are defined
   */
  @NonNull
  Let[] lets() default {};

  /**
   * Get the allowed value constraints for the type or field this annotation is
   * applied to.
   *
   * @return the allowed values or an empty array if no allowed values constraints
   *         are defined
   */
  @NonNull
  AllowedValues[] allowedValues() default {};

  /**
   * Get the matches constraints for the type or field this annotation is applied
   * to.
   *
   * @return the allowed values or an empty array if no allowed values constraints
   *         are defined
   */
  @NonNull
  Matches[] matches() default {};

  /**
   * Get the index-has-key constraints for the type or field this annotation is
   * applied to.
   *
   * @return the allowed values or an empty array if no allowed values constraints
   *         are defined
   */
  @NonNull
  IndexHasKey[] indexHasKey() default {};

  /**
   * Get the expect constraints for the type or field this annotation is applied
   * to.
   *
   * @return the expected constraints or an empty array if no expected constraints
   *         are defined
   */
  @NonNull
  Expect[] expect() default {};
}
