/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.model.IFieldInstance;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This annotation indicates that the target class represents a Module field.
 * <p>
 * Classes with this annotation must have a field with the
 * {@link BoundFieldValue} annotation.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface MetaschemaField {
  /**
   * Get the documentary formal name of the field.
   * <p>
   * If the value is "##none", then the description will be considered
   * {@code null}.
   *
   * @return a markdown string or {@code "##none"} if no formal name is provided
   */
  @NonNull
  String formalName() default ModelUtil.NO_STRING_VALUE;

  /**
   * Get the documentary description of the field.
   * <p>
   * If the value is "##none", then the description will be considered
   * {@code null}.
   *
   * @return a markdown string or {@code "##none"} if no description is provided
   */
  @NonNull
  String description() default ModelUtil.NO_STRING_VALUE;

  /**
   * Name of the field.
   *
   * @return the name
   */
  @NonNull
  String name();

  /**
   * The binary name of the assembly.
   * <p>
   * The value {@link Integer#MIN_VALUE} indicates that there is no index.
   *
   * @return the index value
   */
  int index() default Integer.MIN_VALUE;

  /**
   * Get the name to use for data instances of this field.
   * <p>
   * This overrides the name provided by {@link #name()}.
   * <p>
   * The value {@link ModelUtil#NO_STRING_VALUE} indicates that there is no use
   * name.
   *
   *
   * @return the use name or {@link ModelUtil#NO_STRING_VALUE} if no use name is
   *         provided
   */
  @NonNull
  String useName() default ModelUtil.NO_STRING_VALUE;

  /**
   * The binary use name of the assembly.
   * <p>
   * The value {@link Integer#MIN_VALUE} indicates that there is no index.
   *
   * @return the index value or {@link Integer#MIN_VALUE} if there is no index
   *         value
   */
  int useIndex() default Integer.MIN_VALUE;

  /**
   * Get the metaschema class that "owns" this assembly, which is the concrete
   * implementation of the metaschema containing the assembly.
   *
   * @return the class that extends {@link IBoundModule}
   */
  @NonNull
  Class<? extends IBoundModule> moduleClass();

  /**
   * If the data type allows it, determines if the field's value must be wrapped
   * with an XML element whose name is the specified {@link #name()} and namespace
   * is derived from the namespace of the instance.
   *
   * @return {@code true} if the field must be wrapped, or {@code false} otherwise
   */
  boolean inXmlWrapped() default IFieldInstance.DEFAULT_FIELD_IN_XML_WRAPPED;

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
   * Get the value constraints defined for this Metaschema field definition.
   *
   * @return the value constraints
   */
  ValueConstraints valueConstraints() default @ValueConstraints;
}
