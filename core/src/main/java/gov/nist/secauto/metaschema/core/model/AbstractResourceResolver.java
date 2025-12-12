/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Base implementation of {@link IResourceResolver} providing URI resolution
 * support.
 * <p>
 * This class maintains an optional {@link IUriResolver} that can be configured
 * to customize URI resolution behavior.
 */
public class AbstractResourceResolver implements IResourceResolver {
  /**
   * An {@link IUriResolver} is not provided by default.
   */
  private IUriResolver uriResolver;

  @Override
  public IUriResolver getUriResolver() {
    return uriResolver;
  }

  /**
   * Set the URI resolver for this resource resolver.
   *
   * @param uriResolver
   *          the URI resolver
   */
  public void setUriResolver(@NonNull IUriResolver uriResolver) {
    this.uriResolver = uriResolver;
  }
}
