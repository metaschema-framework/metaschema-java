/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.model.IChoiceGroupInstance;
import gov.nist.secauto.metaschema.core.model.IGroupable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Identifies that the annotation target is a bound property that references a
 * collection of model instances of varying types.
 * <p>
 * For JSON and YAML serializations, the {@link #discriminator()} identifies the
 * property use to differentiate the type of object values.
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface BoundChoiceGroup {
  /**
   * The discriminator to use for determining the type of child elements in JSON.
   *
   * @return the discriminator property name
   */
  @NonNull
  String discriminator() default IChoiceGroupInstance.DEFAULT_JSON_DISCRIMINATOR_PROPERTY_NAME;

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

  /**
   * The name of a common flag to use as the JSON key that appears on all
   * associated {@link #assemblies()} and {@link #fields()}.
   *
   * @return the configured JSON key flag name or
   *         {@link ModelUtil#NO_STRING_VALUE} if no JSON key is configured
   */
  @NonNull
  String jsonKey() default ModelUtil.NO_STRING_VALUE;

  /**
   * The the assemblies that may occur within this choice group.
   *
   * @return an array of assembly bindings which may occur within this choice
   *         group
   */
  @NonNull
  BoundGroupedAssembly[] assemblies() default {};

  /**
   * The the fields that may occur within this choice group.
   *
   * @return an array of field bindings which may occur within this choice group
   */
  @NonNull
  BoundGroupedField[] fields() default {};
}
