/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.constraint.ISource;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements a
 * {@link gov.nist.secauto.metaschema.core.model.constraint.ISource.SourceType#EXTERNAL}
 * source with an associated resource.
 */
public final class ExternalSource implements ISource {
  @NonNull
  private static final Map<URI, ExternalSource> sources = new HashMap<>(); // NOPMD - intentional

  @NonNull
  private final StaticContext staticContext;

  /**
   * Get a new instance of an external source associated with a resource
   * {@code location}.
   *
   * @param staticContext
   *          the static Metapath context to use for compiling Metapath
   *          expressions in this source
   * @return the source
   */
  @NonNull
  public static ISource instance(@NonNull StaticContext staticContext) {
    ISource retval;
    synchronized (sources) {
      retval = ObjectUtils.notNull(sources.computeIfAbsent(
          staticContext.getBaseUri(),
          (uri) -> new ExternalSource(staticContext)));
    }
    return retval;
  }

  /**
   * Construct a new source.
   *
   * @param staticContext
   *          the static Metapath context to use for compiling Metapath
   *          expressions in this source
   */
  private ExternalSource(@NonNull StaticContext staticContext) {
    this.staticContext = staticContext;
  }

  @Override
  public SourceType getSourceType() {
    return SourceType.EXTERNAL;
  }

  @Override
  public URI getSource() {
    return staticContext.getBaseUri();
  }

  @Override
  public StaticContext getStaticContext() {
    return staticContext;
  }

  @Override
  public String toString() {
    return "external:" + getSource();
  }
}
