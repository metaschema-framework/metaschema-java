/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import java.net.URI;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A common interface for implementation classes that load data resources.
 */
public interface IResourceResolver {
  /**
   * Get the entity resolver associated with this loader.
   *
   * @return the entity resolver
   */
  @Nullable
  default IUriResolver getUriResolver() {
    // by default, do not support external URI resolution. Subclasses can override
    // this behavior
    return null;
  }

  /**
   * Resolve the provided URI, producing a resolved URI, which may point to a
   * different resource than the provided URI.
   *
   * @param uri
   *          the URI to resolve
   * @return the resulting URI
   */
  @NonNull
  default URI resolve(@NonNull URI uri) {
    IUriResolver resolver = getUriResolver();
    return resolver == null ? uri : resolver.resolve(uri);
  }
}
