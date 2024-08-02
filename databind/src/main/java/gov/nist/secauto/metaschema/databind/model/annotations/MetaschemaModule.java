/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import edu.umd.cs.findbugs.annotations.NonNull;

@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface MetaschemaModule {
  /**
   * Get the classes representing the global fields defined on this Module.
   *
   * @return an array of field classes
   */
  @NonNull
  Class<? extends IBoundObject>[] fields() default {};

  /**
   * Get the classes representing the global assemblies defined on this Module.
   *
   * @return an array of assembly classes
   */
  @NonNull
  Class<? extends IBoundObject>[] assemblies() default {};

  /**
   * Get the classes representing the Metaschemas imported by this Module.
   *
   * @return an array of imported Metaschemas
   */
  @NonNull
  Class<? extends IBoundModule>[] imports() default {};

  @NonNull
  NsBinding[] nsBindings() default {};

  /**
   * Get any remarks for this metaschema.
   *
   * @return a markdown string or {@code "##none"} if no remarks are provided
   */
  @NonNull
  String remarks() default ModelUtil.NO_STRING_VALUE;
}
