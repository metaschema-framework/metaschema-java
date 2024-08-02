/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import gov.nist.secauto.metaschema.core.util.CollectionUtil;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.NonNull;

public abstract class AbstractLoader<T> implements ILoader<T> {
  private static final Logger LOGGER = LogManager.getLogger(AbstractLoader.class);

  @NonNull
  private final Map<URI, T> cache = new LinkedHashMap<>(); // NOPMD - intentional

  @Override
  @NonNull
  public Collection<T> getLoadedResources() {
    return CollectionUtil.unmodifiableCollection(ObjectUtils.notNull(cache.values()));
  }

  /**
   * Retrieve a mapping of resource URIs to the associated loaded resource.
   *
   * @return the mapping
   */
  @NonNull
  protected Map<URI, T> getCachedEntries() {
    return CollectionUtil.unmodifiableMap(cache);
  }

  @Override
  @NonNull
  public T load(@NonNull URI resource) throws MetaschemaException, IOException {
    if (!resource.isAbsolute()) {
      throw new IllegalArgumentException(String.format("The URI '%s' must be absolute.", resource.toString()));
    }
    return loadInternal(resource, new LinkedList<>());
  }

  /**
   * Load a resource from the specified path.
   *
   * @param path
   *          the resource to load
   * @return the loaded instance for the specified resource
   * @throws MetaschemaException
   *           if an error occurred while processing the resource
   * @throws IOException
   *           if an error occurred parsing the resource
   */
  @Override
  @NonNull
  public T load(@NonNull Path path) throws MetaschemaException, IOException {
    // use toURL to normalize the URI
    return load(ObjectUtils.notNull(path.toAbsolutePath().normalize().toUri().toURL()));
  }

  /**
   * Loads a resource from the specified URL.
   *
   * @param url
   *          the URL to load the resource from
   * @return the loaded instance for the specified resource
   * @throws MetaschemaException
   *           if an error occurred while processing the resource
   * @throws IOException
   *           if an error occurred parsing the resource
   */
  @Override
  @NonNull
  public T load(@NonNull URL url) throws MetaschemaException, IOException {
    try {
      URI resource = url.toURI();
      return loadInternal(ObjectUtils.notNull(resource), new LinkedList<>());
    } catch (URISyntaxException ex) {
      // this should not happen
      LOGGER.error("Invalid url", ex);
      throw new IOException(ex);
    }
  }

  /**
   * Loads a resource from the provided URI.
   * <p>
   * If the resource imports other resources, the provided
   * {@code visitedResources} can be used to track circular imports. This is
   * useful when this method recurses into included resources.
   * <p>
   * Previously loaded resources are provided by the cache. This method will add
   * the resource to the cache after all imported resources have been loaded.
   *
   * @param resource
   *          the resource to load
   * @param visitedResources
   *          a LIFO queue representing previously visited resources in an import
   *          chain
   * @return the loaded resource
   * @throws MetaschemaException
   *           if an error occurred while processing the resource
   * @throws MalformedURLException
   *           if the provided URI is malformed
   * @throws IOException
   *           if an error occurred parsing the resource
   */
  @NonNull
  protected T loadInternal(@NonNull URI resource, @NonNull Deque<URI> visitedResources)
      throws MetaschemaException, MalformedURLException, IOException {
    // first check if the current resource has been visited to prevent cycles
    if (visitedResources.contains(resource)) {
      throw new MetaschemaException("Cycle detected in metaschema includes for '" + resource + "'. Call stack: '"
          + visitedResources.stream().map(URI::toString).collect(Collectors.joining(",")));
    }

    T retval = cache.get(resource);
    if (retval == null) {
      LOGGER.info("Loading '{}'", resource);

      try {
        visitedResources.push(resource);
        retval = parseResource(resource, visitedResources);
      } finally {
        visitedResources.pop();
      }
      cache.put(resource, retval);
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Found resource in cache '{}'", resource);
      }
    }
    return ObjectUtils.notNull(retval);
  }

  /**
   * Parse the provided {@code resource}.
   *
   * @param resource
   *          the resource to parse
   * @param visitedResources
   *          a stack representing previously parsed resources imported by the
   *          provided {@code resource}
   * @return the parsed resource
   * @throws IOException
   *           if an error occurred while parsing the resource
   */
  protected abstract T parseResource(@NonNull URI resource, @NonNull Deque<URI> visitedResources)
      throws IOException;

}
