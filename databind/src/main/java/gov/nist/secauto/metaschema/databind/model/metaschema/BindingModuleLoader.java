/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.model.metaschema;

import gov.nist.secauto.metaschema.core.configuration.IConfiguration;
import gov.nist.secauto.metaschema.core.configuration.IMutableConfiguration;
import gov.nist.secauto.metaschema.core.model.AbstractModuleLoader;
import gov.nist.secauto.metaschema.core.model.IModuleLoader;
import gov.nist.secauto.metaschema.core.model.MetaschemaException;
import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.io.DeserializationFeature;
import gov.nist.secauto.metaschema.databind.io.IBoundLoader;
import gov.nist.secauto.metaschema.databind.model.IBoundDefinitionModelAssembly;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.METASCHEMA;
import gov.nist.secauto.metaschema.databind.model.binding.metaschema.METASCHEMA.Import;
import gov.nist.secauto.metaschema.databind.model.metaschema.impl.BindingModule;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

public class BindingModuleLoader
    extends AbstractModuleLoader<METASCHEMA, IBindingMetaschemaModule>
    implements IMutableConfiguration<DeserializationFeature<?>> {

  @NonNull
  private final IBindingContext bindingContext;
  private IBoundLoader loader;

  /**
   * Construct a new Metaschema loader.
   *
   * @param bindingContext
   *          the Metaschema binding context used to load bound resources
   */
  public BindingModuleLoader(@NonNull IBindingContext bindingContext) {
    this(bindingContext, CollectionUtil.emptyList());
  }

  /**
   * Construct a new Metaschema loader, which use the provided module post
   * processors when loading a module.
   *
   * @param bindingContext
   *          the Metaschema binding context used to load bound resources
   * @param modulePostProcessors
   *          post processors to perform additional module customization when
   *          loading
   */
  public BindingModuleLoader(
      @NonNull IBindingContext bindingContext,
      @NonNull List<IModuleLoader.IModulePostProcessor> modulePostProcessors) {
    super(modulePostProcessors);
    this.bindingContext = bindingContext;
  }

  @Override
  protected IBindingMetaschemaModule newModule(
      URI resource,
      METASCHEMA binding,
      List<? extends IBindingMetaschemaModule> importedModules)
      throws MetaschemaException {
    return new BindingModule(
        resource,
        ObjectUtils.notNull(
            (IBoundDefinitionModelAssembly) getLoader().getBindingContext()
                .getBoundDefinitionForClass(METASCHEMA.class)),
        binding,
        importedModules);
  }

  @Override
  protected List<URI> getImports(METASCHEMA binding) {
    return ObjectUtils.notNull(binding.getImports().stream()
        .map(Import::getHref)
        .collect(Collectors.toUnmodifiableList()));
  }

  @Override
  protected METASCHEMA parseModule(URI resource) throws IOException {
    return getLoader().load(METASCHEMA.class, resource);
  }

  protected IBoundLoader getLoader() {
    synchronized (this) {
      if (this.loader == null) {
        this.loader = bindingContext.newBoundLoader();
      }
      return this.loader;
    }
  }

  @Override
  public boolean isFeatureEnabled(DeserializationFeature<?> feature) {
    return getLoader().isFeatureEnabled(feature);
  }

  @Override
  public Map<DeserializationFeature<?>, Object> getFeatureValues() {
    return getLoader().getFeatureValues();
  }

  @Override
  public IMutableConfiguration<DeserializationFeature<?>>
      applyConfiguration(IConfiguration<DeserializationFeature<?>> other) {
    return getLoader().applyConfiguration(other);
  }

  @Override
  public IMutableConfiguration<DeserializationFeature<?>> set(DeserializationFeature<?> feature, Object value) {
    return getLoader().set(feature, value);
  }

  public void allowEntityResolution() {
    enableFeature(DeserializationFeature.DESERIALIZE_XML_ALLOW_ENTITY_RESOLUTION);
  }
}
