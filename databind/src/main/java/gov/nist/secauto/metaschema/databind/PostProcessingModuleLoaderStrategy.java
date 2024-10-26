/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.databind.IBindingContext.IBindingMatcher;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelComplex;
import gov.nist.secauto.metaschema.databind.model.IBoundModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.umd.cs.findbugs.annotations.NonNull;

public class PostProcessingModuleLoaderStrategy
    implements IBindingContext.IModuleLoaderStrategy {
  @NonNull
  private final List<IModuleLoader.IModulePostProcessor> modulePostProcessors;
  private final Set<IModule> resolvedModules = new HashSet<>();
  private final Lock resolvedModulesLock = new ReentrantLock();
  private final IBindingContext.IModuleLoaderStrategy delegate;
  private final Set<IModule> postProcessedModules = new HashSet<>();
  private final Lock postProcessedModulesLock = new ReentrantLock();

  public PostProcessingModuleLoaderStrategy(
      @NonNull List<IModuleLoader.IModulePostProcessor> modulePostProcessors) {
    this(modulePostProcessors, new SimpleModuleLoaderStrategy());
  }

  public PostProcessingModuleLoaderStrategy(
      @NonNull List<IModuleLoader.IModulePostProcessor> modulePostProcessors,
      @NonNull IBindingContext.IModuleLoaderStrategy delegate) {
    this.modulePostProcessors = CollectionUtil.unmodifiableList(new ArrayList<>(modulePostProcessors));
    this.delegate = delegate;
  }

  @NonNull
  protected List<IModuleLoader.IModulePostProcessor> getModulePostProcessors() {
    return modulePostProcessors;
  }

  @Override
  public IBoundModule loadModule(Class<? extends IBoundModule> clazz, IBindingContext bindingContext) {
    return delegate.loadModule(clazz, bindingContext);
  }

  @Override
  public IBoundModule registerModule(IModule module, IBindingContext bindingContext) {
    IBoundModule retval = delegate.registerModule(module, bindingContext);

    postProcessedModulesLock.lock();
    try {
      if (!postProcessedModules.contains(retval)) {
        for (IModuleLoader.IModulePostProcessor postProcessor : getModulePostProcessors()) {
          postProcessor.processModule(retval);
        }
        postProcessedModules.add(retval);
      }
    } finally {
      postProcessedModulesLock.unlock();
    }
    return retval;
  }

  @Override
  public IBindingMatcher registerBindingMatcher(IBoundDefinitionModelAssembly definition) {
    return delegate.registerBindingMatcher(definition);
  }

  @Override
  public Collection<IBindingMatcher> getBindingMatchers() {
    return delegate.getBindingMatchers();
  }

  @Override
  public IBoundDefinitionModelComplex getBoundDefinitionForClass(
      Class<? extends IBoundObject> clazz,
      IBindingContext bindingContext) {
    IBoundDefinitionModelComplex retval = delegate.getBoundDefinitionForClass(clazz, bindingContext);
    // force loading of metaschema information to apply constraints
    IModule module = retval.getContainingModule();

    resolvedModulesLock.lock();
    try {
      if (!resolvedModules.contains(module)) {
        // add first, to avoid loops
        resolvedModules.add(module);
        for (IModuleLoader.IModulePostProcessor postProcessor : getModulePostProcessors()) {
          postProcessor.processModule(module);
        }
      }
    } finally {
      resolvedModulesLock.unlock();
    }
    return retval;
  }
}
