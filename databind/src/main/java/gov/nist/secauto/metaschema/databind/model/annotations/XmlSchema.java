/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation provides package-level Module information.
 */
@Retention(RUNTIME)
@Target(PACKAGE)
public @interface XmlSchema {
  /**
   * The default value of a schema location, which indicates that no schema will
   * be associated.
   * <p>
   * The value "##none" was chosen because ## is not a valid sequence in
   * xs:anyURI.
   */
  String NO_LOCATION = ModelUtil.NO_STRING_VALUE;

  /**
   * Defines the XML namespace URI and prefix to use for this model. If a prefix
   * is not provided, the XML prefix will be auto-generated.
   *
   * @return an array of namespace definitions
   */
  XmlNs[] xmlns() default {};

  /**
   * Name of the XML namespace.
   * <p>
   * If the value is "##none", then there is no prefix defined.
   *
   * @return a namespace string in the form of a URI
   */
  String namespace() default ModelUtil.NO_STRING_VALUE;

  /**
   * The location of the associated XML schema.
   *
   * @return a location string in the form of a URI
   */
  String xmlSchemaLocation() default NO_LOCATION;

  /**
   * The location of the associated JSON schema.
   *
   * @return a location string in the form of a URI
   */
  String jsonSchemaLocation() default NO_LOCATION;

  /**
   * Get the default XML element form.
   *
   * @return the XML element form
   */
  XmlNsForm xmlElementFormDefault() default XmlNsForm.UNSET;

  /**
   * Get the default XML attribute form.
   *
   * @return the XML attribute form
   */
  XmlNsForm xmlAttributeFormDefault() default XmlNsForm.UNSET;
}
