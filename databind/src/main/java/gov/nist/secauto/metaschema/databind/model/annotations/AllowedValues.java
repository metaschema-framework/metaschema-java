/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.model.constraint.IAllowedValuesConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This annotation defines a set of values permitted to be used in the context
 * of the containing annotation.
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface AllowedValues {
  /**
   * An optional identifier for the constraint, which must be unique to only this
   * constraint.
   *
   * @return the identifier if provided or an empty string otherwise
   */
  @SuppressWarnings("PMD.ShortMethodName")
  @NonNull
  String id() default "";

  /**
   * An optional formal name for the constraint.
   *
   * @return the formal name if provided or an empty string otherwise
   */
  @NonNull
  String formalName() default "";

  /**
   * An optional description of the constraint.
   *
   * @return the description if provided or an empty string otherwise
   */
  @NonNull
  String description() default "";

  /**
   * The significance of a violation of this constraint.
   *
   * @return the level
   */
  @NonNull
  Level level() default IConstraint.Level.ERROR;

  /**
   * An optional metapath that points to the target flag or field value that the
   * constraint applies to. If omitted the target will be ".", which means the
   * target is the value of the {@link BoundFlag}, {@link BoundField} or
   * {@link BoundFieldValue} annotation the constraint appears on. In the prior
   * case, this annotation may only appear on a {@link BoundField} if the field
   * has no flags, which results in a {@link BoundField} annotation on a field
   * instance with a scalar, data type value.
   *
   * @return the target metapath
   */
  @NonNull
  String target() default IConstraint.DEFAULT_TARGET_METAPATH;

  /**
   * An optional set of properties associated with these allowed values.
   *
   * @return the properties or an empty array with no properties
   */
  Property[] properties() default {};

  /**
   * Get any allowed values for this constraint.
   *
   * @return an array of allowed value enumerations
   */
  @NonNull
  AllowedValue[] values();

  /**
   * Indicates if the constraint allows other values not included in the
   * enumerated list.
   *
   * @return {@code true} if other values are allowed or {@code false} otherwise
   */
  boolean allowOthers() default IAllowedValuesConstraint.ALLOW_OTHER_DEFAULT;

  /**
   * Indicates if the constraint can be extended by other constraints.
   *
   * @return the extension mode
   */
  @NonNull
  IAllowedValuesConstraint.Extensible extensible() default IAllowedValuesConstraint.Extensible.EXTERNAL;

  /**
   * Any remarks about the constraint, encoded as an escaped Markdown string.
   *
   * @return an encoded markdown string or an empty string if no remarks are
   *         provided
   */
  @NonNull
  String remarks() default "";
}
