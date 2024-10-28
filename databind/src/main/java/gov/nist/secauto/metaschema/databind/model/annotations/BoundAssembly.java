/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.model.IGroupable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Identifies that the annotation target is a bound property that references a
 * Module assembly.
 * <p>
 * For XML serialization, the {@link #useName()} identifies the name of the
 * element to use for this element.
 * <p>
 * For JSON and YAML serializations, the {@link #useName()} identifies the
 * property/item name to use.
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
public @interface BoundAssembly {
  /**
   * Get the documentary formal name of the assembly.
   * <p>
   * If the value is "##none", then the description will be considered
   * {@code null}.
   *
   * @return a markdown string or {@code "##none"} if no formal name is provided
   */
  @NonNull
  String formalName() default ModelUtil.NO_STRING_VALUE;

  /**
   * Get the documentary description of the assembly.
   * <p>
   * If the value is "##none", then the description will be considered
   * {@code null}.
   *
   * @return a markdown string or {@code "##none"} if no description is provided
   */
  @NonNull
  String description() default ModelUtil.NO_STRING_VALUE;

  /**
   * The model name to use for singleton values. This name will be used for
   * associated XML elements.
   * <p>
   * If the value is "##none", then element name is derived from the JavaBean
   * property name.
   *
   * @return the name or {@code "##none"} if no use name is provided
   */
  @NonNull
  String useName() default ModelUtil.NO_STRING_VALUE;

  /**
   * The binary use name of the assembly.
   * <p>
   * The value {@link Integer#MIN_VALUE} indicates that there is no use name.
   *
   * @return the index value
   */
  int useIndex() default Integer.MIN_VALUE;

  /**
   * A non-negative number that indicates the minimum occurrence of the model
   * instance.
   *
   * @return a non-negative number
   */
  int minOccurs() default IGroupable.DEFAULT_GROUP_AS_MIN_OCCURS;

  /**
   * A number that indicates the maximum occurrence of the model instance.
   *
   * @return a positive number or {@code -1} to indicate "unbounded"
   */
  int maxOccurs() default IGroupable.DEFAULT_GROUP_AS_MAX_OCCURS;

  /**
   * An optional set of associated properties.
   *
   * @return the properties or an empty array with no properties
   */
  Property[] properties() default {};

  /**
   * Get any remarks for this field.
   *
   * @return a markdown string or {@code "##none"} if no remarks are provided
   */
  @NonNull
  String remarks() default ModelUtil.NO_STRING_VALUE;

  /**
   * Used to provide grouping information.
   * <p>
   * This annotation is required when the value of {@link #maxOccurs()} is greater
   * than 1.
   *
   * @return the configured {@link GroupAs} or the default value with a
   *         {@code null} {@link GroupAs#name()}
   */
  @NonNull
  GroupAs groupAs() default @GroupAs(name = ModelUtil.NULL_VALUE);
}
