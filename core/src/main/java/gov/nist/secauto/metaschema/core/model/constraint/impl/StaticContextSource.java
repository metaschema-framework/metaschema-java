/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.model.constraint.impl;

import gov.nist.secauto.metaschema.core.metapath.StaticContext;
import gov.nist.secauto.metaschema.core.model.ISource;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Implements a
 * {@link gov.nist.secauto.metaschema.core.model.ISource.SourceLocation#EXTERNAL}
 * source with an associated resource.
 */
public final class StaticContextSource implements ISource {
  @NonNull
  private static final Map<URI, StaticContextSource> sources = new HashMap<>(); // NOPMD - intentional
  @NonNull
  private static final Lock SOURCE_LOCK = new ReentrantLock();

  @NonNull
  private final StaticContext staticContext;

  /**
   * Get a new instance of an external source associated with a resource
   * {@code location}.
   *
   * @param staticContext
   *          the static Metapath context to use for compiling Metapath
   *          expressions in this source
   * @param useCached
   *          if {@code true} use a previously cached source, otherwise create a
   *          new one
   * @return the source
   */
  @NonNull
  public static ISource instance(
      @NonNull StaticContext staticContext,
      boolean useCached) {
    return useCached
        ? retrieveFromCache(staticContext)
        : new StaticContextSource(staticContext);
  }

  @NonNull
  private static StaticContextSource retrieveFromCache(@NonNull StaticContext staticContext) {
    SOURCE_LOCK.lock();
    try {
      return ObjectUtils.notNull(sources.computeIfAbsent(
          staticContext.getBaseUri(),
          uri -> new StaticContextSource(staticContext)));
    } finally {
      SOURCE_LOCK.unlock();
    }
  }

  /**
   * Construct a new source.
   *
   * @param staticContext
   *          the static Metapath context to use for compiling Metapath
   *          expressions in this source
   */
  private StaticContextSource(@NonNull StaticContext staticContext) {
    assert staticContext.getBaseUri() != null;
    this.staticContext = staticContext;
  }

  @Override
  public SourceLocation getSourceType() {
    return SourceLocation.EXTERNAL;
  }

  @Override
  public URI getSource() {
    return staticContext.getBaseUri();
  }

  @Override
  public String getLocationHint() {

    return ObjectUtils.notNull(
        // we can assume the base URI is always not null, since this is checked
        ObjectUtils.requireNonNull(staticContext.getBaseUri()).toASCIIString());
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
