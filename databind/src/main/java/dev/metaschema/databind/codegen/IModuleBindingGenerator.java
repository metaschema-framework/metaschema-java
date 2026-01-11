/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.databind.model.IBoundModule;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A functional interface for generating bound Java classes from a Metaschema
 * module.
 * <p>
 * Implementations of this interface are responsible for generating, compiling,
 * and loading Java classes that represent the module and its definitions.
 */
@FunctionalInterface
public interface IModuleBindingGenerator {
  /**
   * Generate bound Java classes for the provided Metaschema module.
   *
   * @param module
   *          the Metaschema module to generate classes for
   * @return the generated bound module class
   * @throws MetaschemaException
   *           if an error occurs during generation or compilation
   */
  @NonNull
  Class<? extends IBoundModule> generate(@NonNull IModule module) throws MetaschemaException;
}
