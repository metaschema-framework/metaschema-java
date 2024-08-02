/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to identify the XML namespace to use for a set of annotated Java
 * classes.
 */
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface XmlNs {
  /**
   * Suggests a namespace prefix to use for generated code.
   * <p>
   * If the value is "##none", then there is no prefix defined.
   *
   * @return the associated namespace prefix
   */
  String prefix() default "##none";

  /**
   * Defines the Namespace URI for this namespace.
   *
   * @return the associated namespace
   */
  String namespace();
}
