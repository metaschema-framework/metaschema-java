/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.datatype.IDataTypeAdapter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Identifies that the annotation target is a bound property that represents a
 * Module flag.
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface BoundFlag {
  /**
   * Get the documentary formal name of the flag.
   * <p>
   * If the value is "##none", then the description will be considered
   * {@code null}.
   *
   * @return a markdown string or {@code "##none"} if no formal name is provided
   */
  @NonNull
  String formalName() default ModelUtil.NO_STRING_VALUE;

  /**
   * Get the documentary description of the flag.
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
   * associated XML attributes and JSON properties.
   * <p>
   * If the value is "##none", then element name is derived from the JavaBean
   * property name.
   *
   * @return the name
   */
  @NonNull
  String name() default ModelUtil.NO_STRING_VALUE;

  /**
   * The binary use name of the flag.
   * <p>
   * The value {@link Integer#MIN_VALUE} indicates that there is no use name.
   *
   * @return the index value
   */
  int useIndex() default Integer.MIN_VALUE;

  /**
   * The default value of the flag represented as a string.
   * <p>
   * The value {@link ModelUtil#NULL_VALUE} is used to indicate if no default
   * value is provided.
   *
   * @return the default value
   */
  @NonNull
  String defaultValue() default ModelUtil.NULL_VALUE;

  /**
   * Specifies if the XML Schema attribute is optional or required. If true, then
   * the JavaBean property is mapped to a XML Schema attribute that is required.
   * Otherwise it is mapped to a XML Schema attribute that is optional.
   *
   * @return {@code true} if the flag must occur, or {@code false} otherwise
   */
  boolean required() default false;

  /**
   * The Module data type adapter for the field's value.
   *
   * @return the data type adapter
   */
  @NonNull
  Class<? extends IDataTypeAdapter<?>> typeAdapter() default NullJavaTypeAdapter.class;

  /**
   * An optional set of associated properties.
   *
   * @return the properties or an empty array with no properties
   */
  Property[] properties() default {};

  /**
   * Get any remarks for this flag.
   *
   * @return a markdown string or {@code "##none"} if no remarks are provided
   */
  @NonNull
  String remarks() default ModelUtil.NO_STRING_VALUE;

  /**
   * Get the value constraints defined for this Metaschema flag inline definition.
   *
   * @return the value constraints
   */
  ValueConstraints valueConstraints() default @ValueConstraints;
}
