/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint;
import gov.nist.secauto.metaschema.core.model.constraint.IConstraint.Level;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This annotation defines a rule that requires matching patterns and/or data
 * types among the target contents of the assembly represented by the containing
 * {@link MetaschemaAssembly} annotation.
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Matches {
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
   * An optional Metapath that points to the target flag or field value that the
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
  String target() default ".";

  /**
   * An optional set of properties associated with these allowed values.
   *
   * @return the properties or an empty array with no properties
   */
  Property[] properties() default {};

  /**
   * Retrieve an optional pattern that the associated value must match. This must
   * be a pattern that can compile using {@link Pattern#compile(String)}.
   *
   * @return a pattern string or an empty string if no pattern is provided
   */
  @NonNull
  String pattern() default "";

  /**
   * The Module data type adapter for the data type that the associated value must
   * conform to.
   *
   * @return the data type adapter or a {@link NullJavaTypeAdapter} if none is
   *         provided
   */
  @NonNull
  Class<? extends IDataTypeAdapter<?>> typeAdapter() default NullJavaTypeAdapter.class;

  /**
   * The message to emit when the constraint is violated.
   *
   * @return the message or an empty string otherwise
   */
  @NonNull
  String message() default "";

  /**
   * Any remarks about the constraint, encoded as an escaped Markdown string.
   *
   * @return an encoded markdown string or an empty string if no remarks are
   *         provided
   */
  @NonNull
  String remarks() default "";
}
