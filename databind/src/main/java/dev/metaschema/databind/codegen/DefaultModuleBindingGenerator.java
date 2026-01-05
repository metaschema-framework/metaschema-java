/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind.codegen;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.model.IBoundModule;

import java.io.IOException;
import java.nio.file.Path;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Default implementation of {@link IModuleBindingGenerator} that generates and
 * compiles Java classes for a Metaschema module.
 * <p>
 * This generator creates Java source files representing the module and its
 * definitions, compiles them, and loads the resulting classes using a custom
 * class loader.
 */
public class DefaultModuleBindingGenerator implements IModuleBindingGenerator {
  @NonNull
  private final Path compilePath;

  /**
   * Construct a new binding generator that generates classes in the specified
   * directory.
   *
   * @param compilePath
   *          the directory where generated Java classes will be created and
   *          compiled
   */
  public DefaultModuleBindingGenerator(@NonNull Path compilePath) {
    this.compilePath = compilePath;
  }

  @Override
  public Class<? extends IBoundModule> generate(IModule module) throws MetaschemaException {
    ClassLoader classLoader = ModuleCompilerHelper.newClassLoader(
        compilePath,
        ObjectUtils.notNull(Thread.currentThread().getContextClassLoader()));

    IProduction production;
    try {
      production = ModuleCompilerHelper.compileMetaschema(module, compilePath);
    } catch (IOException ex) {
      throw new MetaschemaException(
          String.format("Unable to generate and compile classes for module '%s'.", module.getLocation()),
          ex);
    }

    try {
      return ObjectUtils.notNull(production.getModuleProduction(module)).load(classLoader);
    } catch (ClassNotFoundException ex) {
      throw new IllegalStateException(ex);
    }
  }

}
