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
public @interface Let {
  /**
   * The variable name.
   *
   * @return the variable name
   */
  @NonNull
  String name();

  /**
   * A Metapath to use the query the values assigned to the variable.
   *
   * @return the value Metapath
   */
  @NonNull
  String target();

  /**
   * Any remarks about the let statement, encoded as an escaped Markdown string.
   *
   * @return an encoded Markdown string or an empty string if no remarks are
   *         provided
   */
  @NonNull
  String remarks() default "";
}
