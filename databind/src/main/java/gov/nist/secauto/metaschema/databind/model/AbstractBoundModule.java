/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.AbstractModule;
import gov.nist.secauto.metaschema.core.model.IBoundObject;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.model.annotations.MetaschemaModule;
import gov.nist.secauto.metaschema.databind.model.annotations.NsBinding;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import edu.umd.cs.findbugs.annotations.NonNull;
import nl.talsmasoftware.lazy4j.Lazy;

public abstract class AbstractBoundModule
    extends AbstractModule<
        IBoundModule,
        IBoundDefinitionModelComplex,
        IBoundDefinitionFlag,
        IBoundDefinitionModelField<?>,
        IBoundDefinitionModelAssembly>
    implements IBoundModule {
  @NonNull
  private final IBindingContext bindingContext;
  @NonNull
  private final Lazy<Map<QName, IBoundDefinitionModelAssembly>> assemblyDefinitions;
  @NonNull
  private final Lazy<Map<QName, IBoundDefinitionModelField<?>>> fieldDefinitions;
  @NonNull
  private final Lazy<StaticContext> staticContext;

  /**
   * Create a new Module instance for a given class annotated by the
   * {@link MetaschemaModule} annotation.
   * <p>
   * Will also load any imported Metaschemas.
   *
   *
   * @param clazz
   *          the Module class
   * @param bindingContext
   *          the Module binding context
   * @return the new Module instance
   */
  @NonNull
  public static IBoundModule createInstance(
      @NonNull Class<? extends IBoundModule> clazz,
      @NonNull IBindingContext bindingContext) {

    if (!clazz.isAnnotationPresent(MetaschemaModule.class)) {
      throw new IllegalStateException(String.format("The class '%s' is missing the '%s' annotation",
          clazz.getCanonicalName(), MetaschemaModule.class.getCanonicalName()));
    }

    MetaschemaModule moduleAnnotation = clazz.getAnnotation(MetaschemaModule.class);

    List<IBoundModule> importedModules;
    if (moduleAnnotation.imports().length > 0) {
      importedModules = new ArrayList<>(moduleAnnotation.imports().length);
      for (Class<? extends IBoundModule> importClass : moduleAnnotation.imports()) {
        assert importClass != null;
        IBoundModule moduleImport = bindingContext.registerModule(importClass);
        importedModules.add(moduleImport);
      }
    } else {
      importedModules = CollectionUtil.emptyList();
    }
    return createInstance(clazz, bindingContext, importedModules);
  }

  @NonNull
  private static IBoundModule createInstance(
      @NonNull Class<? extends IBoundModule> clazz,
      @NonNull IBindingContext bindingContext,
      @NonNull List<? extends IBoundModule> importedModules) {

    Constructor<? extends IBoundModule> constructor;
    try {
      constructor = clazz.getDeclaredConstructor(List.class, IBindingContext.class);
    } catch (NoSuchMethodException ex) {
      throw new IllegalArgumentException(ex);
    }

    try {
      return ObjectUtils.notNull(constructor.newInstance(importedModules, bindingContext));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

  /**
   * Construct a new Module instance.
   *
   * @param importedModules
   *          Module imports associated with the Metaschema module
   * @param bindingContext
   *          the Module binding context
   */
  protected AbstractBoundModule(
      @NonNull List<? extends IBoundModule> importedModules,
      @NonNull IBindingContext bindingContext) {
    super(importedModules);
    this.bindingContext = bindingContext;
    this.assemblyDefinitions = ObjectUtils.notNull(Lazy.lazy(() -> Arrays.stream(getAssemblyClasses())
        .map(clazz -> {
          assert clazz != null;
          return (IBoundDefinitionModelAssembly) ObjectUtils
              .requireNonNull(bindingContext.getBoundDefinitionForClass(clazz));
        })
        .collect(Collectors.toUnmodifiableMap(
            IBoundDefinitionModelAssembly::getDefinitionQName,
            Function.identity()))));
    this.fieldDefinitions = ObjectUtils.notNull(Lazy.lazy(() -> Arrays.stream(getFieldClasses())
        .map(clazz -> {
          assert clazz != null;
          return (IBoundDefinitionModelField<?>) ObjectUtils
              .requireNonNull(bindingContext.getBoundDefinitionForClass(clazz));
        })
        .collect(Collectors.toUnmodifiableMap(
            IBoundDefinitionModelField::getDefinitionQName,
            Function.identity()))));
    this.staticContext = ObjectUtils.notNull(Lazy.lazy(() -> {
      StaticContext.Builder builder = StaticContext.builder()
          .defaultModelNamespace(getXmlNamespace());

      getNamespaceBindings()
          .forEach(
              (prefix, ns) -> builder.namespace(
                  ObjectUtils.requireNonNull(prefix),
                  ObjectUtils.requireNonNull(ns)));
      return builder.build();
    }));
  }

  @Override
  public StaticContext getModuleStaticContext() {
    return ObjectUtils.notNull(staticContext.get());
  }

  @Override
  @NonNull
  public IBindingContext getBindingContext() {
    return bindingContext;
  }

  @Override
  public Map<String, String> getNamespaceBindings() {
    return ObjectUtils.notNull(Arrays.stream(getNsBindings())
        .collect(Collectors.toMap(
            NsBinding::prefix,
            NsBinding::uri,
            (v1, v2) -> v2,
            LinkedHashMap::new)));
  }

  @SuppressWarnings({ "null" })
  @NonNull
  protected NsBinding[] getNsBindings() {
    return getClass().isAnnotationPresent(MetaschemaModule.class)
        ? getClass().getAnnotation(MetaschemaModule.class).nsBindings()
        : (NsBinding[]) Array.newInstance(NsBinding.class, 0);
  }

  /**
   * Get the assembly instance annotations associated with this bound choice
   * group.
   *
   * @return the annotations
   */
  @SuppressWarnings({ "null", "unchecked" })
  @NonNull
  protected Class<? extends IBoundObject>[] getAssemblyClasses() {
    return getClass().isAnnotationPresent(MetaschemaModule.class)
        ? getClass().getAnnotation(MetaschemaModule.class).assemblies()
        : (Class<? extends IBoundObject>[]) Array.newInstance(Class.class, 0);
  }

  /**
   * Get the field instance annotations associated with this bound choice group.
   *
   * @return the annotations
   */
  @SuppressWarnings({ "null", "unchecked" })
  @NonNull
  protected Class<? extends IBoundObject>[] getFieldClasses() {
    return getClass().isAnnotationPresent(MetaschemaModule.class)
        ? getClass().getAnnotation(MetaschemaModule.class).fields()
        : (Class<? extends IBoundObject>[]) Array.newInstance(Class.class, 0);
  }

  /**
   * Get the mapping of assembly definition effective name to definition.
   *
   * @return the mapping
   */
  protected Map<QName, IBoundDefinitionModelAssembly> getAssemblyDefinitionMap() {
    return assemblyDefinitions.get();
  }

  @SuppressWarnings("null")
  @Override
  public Collection<IBoundDefinitionModelAssembly> getAssemblyDefinitions() {
    return getAssemblyDefinitionMap().values();
  }

  @Override
  public IBoundDefinitionModelAssembly getAssemblyDefinitionByName(@NonNull QName name) {
    return getAssemblyDefinitionMap().get(name);
  }

  /**
   * Get the mapping of field definition effective name to definition.
   *
   * @return the mapping
   */
  protected Map<QName, IBoundDefinitionModelField<?>> getFieldDefinitionMap() {
    return fieldDefinitions.get();
  }

  @SuppressWarnings("null")
  @Override
  public Collection<IBoundDefinitionModelField<?>> getFieldDefinitions() {
    return getFieldDefinitionMap().values();
  }

  @Override
  public IBoundDefinitionModelField<?> getFieldDefinitionByName(@NonNull QName name) {
    return getFieldDefinitionMap().get(name);
  }

  @SuppressWarnings("null")
  @Override
  public Collection<IBoundDefinitionFlag> getFlagDefinitions() {
    // Flags are always inline, so they do not have separate definitions
    return Collections.emptyList();
  }

  @Override
  public IBoundDefinitionFlag getFlagDefinitionByName(@NonNull QName name) {
    // Flags are always inline, so they do not have separate definitions
    return null;
  }
}
