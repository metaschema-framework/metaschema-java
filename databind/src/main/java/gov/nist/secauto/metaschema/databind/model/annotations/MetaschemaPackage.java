/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.annotations;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
