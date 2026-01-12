/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import dev.metaschema.core.model.IAttributable;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Defines a name-value property for a Metaschema definition or instance.
 */
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
  String namespace() default IAttributable.DEFAULT_PROPERY_NAMESPACE;

  /**
   * The values for the property's name and namespace.
   *
   * @return the namespace
   */
  @NonNull
  String[] values();
}
