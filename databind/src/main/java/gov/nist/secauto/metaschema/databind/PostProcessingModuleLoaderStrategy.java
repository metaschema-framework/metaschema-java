/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.umd.cs.findbugs.annotations.NonNull;

class PostProcessingModuleLoaderStrategy
    extends SimpleModuleLoaderStrategy {
  @NonNull
  private final List<IModuleLoader.IModulePostProcessor> modulePostProcessors;
  private final Set<IModule> resolvedModules = new HashSet<>();
  private final Lock resolvedModulesLock = new ReentrantLock();

  protected PostProcessingModuleLoaderStrategy(
      @NonNull IBindingContext bindingContext,
      @NonNull List<IModuleLoader.IModulePostProcessor> modulePostProcessors) {
    super(bindingContext);
    this.modulePostProcessors = CollectionUtil.unmodifiableList(new ArrayList<>(modulePostProcessors));
  }

  @NonNull
  protected List<IModuleLoader.IModulePostProcessor> getModulePostProcessors() {
    return modulePostProcessors;
  }

  @Override
  public IBoundDefinitionModelComplex getBoundDefinitionForClass(@NonNull Class<? extends IBoundObject> clazz) {
    IBoundDefinitionModelComplex retval = super.getBoundDefinitionForClass(clazz);
    if (retval != null) {
      // force loading of metaschema information to apply constraints
      IModule module = retval.getContainingModule();

      try {
        resolvedModulesLock.lock();
        if (!resolvedModules.contains(module)) {
          // add first, to avoid loops
          resolvedModules.add(module);
          handleModule(module);
        }
      } finally {
        resolvedModulesLock.unlock();
      }
    }
    return retval;
  }

  private void handleModule(@NonNull IModule module) {
    for (IModuleLoader.IModulePostProcessor postProcessor : getModulePostProcessors()) {
      postProcessor.processModule(module);
    }
  }
}
