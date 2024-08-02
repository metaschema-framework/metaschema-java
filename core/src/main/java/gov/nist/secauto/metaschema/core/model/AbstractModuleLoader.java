/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides methods to load a Metaschema expressed in XML.
 * <p>
 * Loaded Metaschema instances are cached to avoid the need to load them for
 * every use. Any Metaschema imported is also loaded and cached automatically.
 *
 * @param <T>
 *          the Java type of the module binding
 * @param <M>
 *          the Java type of the Metaschema module loaded by this loader
 */
public abstract class AbstractModuleLoader<T, M extends IModuleExtended<M, ?, ?, ?, ?>>
    extends AbstractLoader<M>
    implements IModuleLoader<M> {
  @NonNull
  private final List<IModuleLoader.IModulePostProcessor> modulePostProcessors;

  /**
   * Construct a new Metaschema module loader, which use the provided module post
   * processors when loading a module.
   *
   * @param modulePostProcessors
   *          post processors to perform additional module customization when
   *          loading
   */
  protected AbstractModuleLoader(@NonNull List<IModuleLoader.IModulePostProcessor> modulePostProcessors) {
    this.modulePostProcessors = CollectionUtil.unmodifiableList(new ArrayList<>(modulePostProcessors));
  }

  /**
   * Get the set of module post processors associated with this loader.
   *
   * @return the set of constraints
   */
  @NonNull
  protected List<IModuleLoader.IModulePostProcessor> getModulePostProcessors() {
    return modulePostProcessors;
  }

  /**
   * Parse the {@code resource} based on the provided {@code xmlObject}.
   *
   * @param resource
   *          the URI of the resource being parsed
   * @param binding
   *          the XML beans object to parse
   * @param importedModules
   *          previously parsed Metaschema modules imported by the provided
   *          {@code resource}
   * @return the parsed resource as a Metaschema module
   * @throws MetaschemaException
   *           if an error occurred while parsing the XML beans object
   */
  @NonNull
  protected abstract M newModule(
      @NonNull URI resource,
      @NonNull T binding,
      @NonNull List<? extends M> importedModules) throws MetaschemaException;

  /**
   * Get the list of Metaschema module URIs associated with the provided binding.
   *
   * @param binding
   *          the Metaschema module binding declaring the imports
   * @return the list of Metaschema module URIs
   */
  @NonNull
  protected abstract List<URI> getImports(@NonNull T binding);

  @Override
  protected M parseResource(@NonNull URI resource, @NonNull Deque<URI> visitedResources)
      throws IOException {
    // parse this Metaschema module
    T binding = parseModule(resource);

    // now check if this Metaschema imports other metaschema
    List<URI> imports = getImports(binding);
    @NonNull Map<URI, M> importedModules;
    if (imports.isEmpty()) {
      importedModules = ObjectUtils.notNull(Collections.emptyMap());
    } else {
      try {
        importedModules = new LinkedHashMap<>();
        for (URI importedResource : imports) {
          URI resolvedResource = ObjectUtils.notNull(resource.resolve(importedResource));
          importedModules.put(resolvedResource, loadInternal(resolvedResource, visitedResources));
        }
      } catch (MetaschemaException ex) {
        throw new IOException(ex);
      }
    }

    // now create this metaschema
    Collection<M> values = importedModules.values();
    try {
      M module = newModule(resource, binding, new ArrayList<>(values));

      for (IModuleLoader.IModulePostProcessor postProcessor : getModulePostProcessors()) {
        postProcessor.processModule(module);
      }
      return module;
    } catch (MetaschemaException ex) {
      throw new IOException(ex);
    }
  }

  /**
   * Parse the provided XML resource as a Metaschema module.
   *
   * @param resource
   *          the resource to parse
   * @return the XMLBeans representation of the Metaschema module
   * @throws IOException
   *           if a parsing error occurred
   */
  @NonNull
  protected abstract T parseModule(@NonNull URI resource) throws IOException;
}
