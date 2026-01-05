/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import dev.metaschema.core.model.IModule;
import dev.metaschema.databind.model.IBoundModule;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Associates Metaschema module classes with a package.
 */
@Retention(RUNTIME)
@Target(PACKAGE)
public @interface MetaschemaPackage {
  /**
   * Get the metaschemas associated with this package.
   *
   * @return the classes that extend {@link IModule} or an empty array if no
   *         metaschemas are defined
   */
  Class<? extends IBoundModule>[] moduleClass() default {};
}
