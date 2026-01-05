/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.databind;

import dev.metaschema.core.model.IBoundObject;
import dev.metaschema.core.model.IModule;
import dev.metaschema.core.model.MetaschemaException;
import dev.metaschema.core.model.constraint.DefaultConstraintValidator;
import dev.metaschema.core.qname.IEnhancedQName;
import dev.metaschema.core.util.ExceptionUtils;
import dev.metaschema.core.util.ExceptionUtils.WrappedException;
import dev.metaschema.core.util.ObjectUtils;
import dev.metaschema.databind.IBindingContext.IBindingMatcher;
import dev.metaschema.databind.model.IBoundDefinitionModelAssembly;
import dev.metaschema.databind.model.IBoundDefinitionModelComplex;
import dev.metaschema.databind.model.IBoundModule;
import dev.metaschema.databind.model.annotations.MetaschemaAssembly;
import dev.metaschema.databind.model.annotations.MetaschemaField;
import dev.metaschema.databind.model.annotations.MetaschemaModule;
import dev.metaschema.databind.model.annotations.ModelUtil;
import dev.metaschema.databind.model.impl.DefinitionAssembly;
import dev.metaschema.databind.model.impl.DefinitionField;
import dev.metaschema.databind.model.metaschema.binding.MetaschemaModelModule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides basic module loading capabilities.
 *
 * @since 2.0.0
 */
public abstract class AbstractModuleLoaderStrategy implements IBindingContext.IModuleLoaderStrategy {
  private static final Logger LOGGER = LogManager.getLogger(DefaultConstraintValidator.class);

  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<IEnhancedQName, IBindingMatcher> bindingMatchers = new LinkedHashMap<>();
  @NonNull
  private final Map<IModule, IBoundModule> moduleToBoundModuleMap = new ConcurrentHashMap<>();
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<Class<? extends IBoundModule>, IBoundModule> modulesByClass = new HashMap<>();

  @NonNull
  private final Lock modulesLock = new ReentrantLock();
  @SuppressWarnings("PMD.UseConcurrentHashMap")
  @NonNull
  private final Map<Class<? extends IBoundObject>, IBoundDefinitionModelComplex> definitionsByClass
      = new HashMap<>();
  @NonNull
  private final Lock definitionsLock = new ReentrantLock();

  @Override
  public IBoundModule loadModule(
      @NonNull Class<? extends IBoundModule> clazz,
      @NonNull IBindingContext bindingContext) {
    return lookupInstance(clazz, bindingContext);
  }

  @Override
  @SuppressWarnings("PMD.ExceptionAsFlowControl")
  public IBoundModule registerModule(
      IModule module,
      IBindingContext bindingContext) throws MetaschemaException {
    modulesLock.lock();
    try {
      return ObjectUtils.notNull(moduleToBoundModuleMap.computeIfAbsent(module, key -> {
        assert key != null;

        IBoundModule boundModule;
        if (key instanceof IBoundModule) {
          boundModule = (IBoundModule) key;
        } else {
          try {
            Class<? extends IBoundModule> moduleClass = handleUnboundModule(key);
            boundModule = lookupInstance(moduleClass, bindingContext);
          } catch (MetaschemaException ex) {
            throw ExceptionUtils.wrap(ex);
          }
        }

        boundModule.getExportedAssemblyDefinitions().forEach(assembly -> {
          assert assembly != null;
          if (assembly.isRoot()) {
            // force the binding matchers to load
            registerBindingMatcher(assembly);
          }
        });

        return boundModule;
      }));
    } catch (WrappedException ex) {
      throw ExceptionUtils.unwrap(ex, MetaschemaException.class);
    } finally {
      modulesLock.unlock();
    }
  }

  /**
   * Handle a module that is not already bound to a Java class.
   * <p>
   * This method is called when a module is encountered that does not implement
   * {@link IBoundModule}. Implementations must determine the appropriate bound
   * module class to use for the given module.
   *
   * @param key
   *          the unbound module to handle
   * @return the class of the bound module to use
   * @throws MetaschemaException
   *           if an error occurs while determining the bound module class
   */
  @NonNull
  protected abstract Class<? extends IBoundModule> handleUnboundModule(@NonNull IModule key) throws MetaschemaException;

  /**
   * Get the Module instance for a given class annotated by the
   * {@link MetaschemaModule} annotation, instantiating it if needed.
   * <p>
   * Will also load any imported Metaschemas.
   *
   *
   * @param moduleClass
   *          the Module class
   * @param bindingContext
   *          the Metaschema binding context used to lookup binding information
   * @return the new Module instance
   */
  @NonNull
  protected IBoundModule lookupInstance(
      @NonNull Class<? extends IBoundModule> moduleClass,
      @NonNull IBindingContext bindingContext) {
    IBoundModule retval;
    modulesLock.lock();
    try {
      retval = modulesByClass.get(moduleClass);
      if (retval == null) {
        if (!moduleClass.isAnnotationPresent(MetaschemaModule.class)) {
          throw new IllegalStateException(String.format("The class '%s' is missing the '%s' annotation",
              moduleClass.getCanonicalName(), MetaschemaModule.class.getCanonicalName()));
        }

        retval = IBoundModule.newInstance(moduleClass, bindingContext, getImportedModules(moduleClass, bindingContext));
        modulesByClass.put(moduleClass, retval);
      }
    } finally {
      modulesLock.unlock();
    }
    return retval;
  }

  /**
   * Register a binding matcher for a root assembly definition.
   * <p>
   * The binding matcher is used to match document root elements to their
   * corresponding assembly definitions during deserialization.
   *
   * @param definition
   *          the root assembly definition to register a matcher for
   * @return the registered binding matcher
   * @throws IllegalArgumentException
   *           if the provided definition is not a root assembly
   */
  @NonNull
  protected IBindingMatcher registerBindingMatcher(@NonNull IBoundDefinitionModelAssembly definition) {
    IBindingMatcher retval;
    modulesLock.lock();
    try {
      if (!definition.isRoot()) {
        throw new IllegalArgumentException(
            String.format("The provided definition '%s' is not a root assembly.",
                definition.getBoundClass().getName()));
      }
      IEnhancedQName qname = definition.getRootQName();
      retval = IBindingMatcher.of(definition);
      // always replace the existing matcher to ensure the last loaded module wins
      IBindingMatcher old = bindingMatchers.put(qname, retval);
      if (old != null && !(definition.getContainingModule() instanceof MetaschemaModelModule)) {
        // FIXME: find existing causes of this in unit tests
        if (LOGGER.isDebugEnabled()) {
          LOGGER.atDebug().log("Replacing matcher for QName: {}", qname);
        }
      }

      // retval = bindingMatchers.get(definition);
      // if (retval == null) {
      // if (!definition.isRoot()) {
      // throw new IllegalArgumentException(
      // String.format("The provided definition '%s' is not a root assembly.",
      // definition.getBoundClass().getName()));
      // }
      //
      // retval = IBindingMatcher.of(definition);
      // bindingMatchers.put(definition, retval);
      // }
    } finally {
      modulesLock.unlock();
    }
    return retval;
  }

  @Override
  public final List<IBindingMatcher> getBindingMatchers() {
    modulesLock.lock();
    try {
      // make a defensive copy
      return new ArrayList<>(bindingMatchers.values());
    } finally {
      modulesLock.unlock();
    }
  }

  @NonNull
  private List<IBoundModule> getImportedModules(
      @NonNull Class<? extends IBoundModule> moduleClass,
      @NonNull IBindingContext bindingContext) {
    MetaschemaModule moduleAnnotation = moduleClass.getAnnotation(MetaschemaModule.class);

    return ObjectUtils.notNull(Arrays.stream(moduleAnnotation.imports())
        .map(clazz -> lookupInstance(ObjectUtils.requireNonNull(clazz), bindingContext))
        .collect(Collectors.toUnmodifiableList()));
  }

  @Override
  public IBoundDefinitionModelComplex getBoundDefinitionForClass(
      @NonNull Class<? extends IBoundObject> clazz,
      @NonNull IBindingContext bindingContext) {

    IBoundDefinitionModelComplex retval;
    definitionsLock.lock();
    try {
      retval = definitionsByClass.get(clazz);
      if (retval == null) {
        retval = newBoundDefinition(clazz, bindingContext);
        definitionsByClass.put(clazz, retval);
      }

      // // force loading of metaschema information to apply constraints
      // IModule module = retval.getContainingModule();
      // registerModule(module, bindingContext);
      return retval;
    } finally {
      definitionsLock.unlock();
    }
  }

  @NonNull
  private IBoundDefinitionModelComplex newBoundDefinition(
      @NonNull Class<? extends IBoundObject> clazz,
      @NonNull IBindingContext bindingContext) {
    IBoundDefinitionModelComplex retval;
    if (clazz.isAnnotationPresent(MetaschemaAssembly.class)) {
      MetaschemaAssembly annotation = ModelUtil.getAnnotation(clazz, MetaschemaAssembly.class);
      Class<? extends IBoundModule> moduleClass = annotation.moduleClass();
      IBoundModule module = loadModule(moduleClass, bindingContext);
      retval = DefinitionAssembly.newInstance(clazz, annotation, module, bindingContext);
    } else if (clazz.isAnnotationPresent(MetaschemaField.class)) {
      MetaschemaField annotation = ModelUtil.getAnnotation(clazz, MetaschemaField.class);
      Class<? extends IBoundModule> moduleClass = annotation.moduleClass();
      IBoundModule module = loadModule(moduleClass, bindingContext);
      retval = DefinitionField.newInstance(clazz, annotation, module, bindingContext);
    } else {
      throw new IllegalArgumentException(String.format("Unable to find bound definition for class '%s'.",
          clazz.getName()));
    }
    return retval;
  }
}
