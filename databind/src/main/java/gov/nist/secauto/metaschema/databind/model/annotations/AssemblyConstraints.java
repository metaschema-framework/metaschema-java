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
public @interface AssemblyConstraints {
  /**
   * Get the index constraints for this assembly.
   *
   * @return the index constraints or an empty array if no index constraints are
   *         defined
   */
  @NonNull
  Index[] index() default {};

  /**
   * Get the unique constraints for this assembly.
   *
   * @return the unique constraints or an empty array if no unique constraints are
   *         defined
   */
  @NonNull
  IsUnique[] unique() default {};

  /**
   * Get the cardinality constraints for this assembly.
   *
   * @return the cardinality constraints or an empty array if no cardinality
   *         constraints are defined
   */
  @NonNull
  HasCardinality[] cardinality() default {};
}
