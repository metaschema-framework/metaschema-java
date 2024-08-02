/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.model.JsonGroupAsBehavior;
import gov.nist.secauto.metaschema.core.model.XmlGroupAsBehavior;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

@Documented
@Retention(RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface GroupAs {
  /**
   * The name to use for an XML grouping element wrapper or a JSON/YAML grouping
   * property.
   *
   * @return the name
   */
  @NonNull
  String name();

  /**
   * Describes how to handle collections in JSON/YAML.
   *
   * @return the JSON collection strategy
   */
  @NonNull
  JsonGroupAsBehavior inJson() default JsonGroupAsBehavior.SINGLETON_OR_LIST;

  /**
   * Describes how to handle collections in XML.
   *
   * @return the XML collection strategy
   */
  @NonNull
  XmlGroupAsBehavior inXml() default XmlGroupAsBehavior.UNGROUPED;
}
