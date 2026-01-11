/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package dev.metaschema.core.model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;

import dev.metaschema.core.util.ObjectUtils;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Provides loading capabilities for resources of type {@code T}.
 * <p>
 * Supports loading from various sources including URIs, paths, files, and URLs.
 *
 * @param <T>
 *          the type of resource loaded by this loader
 */
public interface ILoader<T> {
  /**
   * Retrieve the set of loaded resources.
   *
   * @return the set of loaded resources
   */
  @NonNull
  Collection<T> getLoadedResources();

  /**
   * Load a resource from the specified URI.
   *
   * @param resource
   *          the resource to load
   * @return the loaded instance for the specified resource
   * @throws MetaschemaException
   *           if an error occurred while processing the resource
   * @throws IOException
   *           if an error occurred parsing the resource
   */
  @NonNull
  T load(@NonNull URI resource) throws MetaschemaException, IOException;

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
  @NonNull
  T load(@NonNull Path path) throws MetaschemaException, IOException;

  /**
   * Load a resource from the specified file.
   *
   * @param file
   *          the resource to load
   * @return the loaded instance for the specified resource
   * @throws MetaschemaException
   *           if an error occurred while processing the resource
   * @throws IOException
   *           if an error occurred parsing the resource
   */
  @NonNull
  default T load(@NonNull File file) throws MetaschemaException, IOException {
    return load(ObjectUtils.notNull(file.toPath()));
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
  @NonNull
  T load(@NonNull URL url) throws MetaschemaException, IOException;
}
