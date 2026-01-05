/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind;

import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.databind.codegen.IModuleBindingGenerator;
import dev.metaschema.databind.model.IBoundModule;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A simple module loader strategy that supports optional dynamic code
 * generation.
 * <p>
 * By default, dynamic compilation is disabled. To enable dynamic compilation of
 * Metaschema modules into bound Java classes, provide an
 * {@link IModuleBindingGenerator} implementation to the constructor.
 *
 * @since 2.0.0
 */
public class SimpleModuleLoaderStrategy
    extends AbstractModuleLoaderStrategy {
  @NonNull
  private static final IModuleBindingGenerator COMPILATION_DISABLED_GENERATOR = module -> {
    throw new UnsupportedOperationException(
        "Dynamic compilation of Metaschema modules is not enabled by default." +
            " Configure a different IModuleBindingGenerator with the IModuleLoaderStrategy" +
            " used with the IBindignContext.");
  };

  @NonNull
  private final IModuleBindingGenerator generator;

  /**
   * Construct a new simple module loader strategy with dynamic compilation
   * disabled.
   */
  public SimpleModuleLoaderStrategy() {
    this(COMPILATION_DISABLED_GENERATOR);
  }

  /**
   * Construct a new simple module loader strategy with the provided binding
   * generator.
   *
   * @param generator
   *          the generator to use for dynamic module compilation
   */
  public SimpleModuleLoaderStrategy(@NonNull IModuleBindingGenerator generator) {
    this.generator = generator;
  }

  @Override
  protected Class<? extends IBoundModule> handleUnboundModule(IModule module) throws MetaschemaException {
    return generator.generate(module);
  }
}
