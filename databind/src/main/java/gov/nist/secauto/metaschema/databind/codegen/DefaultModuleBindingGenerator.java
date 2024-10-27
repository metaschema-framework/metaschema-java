/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.metapath.MetapathException;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import java.io.IOException;
import java.nio.file.Path;

import edu.umd.cs.findbugs.annotations.NonNull;

public class DefaultModuleBindingGenerator implements IModuleBindingGenerator {
  @NonNull
  private final Path compilePath;

  public DefaultModuleBindingGenerator(@NonNull Path compilePath) {
    this.compilePath = compilePath;
  }

  @Override
  public Class<? extends IBoundModule> generate(IModule module) {
    ClassLoader classLoader = ModuleCompilerHelper.newClassLoader(
        compilePath,
        ObjectUtils.notNull(Thread.currentThread().getContextClassLoader()));

    IProduction production;
    try {
      production = ModuleCompilerHelper.compileMetaschema(module, compilePath);
    } catch (IOException ex) {
      throw new MetapathException(
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
