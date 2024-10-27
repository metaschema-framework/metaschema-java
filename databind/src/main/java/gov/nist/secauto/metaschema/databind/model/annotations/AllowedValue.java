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

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This annotation provides an enumerated value that is used as part of an
 * {@link AllowedValues} annotation.
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface AllowedValue {
  /**
   * The specific enumerated value.
   *
   * @return the value
   */
  @NonNull
  String value();

  /**
   * A description, encoded as a line of Markdown.
   *
   * @return an encoded markdown string
   */
  @NonNull
  String description();

  /**
   * The version this value was deprecated in.
   *
   * @return the version or an empty string if the value is not deprecated
   */
  @NonNull
  String deprecatedVersion() default "";
}
