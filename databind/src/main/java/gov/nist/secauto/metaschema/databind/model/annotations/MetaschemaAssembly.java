/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This annotation indicates that the target class represents a Module assembly.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface MetaschemaAssembly {
  /**
   * Get the documentary formal name of the assembly.
   * <p>
   * If the value is "##none", then the description will be considered
   * {@code null}.
   *
   * @return a Markdown string or {@code "##none"} if no formal name is provided
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
   * Get the Metaschema module class that "owns" this assembly, which is the
   * concrete implementation of the module containing the assembly.
   *
   * @return the {@link IBoundModule} class
   */
  @NonNull
  Class<? extends IBoundModule> moduleClass();

  /**
   * Name of the assembly.
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
   * Name of the root XML element or the JSON/YAML property.
   * <p>
   * If the value is "##none", then there is no root name.
   *
   * @return the name
   */
  @NonNull
  String rootName() default ModelUtil.NO_STRING_VALUE;

  /**
   * The binary root name of the assembly.
   * <p>
   * The value {@link Integer#MIN_VALUE} indicates that there is no root index.
   *
   * @return the index value
   */
  int rootIndex() default Integer.MIN_VALUE;

  /**
   * An optional set of associated properties.
   *
   * @return the properties or an empty array with no properties
   */
  Property[] properties() default {};

  /**
   * Get any remarks for this assembly.
   *
   * @return a markdown string or {@code "##none"} if no remarks are provided
   */
  @NonNull
  String remarks() default ModelUtil.NO_STRING_VALUE;

  /**
   * Get the value constraints defined for this Metaschema assembly definition.
   *
   * @return the value constraints
   */
  ValueConstraints valueConstraints() default @ValueConstraints;

  /**
   * Get the model constraints defined for this Metaschema assembly definition.
   *
   * @return the value constraints
   */
  AssemblyConstraints modelConstraints() default @AssemblyConstraints;
}
