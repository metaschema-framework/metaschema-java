/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind;

import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.databind.IBindingContext.IBindingMatcher;
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

/**
 * A module loader strategy that applies post-processors to loaded modules.
 * <p>
 * This strategy wraps another {@link IBindingContext.IModuleLoaderStrategy} and
 * ensures that configured {@link IModuleLoader.IModulePostProcessor} instances
 * are invoked on each module before it is registered. Post-processing is
 * applied only once per module, even if the module is referenced multiple
 * times.
 *
 * @since 2.0.0
 */
public class PostProcessingModuleLoaderStrategy
    implements IBindingContext.IModuleLoaderStrategy {
  @NonNull
  private final List<IModuleLoader.IModulePostProcessor> modulePostProcessors;
  private final IBindingContext.IModuleLoaderStrategy delegate;
  private final Set<IModule> postProcessedModules = new HashSet<>();
  private final Lock postProcessedModulesLock = new ReentrantLock();

  /**
   * Construct a new post-processing module loader strategy using the default
   * delegate strategy.
   *
   * @param modulePostProcessors
   *          the post-processors to apply to loaded modules
   */
  public PostProcessingModuleLoaderStrategy(
      @NonNull List<IModuleLoader.IModulePostProcessor> modulePostProcessors) {
    this(modulePostProcessors, new SimpleModuleLoaderStrategy());
  }

  /**
   * Construct a new post-processing module loader strategy with a custom
   * delegate.
   *
   * @param modulePostProcessors
   *          the post-processors to apply to loaded modules
   * @param delegate
   *          the delegate strategy to use for actual module loading and
   *          registration
   */
  public PostProcessingModuleLoaderStrategy(
      @NonNull List<IModuleLoader.IModulePostProcessor> modulePostProcessors,
      @NonNull IBindingContext.IModuleLoaderStrategy delegate) {
    this.modulePostProcessors = CollectionUtil.unmodifiableList(new ArrayList<>(modulePostProcessors));
    this.delegate = delegate;
  }

  /**
   * Get the configured module post-processors.
   *
   * @return an unmodifiable list of post-processors
   */
  @NonNull
  protected List<IModuleLoader.IModulePostProcessor> getModulePostProcessors() {
    return modulePostProcessors;
  }

  @Override
  public IBoundModule loadModule(Class<? extends IBoundModule> clazz, IBindingContext bindingContext) {
    return delegate.loadModule(clazz, bindingContext);
  }

  @Override
  public void postProcessModule(IModule module, IBindingContext bindingContext) {
    processModule(module);
    delegate.postProcessModule(module, bindingContext);
  }

  @Override
  public IBoundModule registerModule(IModule module, IBindingContext bindingContext) throws MetaschemaException {
    IBoundModule boundModule;
    postProcessedModulesLock.lock();
    try {
      // process before registering
      processModule(module);

      boundModule = delegate.registerModule(module, bindingContext);

      // ensure the resulting bound module is not processed again
      postProcessedModules.add(boundModule);
    } finally {
      postProcessedModulesLock.unlock();
    }
    return boundModule;
  }

  /**
   * Perform post-processing on the provided module.
   *
   * @param module
   *          the module to post process
   */
  protected void processModule(@NonNull IModule module) {
    postProcessedModulesLock.lock();
    try {
      if (!postProcessedModules.contains(module)) {
        for (IModuleLoader.IModulePostProcessor postProcessor : getModulePostProcessors()) {
          postProcessor.processModule(module);
        }
        postProcessedModules.add(module);
      }
    } finally {
      postProcessedModulesLock.unlock();
    }
  }

  @Override
  public Collection<IBindingMatcher> getBindingMatchers() {
    return delegate.getBindingMatchers();
  }

  @Override
  public IBoundDefinitionModelComplex getBoundDefinitionForClass(
      Class<? extends IBoundObject> clazz,
      IBindingContext bindingContext) {
    return delegate.getBoundDefinitionForClass(clazz, bindingContext);
  }
}
