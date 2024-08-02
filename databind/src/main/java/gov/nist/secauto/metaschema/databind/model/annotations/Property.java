/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Property {
  /**
   * The name of the property.
   *
   * @return the name
   */
  @NonNull
  String name();

  /**
   * The namespace of the property's name.
   *
   * @return the namespace
   */
  @NonNull
  String namespace();

  /**
   * The values for the property's name and namespace.
   *
   * @return the namespace
   */
  @NonNull
  String[] values();
}
