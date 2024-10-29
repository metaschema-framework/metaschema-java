/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.databind.codegen.IModuleBindingGenerator;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import edu.umd.cs.findbugs.annotations.NonNull;

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

  public SimpleModuleLoaderStrategy() {
    this(COMPILATION_DISABLED_GENERATOR);
  }

  public SimpleModuleLoaderStrategy(@NonNull IModuleBindingGenerator generator) {
    this.generator = generator;
  }

  @Override
  protected Class<? extends IBoundModule> handleUnboundModule(IModule module) {
    return generator.generate(module);
  }
}
